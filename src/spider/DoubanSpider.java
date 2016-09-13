package spider;

import org.apache.http.*;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import pojo.CnkiResult;
import pojo.SearchResult;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;


/**
 * Created by Administrator on 2015/9/5.
 */
public class DoubanSpider {
    String cookieStr;

    public Double[] requestDouban(SearchResult searchResult) throws IOException {
        Double[] returns = new Double[2];
        Double persons = 0.0;
        Double commentRes = 0.0;
        //格式化查询条件
        String[] parsedTitleAuthor = formatSearchResult(searchResult);
        String searchTitle = parsedTitleAuthor[0];
        String searchAuthor = parsedTitleAuthor[1];
        String searchSpareTitle = parsedTitleAuthor[2];
        String searchSpareAuthor = parsedTitleAuthor[3];
        String query = URLEncoder.encode(searchTitle, "utf-8");
        String URL = "https://book.douban.com/subject_search?cat=1001&search_text=" + query;
        //拿到响应
        String resText = getEntity(URL, "www.douban.com");
        System.out.println("---开始----");
        System.out.println("作者：" + searchAuthor + "   书名:" + searchTitle + "    出版社:" + searchResult.getPublisher() + "    出版时间:" + searchResult.getPubTime());
        Document doc = Jsoup.parse(resText);
        Elements titleNodes = doc.select("h2[class] a");
        for (int i = 0; i < titleNodes.size(); i++) {
            //书名节点
            Element titleNode = titleNodes.get(i);
            String titleNodeStr = titleNode.attr("title");
            titleNodeStr.replace("“", "\"");
            titleNodeStr.replace("”", "\"");
            // 匹配到书名之后，开始匹配作者，出版社，时间
            // 同时匹配备用书名和正式书名
            String matchedTitle = null;
            if (titleNodeStr.indexOf(searchTitle) != -1) {
                matchedTitle = searchTitle;
            } else if (searchSpareTitle != null && titleNodeStr.indexOf(searchSpareTitle) != -1) {
                matchedTitle = searchSpareTitle;
            }
            if (matchedTitle != null) {
                Element otherInfo = titleNode.parent().nextElementSibling();
                String[] infos = otherInfo.text().trim().split(" / ");
                //格式化作者、出版社、出版时间等信息
                String[] parsedInfos = getInfos(infos);
                String author = parsedInfos[0];
                String publisher = parsedInfos[1];
                Integer publishTime = null;
                if(parsedInfos[2] != null){
                   publishTime = Integer.parseInt(parsedInfos[2]);
                }
                //允许作者、出版时间为空，不为空则应该包含查询条件
                //同时匹配备用作者和正式作者
                String matchedAuthor = null;
                if(author == null || author.indexOf(searchAuthor) != -1 || searchAuthor.contains(author)){
                    matchedAuthor = searchAuthor;
                }
                //如果查到的作者为空就不需要匹配备用作者了，不为空，则需要。代码逻辑不严密但没有错误
                if(author == null || (searchSpareAuthor != null && author.indexOf(searchSpareAuthor) != -1)){
                    matchedAuthor = searchSpareAuthor;
                }
                //如果查到的作者为空是合法的，查到的作者不为空，matchedAuthor不为空才能放行，因为如果为空的话，说明作者匹配失败了
                if ((author == null || matchedAuthor != null)
                        && (publishTime == null || publishTime.equals(searchResult.getPubTime()))) {
                    System.out.println("匹配成功:" + titleNode.attr("title") + "、" + author + "、" + publishTime + "、" + publisher);
                    if (author == null && publishTime == null) {
                        System.out.println("------------[失败]作者和出版社都没有获取到，待确认-----------------");
                    }
                    //拉取评价分数和评价人数
                    Element commentNode = otherInfo.nextElementSibling();
                    String detailURL = titleNode.attr("href") + "collections";
                    returns = getCommentInfo(commentNode, detailURL);
                    return returns;
                } else {
                    if (author != null && matchedAuthor != null && author.indexOf(matchedAuthor) == -1) {
                        System.out.println("-----------------作者匹配失败：" + author);
                    }
                    if (publishTime != null && !publishTime.equals(searchResult.getPubTime())) {
                        System.out.println("-----------------出版社匹配失败：" + publisher);
                    }
                }
            }
        }
        System.out.println("-------------------书名匹配失败-----------------");
        returns[0] = null;
        returns[1] = null;
        return returns;
    }

