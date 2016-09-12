package spider;

import com.google.gson.Gson;
import com.sun.deploy.net.URLEncoder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.CnkiResult;
import pojo.SearchResult;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/9/5.
 */
public class AmazonSpider {
    private String cookieStr;

    public Double[] requestAmazon(SearchResult searchResult) throws IOException {
        Double[] result = new Double[2];
        String searchTitle = searchResult.getTitle();
        String searchAuthor = searchResult.getAuthor();
        //截取冒号，破折号之前的书名
        if (searchTitle.indexOf(":") != -1) {
            searchTitle = searchTitle.substring(0, searchTitle.indexOf(":"));
        } else if (searchTitle.indexOf("—") != -1) {
            searchTitle = searchTitle.substring(0, searchTitle.indexOf("—"));
        } else if (searchTitle.indexOf("-") != -1) {
            searchTitle = searchTitle.substring(0, searchTitle.indexOf("-"));
        } else if (searchTitle.indexOf("•") != -1) {
            searchTitle = searchTitle.substring(0, searchTitle.indexOf("•"));
        } else if (searchTitle.indexOf("·") != -1) {
            searchTitle = searchTitle.substring(0, searchTitle.indexOf("·"));
        } else if (searchTitle.indexOf("、") != -1) {
            searchTitle = searchTitle.substring(0, searchTitle.indexOf("、"));
        }
        if (searchAuthor.indexOf("•") != -1) {
            searchAuthor = searchAuthor.substring(0, searchAuthor.indexOf("•"));
        } else if (searchAuthor.indexOf("·") != -1) {
            searchAuthor = searchAuthor.substring(0, searchAuthor.indexOf("·"));
        }
        String url = "https://www.amazon.cn/s/ref=nb_sb_noss?__mk_zh_CN=%E4%BA%9A%E9%A9%AC%E9%80%8A%E7%BD%91%E7%AB%99&url=search-alias%3Dstripbooks&field-keywords=" + URLEncoder.encode(searchTitle, "utf-8") + "+" + URLEncoder.encode(searchAuthor, "utf-8");
//        直接获取json
//        String query = URLEncoder.encode(searchTitle, "utf-8") + "+" + URLEncoder.encode(author, "utf-8");
//        String url = "https://www.amazon.cn/mn/search/ajax/ref=nb_sb_noss?__mk_zh_CN=%E4%BA%9A%E9%A9%AC%E9%80%8A%E7%BD%91%E7%AB%99&url=search-alias=stripbooks&field-keywords="+query+"&"+URLEncoder.encode("rh=n:658390051,k:"+query+URLEncoder.encode("&fromHash=/ref=nb_sb_noss?__mk_zh_CN=亚马逊网站&url=search-alias=stripbooks&field-keywords="+query+"&rh=n:658390051,k:"+query+"&section=ATF,BTF&fromApp=gp/search&fromPage=results&fromPageConstruction=auisearch&version=2&oqid=1473696564&atfLayout=list", "utf-8"),"utf-8");
        System.out.println("---------------开始-------------------");
        System.out.println(url);
        System.out.println("书名、作者名为： " + searchTitle + "   " + searchAuthor);
        String resText = getEntity(url);
        Document doc = Jsoup.parse(resText);
//        resText = resText.replace("&&&", ",");

        //匹配书名
        Elements bookList = doc.select("li[class=s-result-item celwidget]");
        for (int i = 0; i < bookList.size(); i++) {
            Element title = bookList.get(i).select("h2[class=a-size-medium a-color-null s-inline  s-access-title  a-text-normal]").get(0);
            Elements author = bookList.get(i).select("div[class=a-row a-spacing-none] span[class=a-size-small a-color-secondary]");
            if(title != null && title.attr("data-attribute").indexOf(searchTitle) != -1){
                    if(author.get(1).text().indexOf(searchAuthor) != -1){
                        System.out.println("---------匹配成功----------");
                        Element commentNode = null;
                        Element countNode = null;
                        if(bookList.get(i).select("span[class=a-icon-alt]").size() > 0){
                            commentNode = bookList.get(i).select("span[class=a-icon-alt]").get(0);
                            Elements test =  bookList.get(i).select("div[class=a-row a-spacing-mini]");
                            countNode = bookList.get(i).select("div[class=a-row a-spacing-mini]").get(0).select("a[rel=noopener noreferrer]").get(0);
                        }
                        if(commentNode != null){
                            String countStr = countNode.text();
                            String commentStr = commentNode.text();
                            result[0] = Double.parseDouble(countStr);
                            result[1] = Double.parseDouble(commentStr.substring(2, commentStr.indexOf(" 星")));
                            System.out.println("评价人数：  "+result[0]+"    得分："+result[1]);
                            return result;
                        }else {
                            System.out.println("没有评价信息");
                            result[0] = null;
                            result[1] = null;
                            return result;
                        }
                    }else {
                        System.out.println("----------作者匹配失败--------");
                    }
            }else {
                System.out.println("------书名匹配失败------");
            }
        }
        result[0] = null;
        result[1] = null;
        return result;
    }

    public String getEntity(String url) throws IOException {
        cookieStr = "x-wl-uid=1bHFKxNB4scRV9Jj2K5vmfPjnndBF1x0UYcoX2KYiSUVzRm03Y+T1vPt3SIYj5lzfU9iTJ756x6k=; session-token=0XA/TtppFeTIB5A3lFArF+XK9Ve4tH6OpebJvOGNjWj8pIgFYKz7nj6byD3U+W3rTMWtl/IzlU3WifJDKCA2jqju59P+xMi5w4RnAbJU102rxi1oo71Mu68FLBUJq0Sq1gmYJK465wOTCmlgP3NjVVQBf62ua4i+rAiQkx/ax0xdOcyAYMbbdNybiJ2tw/aJjQEiM0rN+ztAPSTXF0hQlJ5UHBiZ6VinmpStipy92SQm35WqiNggpg==; 5SnMamzvowels.pos=1; 5SnMamzvowels.time.0=1441437177835; session-id-time=2082729601l; session-id=479-6067989-0408645; csm-hit=M794MJE4WYEQ3F7CRDDV+s-HG0DVC22HX8ZEFN9D952|1441437237132; ubid-acbcn=477-5748511-6481749";
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
                redirectUrl += "/collections";
                resText = getEntity(redirectUrl);
            }
        } else if (statusCode == HttpStatus.SC_OK) {
            entity = response.getEntity();
            resText = EntityUtils.toString(entity, "utf-8");
        } else {
            System.out.println("状态码：" + statusCode);
        }
        return resText;
    }

    //单元测试
    public static void main(String[] args) throws IOException {
        AmazonSpider amazonSpider = new AmazonSpider();
        SearchResult searchResult = new SearchResult("古英语与中古英语文学通论","陈才宇", "", 0, "");
        System.out.println(amazonSpider.requestAmazon(searchResult));

    }
}
