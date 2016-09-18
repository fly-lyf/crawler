package spider;


import com.sun.deploy.net.URLEncoder;
import org.apache.http.*;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.CnkiResult;
import pojo.SearchResult;
import java.io.IOException;



/**
 * Created by Administrator on 2015/9/5.
 */
public class AmazonSpider {
    private String cookieStr;
    private SearchResult innerSearch;
    private Util util = new Util();

    //主查询
    public Double[] requestAmazon(SearchResult searchResult) throws IOException, InterruptedException {
        innerSearch = searchResult;
        Double[] result = new Double[2];
        //格式化查询条件
        String[] parsedTitleAuthor = util.formatSearchResult(searchResult);
        String searchTitle = parsedTitleAuthor[0];
        String searchAuthor = parsedTitleAuthor[1];
        String searchSpareTitle = parsedTitleAuthor[2];
        String searchSpareAuthor = parsedTitleAuthor[3];

        //备用书名生成
        searchSpareTitle = util.createSpareTile(searchTitle, searchSpareTitle);

        String url = "https://www.amazon.cn/s/ref=nb_sb_noss?__mk_zh_CN=%E4%BA%9A%E9%A9%AC%E9%80%8A%E7%BD%91%E7%AB%99&url=search-alias%3Dstripbooks&field-keywords=" + URLEncoder.encode(searchTitle, "utf-8");
//        直接获取json
//        String query = URLEncoder.encode(searchTitle, "utf-8") + "+" + URLEncoder.encode(author, "utf-8");
//        String url = "https://www.amazon.cn/mn/search/ajax/ref=nb_sb_noss?__mk_zh_CN=%E4%BA%9A%E9%A9%AC%E9%80%8A%E7%BD%91%E7%AB%99&url=search-alias=stripbooks&field-keywords="+query+"&"+URLEncoder.encode("rh=n:658390051,k:"+query+URLEncoder.encode("&fromHash=/ref=nb_sb_noss?__mk_zh_CN=亚马逊网站&url=search-alias=stripbooks&field-keywords="+query+"&rh=n:658390051,k:"+query+"&section=ATF,BTF&fromApp=gp/search&fromPage=results&fromPageConstruction=auisearch&version=2&oqid=1473696564&atfLayout=list", "utf-8"),"utf-8");
        System.out.println("---------------开始-------------------");
        System.out.println(url);
        System.out.println("原书名、作者名为： " + searchResult.getTitle() + "   " + searchResult.getAuthor());
        System.out.println("查询用的书名、作者名为： " + searchTitle + "   " + searchAuthor);
        String resText = getEntity(url);
        Document doc = Jsoup.parse(resText);
//        resText = resText.replace("&&&", ",");

        //匹配书名
        Elements bookList = doc.select("li[class=s-result-item celwidget]");
        for (int i = 0; i < bookList.size(); i++) {
            Element isDigital = bookList.get(i).select("h3[class=a-size-small a-color-null s-inline    a-text-normal]").get(0);
            if (bookList.size() > 1 && isDigital.text().contains("电子书")) {
                System.out.println("结果列表长度大于1， 跳过电子书的条目");
                continue;
            }
            Element titleNode = bookList.get(i).select("h2[class=a-size-medium a-color-null s-inline  s-access-title  a-text-normal]").get(0);
            Elements authorNode = bookList.get(i).select("div[class=a-row a-spacing-none] span[class=a-size-small a-color-secondary]");
            String title = null;
            if(titleNode != null){
                title = titleNode.attr("data-attribute");
                title = util.formatTitleString(title);
            }
            String author = null;
            if(authorNode.size() > 1){
                author = authorNode.get(1).text();
            }

            // 同时匹配备用书名和正式书名
            String matchedTitle = null;
            if (title.indexOf(searchTitle) != -1) {
                matchedTitle = searchTitle;
            } else if (searchSpareTitle != null && title.indexOf(searchSpareTitle) != -1) {
                matchedTitle = searchSpareTitle;
            }
            //匹配正式作者
            String matchedAuthor = null;
            if (author == null || author.indexOf(searchAuthor) != -1 || searchAuthor.contains(author)) {
                matchedAuthor = searchAuthor;
            }
            //如果查到的作者为空就不需要匹配备用作者了，不为空，则需要。代码逻辑不严密但没有错误
            if(author == null || (searchSpareAuthor != null && !searchSpareAuthor.equals("") && author.indexOf(searchSpareAuthor) != -1)){
                matchedAuthor = searchSpareAuthor;
            }
            if (matchedTitle != null) {
                if (matchedAuthor != null) {
                    System.out.println("---------书名、作者匹配成功----------");
                    Element commentNode = null;
                    Element countNode = null;
                    if (bookList.get(i).select("span[class=a-icon-alt]").size() > 0) {
                        commentNode = bookList.get(i).select("span[class=a-icon-alt]").get(0);
                        countNode = bookList.get(i).select("div[class=a-column a-span5 a-span-last]").get(0).select("a[rel=noopener noreferrer]").get(0);
                    }
                    if (commentNode != null) {
                        String countStr = countNode.text();
                        String commentStr = commentNode.text();
                        result[0] = Double.parseDouble(countStr);
                        result[1] = Double.parseDouble(commentStr.substring(2, commentStr.indexOf(" 星")));
                        System.out.println("评价人数：  " + result[0] + "    得分：" + result[1]);
                        return result;
                    } else {
                        System.out.println("没有评价信息");
                        result[0] = 0.0;
                        result[1] = 0.0;
                        return result;
                    }
                } else {
                    System.out.println("----------作者匹配失败,爬到的作者名字是--------"+author);
                }
            }
        }
        System.out.println("----------获取结果列表失败，可能没有匹配到书目--------");
        result[0] = null;
        result[1] = null;
        return result;
    }