    //可以处理重定向的getEntity
    public String getEntity(String url, String host) throws IOException {
        cookieStr = "bid=\"l2mUKjZAzCE\"; _pk_ref.100001.8cb4=%5B%22%22%2C%22%22%2C1441394136%2C%22https%3A%2F%2Fwww.baidu.com%2Flink%3Furl%3DvPTTDPLg_sx_mUhxrmx7dYLZamRP9SbODNBBFnW768q%26wd%3D%26eqid%3Da28bf57500137be00000000355e9edd4%22%5D; __utmt=1; __utma=30149280.1984903755.1439951905.1439996352.1441394137.3; __utmb=30149280.1.10.1441394137; __utmc=30149280; __utmz=30149280.1441394137.3.3.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; ll=\"118281\"; _pk_id.100001.8cb4=face501a800a3d08.1439951477.3.1441394145.1439996352.; _pk_ses.100001.8cb4=*";
        CnkiResult cnkiResult = new CnkiResult();
        String resText = "";
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setRedirectStrategy(new RedirectStrategy() { //设置重定向处理方式

            @Override
            public boolean isRedirected(HttpRequest arg0,
                                        HttpResponse arg1, HttpContext arg2)
                    throws ProtocolException {

                return false;
            }

            @Override
            public HttpUriRequest getRedirect(HttpRequest arg0,
                                              HttpResponse arg1, HttpContext arg2)
                    throws ProtocolException {

                return null;
            }
        });
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        HttpEntity entity = null;
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cookie", cookieStr);
        httpGet.setHeader("Host", host);
        httpGet.setHeader("Referer", "http://book.douban.com");
        response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
            Header[] headers = response.getHeaders("Location");
            if (headers != null && headers.length > 0) {
                String redirectUrl = headers[0].getValue();
                System.out.println("重定向的URL:" + redirectUrl);
                redirectUrl = redirectUrl.replace(" ", "%20");
//                redirectUrl += "/collections";
                resText = getEntity(redirectUrl, "book.douban.com");
            }
        } else if (statusCode == HttpStatus.SC_OK) {
            entity = response.getEntity();
            resText = EntityUtils.toString(entity, "utf-8");
        } else {
            System.out.println("状态码：" + statusCode);
        }
        return resText;
    }

    //豆瓣查询条件格式化——处理空格等各种标点符号
    public String[] formatSearchResult(SearchResult searchResult) {
        //去空格
        String title = searchResult.getTitle();
        String author = searchResult.getAuthor();
        String spareTitle = searchResult.getSpareTitle();
        String spareAuthor = searchResult.getSpareAuthor();
        while (title.indexOf(" ") != -1) {
            title = title.substring(0, title.indexOf(" ")) + title.substring(title.indexOf(" ") + 1, title.length());
        }
        //引号改英文
        title = title.replace("“", "\"");
        title = title.replace("”", "\"");
        //截取冒号，破折号之前的书名
        if (title.indexOf(":") != -1) {
            title = title.substring(0, title.indexOf(":"));
        }
        if (title.indexOf("•") != -1) {
            title = title.substring(0, title.indexOf("•"));
        }
        if (title.indexOf("——") != -1) {
            title = title.substring(0, title.indexOf("——"));
        }
        if (title.indexOf("--") != -1) {
            title = title.substring(0, title.indexOf("--"));
        }
        if (title.indexOf("·") != -1) {
            title = title.substring(0, title.indexOf("·"));
        } else if (title.indexOf("、") != -1) {
            title = title.substring(0, title.indexOf("、"));
        }
        //作者截断
        if (author.indexOf("•") != -1) {
            author = author.substring(0, author.indexOf("•"));
        } else if (author.indexOf("·") != -1) {
            author = author.substring(0, author.indexOf("·"));
        }
        //备用书名处理
        if (spareTitle != null) {
            while (spareTitle.indexOf(" ") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf(" ")) + spareTitle.substring(spareTitle.indexOf(" ") + 1, spareTitle.length());
            }
            //引号改英文
            spareTitle = spareTitle.replace("“", "\"");
            spareTitle = spareTitle.replace("”", "\"");
            //截取冒号，破折号之前的书名
            if (spareTitle.indexOf(":") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf(":"));
            }
            if (spareTitle.indexOf("•") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("•"));
            }
            if (spareTitle.indexOf("——") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("——"));
            }
            if (spareTitle.indexOf("--") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("--"));
            }
            if (spareTitle.indexOf("·") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("·"));
            } else if (spareTitle.indexOf("、") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("、"));
            }
        }
        if (spareAuthor != null) {
            //备用作者截断
            if (spareAuthor.indexOf("•") != -1) {
                spareAuthor = spareAuthor.substring(0, spareAuthor.indexOf("•"));
            } else if (spareAuthor.indexOf("·") != -1) {
                spareAuthor = spareAuthor.substring(0, spareAuthor.indexOf("·"));
            }
        }
        String[] results = new String[]{title, author, spareTitle, spareAuthor};
        return results;
    }

    //豆瓣文档解析——分解爬取到的作者、出版社、时间等信息
    public String[] getInfos(String[] infos) {
        String author = null;
        String publisher = null;
        String publishTime = null;
        // 有译者的，xls中的负责人都是译者
        // 作者、出版时间、价格或者作者、译者、价格（这种只能手动处理了）
        if (infos.length == 3) {
            try {
                if (infos[0].contains("印书馆") || infos[0].contains("书局") || infos[0].contains("出版社")) {
                    publisher = infos[0];
                } else if (infos[0].contains("-") && (infos[0].contains("19") || infos[0].contains("20"))) {
                    publishTime = infos[0].substring(0, 4);
                } else {
                    author = infos[0];
                }
                if (infos[1].contains("印书馆") || infos[1].contains("书局") || infos[1].contains("出版社")) {
                    publisher = infos[1];
                } else if (infos[1].contains("-") && (infos[0].contains("19") || infos[0].contains("20"))) {
                    publishTime = infos[1].substring(0, 4);
                } else {
                    author = infos[1];
                }
                if(publishTime == null && infos[2].contains("-")){
                    publishTime = infos[2].substring(0, 4);
                }
            } catch (NumberFormatException e) {
                System.out.println("-----------------线上的出版社、出版时间格式匹配失败,格式解析错误:");
                for (int j = 0; j < infos.length; j++) {
                    String info = infos[j];
                    System.out.print(info);
                }
                System.out.println();
            }
            //作者、出版社、出版时间、价格或作者、译者、出版时间、价格或作者、译者、出版社、出版时间（这种只能手动处理了）
        } else if (infos.length == 4) {
            try {
                author = infos[0];
                if (infos[1].contains("印书馆") || infos[1].contains("书局") || infos[1].contains("出版社")) {
                    publisher = infos[1];
                } else {
                    author = infos[1];
                }
                if (infos[2].contains("印书馆") || infos[2].contains("书局") || infos[2].contains("出版社")) {
                    publisher = infos[2];
                } else {
                    publishTime = infos[2].substring(0, 4);
                }
                if(publishTime == null && infos[3].contains("-") && (infos[0].contains("19") || infos[0].contains("20"))){
                    publishTime = infos[3].substring(0, 4);
                }
            } catch (NumberFormatException e) {
                System.out.println("-----------------线上的出版社、出版时间格式匹配失败,格式解析错误");
                for (int j = 0; j < infos.length; j++) {
                    String info = infos[j];
                    System.out.print(info);
                }
                System.out.println();
            }
            //出版时间和价格，作者和出版社，时间和出版社，时间和价格等等各种
            //作者不可能在第二个位置，价格应该也不会在第一个位置
        } else if (infos.length == 2) {
            try {
                if (infos[0].contains("印书馆") || infos[0].contains("书局") || infos[0].contains("出版社")) {
                    publisher = infos[0];
                } else if (infos[1].contains("-") && (infos[0].contains("19") || infos[0].contains("20"))) {
                    publishTime = infos[0].substring(0, 4);
                } else {
                    author = infos[0];
                }
                if (infos[1].contains("印书馆") || infos[1].contains("书局") || infos[1].contains("出版社")) {
                    publisher = infos[1];
                } else if (infos[1].contains("-") && (infos[0].contains("19") || infos[0].contains("20"))) {
                    publishTime = infos[1].substring(0, 4);
                }
            } catch (NumberFormatException e) {
                System.out.println("-----------------线上的出版社、出版时间格式匹配失败,格式解析错误");
                for (int j = 0; j < infos.length; j++) {
                    String info = infos[j];
                    System.out.print(info);
                }
                System.out.println();
            }
            //作者、译者、出版社、出版时间、价格
        } else if (infos.length == 5) {
            try {
                author = infos[1];
                publisher = infos[2];
                publishTime = infos[3].substring(0, 4);
            } catch (NumberFormatException e) {
                System.out.println("-----------------线上的出版社、出版时间格式匹配失败,格式解析错误");
            }
        } else {
            System.out.println("-----------------线上的出版社、出版时间格式匹配失败，拉到的数组长度为:" + infos.length);
        }
        String[] results = new String[]{author, publisher, publishTime};
        return results;
    }

    //豆瓣评论数据解析——拉取评论数和评论人数
    public Double[] getCommentInfo(Element commentNode, String detailURL) throws IOException {
        Double[] results = new Double[2];
        Element commentTextNode = commentNode.select("span[class=pl]").get(0);
        String commentText = commentTextNode.text();
        if (commentText.contains("少于")) {
            //请求书目详情页面
            System.out.println("----------------跳转详情页面------------------");
            String detailResText = getEntity(detailURL, "book.douban.com");
            Document detailDoc = Jsoup.parse(detailResText);
            commentTextNode = detailDoc.select("div[class=article] h2 span").get(0);
            commentText = commentTextNode.text();
            Element outerCommentScoreNode = detailDoc.select("div[class=rating_detail_star]").get(0);
            Double commentScore = 0.0;
            for (int j = 1; j <= 5; j++) {
                Element innerCommentScoreNode = outerCommentScoreNode.select("span[class=stars" + j + " starstop]").get(0);
                String innerCommentPercent = ((TextNode) innerCommentScoreNode.nextElementSibling().nextSibling()).text().trim();
                Double percent = Double.parseDouble(innerCommentPercent.substring(0, innerCommentPercent.indexOf("%"))) / 100;
                commentScore += percent * j;
            }
            results[0] = Double.parseDouble(commentText.substring(0, commentText.indexOf("人")));
            results[1] = commentScore;
            System.out.println("人数：" + results[0] + "      评分：" + results[1]);
        } else if (commentText.contains("无人")) {
            results[0] = null;
            results[1] = null;
            System.out.println("无人评价");
        } else {
            //无需请求详情页面
            results[0] = Double.parseDouble(commentText.substring(1, commentText.indexOf("人评价")));
            results[1] = Double.parseDouble(commentNode.select("span[class=rating_nums]").get(0).text());
            System.out.println("人数：" + results[0] + "      评分：" + results[1]);
        }
        return results;
    }

    //单元测试，可用于验证个别的书目的查询
    public static void main(String[] args) throws IOException {
        DoubanSpider douban = new DoubanSpider();
        SearchResult searchResult = new SearchResult();
        searchResult.setAuthor("赵季");
        searchResult.setSpareAuthor("");
        searchResult.setSpareTitle("《箕雅》五百诗人本事辑考");
        searchResult.setTitle("箕雅五百诗人本事辑考");
        searchResult.setPublisher("人民文学出版社");
        searchResult.setPubTime(2013);
        Double[] results = douban.requestDouban(searchResult);
        System.out.println(results);
    }
}
