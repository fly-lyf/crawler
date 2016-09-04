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

import javax.xml.soap.Text;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/9/5.
 */
public class DoubanSpider {
    String cookieStr;

    public Double[] requestDouban(SearchResult searchResult) throws IOException {
        Double[] returns = new Double[2];
        Double persons = 0.0;
        Double commentRes = 0.0;
        //爬虫
        String query = "q=" + URLEncoder.encode(searchResult.getTitle(), "utf-8");
        String URL = "http://www.douban.com/search?cat=1001&" + query;
        String resText = getEntity(URL, "www.douban.com");
        //匹配书名
        System.out.println("---开始----");
        System.out.println("作者：" + searchResult.getAuthor() + "   书名:" + searchResult.getTitle() + "    出版社:"+ searchResult.getPublisher() + "    出版时间:"+ searchResult.getPubTime());
        Document doc = Jsoup.parse(resText);

        Elements titleNodes = doc.select("h2[class] a");
        for (int i = 0; i < titleNodes.size(); i++) {
            Element titleNode = titleNodes.get(i);
            String title = titleNode.attr("title");
            if (titleNode.select("span").size() > 0) {
                title += titleNode.select("span").get(0).text().trim();
            }
            //去空格
            while(title.indexOf(" ") != -1){
                title = title.substring(0, title.indexOf(" ")) + title.substring(title.indexOf(" ")+1, title.length());
            }
            //截取冒号，破折号之前的书名
            if(title.indexOf(":") != -1){
                title = title.substring(0, title.indexOf(":"));
            }else if(title.indexOf("—") != -1){
                title = title.substring(0, title.indexOf("—"));
            }
            // 匹配到书名之后，开始匹配作者，出版社，时间
            // 处理中英文标点
            if(title.contains(searchResult.getTitle())){
                Element otherInfo = titleNode.parent().nextElementSibling();
                String[] infos = otherInfo.text().trim().split(" / ");
                String author = infos[0];
                String publisher;
                Integer publishTime = 0;
                // 没有出版社的情况
                if(infos.length == 3){
                    publishTime = Integer.parseInt(infos[1].substring(0, 4));
                }else if(infos.length == 4){
                    publisher = infos[1];
                    publishTime = Integer.parseInt(infos[2].substring(0, 4));
                }else{
                    System.out.println("-----------------线上的出版社、出版时间格式不对-----------------------------");
                    returns[0] = null;
                    returns[1] = null;
                    return returns;
                }

                if(author.equals(searchResult.getAuthor()) && publishTime.equals(searchResult.getPubTime())){
                    System.out.println("匹配成功");
                    //拉取评价分数和评价人数
                    Element commentNode = otherInfo.nextElementSibling();
                    String commentTextNode = commentNode.select("span[class=pl]").get(0).text();
                    if(commentTextNode.contains("少于")){
                        //请求书目详情页面
                        System.out.println("----------------跳转详情页面------------------");
                        String detailURL = titleNode.attr("href")+"collections";
                        String detailResText = getEntity(detailURL, "book.douban.com");
                        Document detailDoc = Jsoup.parse(detailResText);
                        commentTextNode = detailDoc.select("div[class=article] h2 span").get(0).text();
                        Element outerCommentScoreNode = detailDoc.select("div[class=rating_detail_star]").get(0);
                        Double commentScore = 0.0;
                        for(int j=1;j<=5;j++){
                            Element innerCommentScoreNode = outerCommentScoreNode.select("span[class=stars"+j+" starstop]").get(0);
                            String innerCommentPercent = ((TextNode)innerCommentScoreNode.nextElementSibling().nextSibling()).text().trim();
                            Double percent = Double.parseDouble(innerCommentPercent.substring(0,innerCommentPercent.indexOf("%")))/100;
                            commentScore += percent*j;
                         }
                        returns[0] = Double.parseDouble(commentTextNode.substring(0, commentTextNode.indexOf("人")));
                        returns[1] = commentScore;
                    }else if(commentTextNode.contains("无人")){
                        returns[0] = null;
                        returns[1] = null;
                    }else{
                        returns[0] = Double.parseDouble(commentNode.select("span[class=rating_nums]").get(0).text());
                        returns[1] = Double.parseDouble(commentTextNode.substring(1, commentTextNode.indexOf("评价")));
                    }
                    returns[0] = null;
                    returns[1] = null;
                    return returns;

                }else{
                    System.out.println("-----------------出版社、出版时间匹配错误："+author+"、"+publishTime);
                    returns[0] = null;
                    returns[1] = null;
                    return returns;
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
        httpGet.setHeader("Referer", "http://www.douban.com");
        response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
            Header[] headers = response.getHeaders("Location");
            if (headers != null && headers.length > 0) {
                String redirectUrl = headers[0].getValue();
                System.out.println("重定向的URL:" + redirectUrl);
                redirectUrl = redirectUrl.replace(" ", "%20");
                redirectUrl += "/collections";
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
}