    //获取http响应
    public String getEntity(String url) throws IOException, InterruptedException {
        cookieStr = "x-wl-uid=13s6CQwa7CHtIWW1zoT6waLh4ASpP1hAV2mBoiP6jLroCnBMSl/isUm9JfmOcZ1SaIBFxpW776Vg=; session-token=\"VDOfgl36uB/iR1SkbIHZbN+Iz1QDH4+EwBQFeDk+IcTLb5ozSjpLTS5DV6VC+9ZDV4A5MmZJF07Qt7gH57if5Ifwtr0NdL/EPpOUGzUvPy2k1AR0+grhK3QPErDL2xrG0Q49FU1V+QZ8T13Jui7LOLxtBugQyBiGdc9SGm+bVNIoY59RlWeBCxnsR6RHtDJHv1d0o7QHQlgvFv85U5qyXw==\"; ubid-acbcn=452-0670493-8094523; session-id-time=2082729601l; session-id=456-1692712-1972507; csm-hit=B22D700A9XR07N0RB36N+s-B22D700A9XR07N0RB36N|1473950318203";
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
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.89 Safari/537.36");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cookie", cookieStr);
        httpGet.setHeader("Host", "www.amazon.cn");
//        httpGet.setHeader("Referer", "");
        response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
            Header[] headers = response.getHeaders("Location");
            if (headers != null && headers.length > 0) {
                String redirectUrl = headers[0].getValue();
                System.out.println("重定向的URL:" + redirectUrl);
                redirectUrl = redirectUrl.replace(" ", "%20");
                resText = getEntity(redirectUrl);
            }
        } else if (statusCode == HttpStatus.SC_OK) {
            entity = response.getEntity();
            resText = EntityUtils.toString(entity, "utf-8");
        } else {
            System.out.println("状态码：" + statusCode);
            System.out.println("重试");
            Thread.sleep(3000);
            this.requestAmazon(innerSearch);
        }
        return resText;
    }

    //单元测试
    public static void main(String[] args) throws IOException, InterruptedException {
        AmazonSpider amazonSpider = new AmazonSpider();
        SearchResult searchResult = new SearchResult("中国云南少数民族音乐考源", "谢自律", "", 2011, "");
        System.out.println(amazonSpider.requestAmazon(searchResult));

    }
}
