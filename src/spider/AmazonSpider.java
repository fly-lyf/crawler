package spider;

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

    public Double[] getParams(SearchResult searchResult) throws IOException {
        Double[] result = new Double[2];
        String url = "https://www.amazon.cn/s/ref=nb_sb_noss?__mk_zh_CN=%E4%BA%9A%E9%A9%AC%E9%80%8A%E7%BD%91%E7%AB%99&url=search-alias%3Dstripbooks&field-keywords=" + URLEncoder.encode(searchResult.getTitle(), "utf-8") + "+" + URLEncoder.encode(searchResult.getAuthor(), "utf-8");
        String resText = getEntity(url);
//        System.out.println(resText);
        System.out.print("书名+作者名为：" + searchResult.getTitle() + "+" + searchResult.getAuthor());
        //匹配书名
        Pattern patternTitle = Pattern.compile("<h2(\\s*)class=\"a-size-medium(\\s*)a-color-null(\\s*)s-inline(\\s*)s-access-title(\\s*)a-text-normal\">([^\\<]*)");
        Matcher matcherTitle = patternTitle.matcher(resText);
        String title = null;
        while (matcherTitle.find()) {
            title = matcherTitle.group();
            title = title.substring(title.lastIndexOf(">") + 1, title.length());
            title = StringEscapeUtils.unescapeHtml3(title);

            if (title == null) {
                title = "error错误";
            }
            if (searchResult.getTitle().contains(title) || title.contains(searchResult.getTitle())) {
                //匹配评分
                Pattern patternScore = Pattern.compile("<span(\\s*)class=\"a-icon-alt\">([^\\<]*)");
                Matcher matcherScore = patternScore.matcher(resText);
                while (matcherScore.find()) {
                    String score = matcherScore.group();
                    score = score.substring(score.lastIndexOf(">") + 1, score.length());
                    if (score.contains("平均")) {
                        score = score.substring(2, score.indexOf("星"));
                        score = score.trim();
                        System.out.print("    得分为：" + score);
                        result[1] = Double.parseDouble(score);
                        break;
                    }
                }
                //匹配评论人数
                Pattern patternPerson = Pattern.compile("<a(\\s*)class=\"a-size-small(\\s*)a-link-normal(\\s*)a-text-normal\"([^\\<]*)");
                Matcher matcherPerson = patternPerson.matcher(resText);
                while (matcherPerson.find()) {
                    String person = matcherPerson.group();
                    person = person.substring(person.lastIndexOf(">") + 1, person.length());
                    if (StringUtils.isNumeric(person)) {
                        result[0] = Double.parseDouble(person);
                        System.out.println("   评论人数为：" + person);
                        break;
                    }

                }
                break;
            }
        }
        if (result[0] == null) {
            System.out.println("   无评论信息");
        }
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
}
