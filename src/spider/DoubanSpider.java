package spider;

import org.apache.http.*;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import pojo.CnkiResult;
import pojo.SearchResult;

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

    public Double[] getParams(SearchResult searchResult) throws IOException {
        Double[] returns = new Double[2];
        Double persons = 0.0;
        Double commentRes = 0.0;
        //爬虫
        String query = "q=" + URLEncoder.encode(searchResult.getTitle(), "utf-8");
        String URL = "http://www.douban.com/search?cat=1001&" + query;
        String resText = getEntity(URL,"www.douban.com");
//        System.out.println(resText);
        //匹配书名
        System.out.println("---开始----");
        System.out.println("作者：" + searchResult.getAuthor() + "   书名:" + searchResult.getTitle());
        Pattern pattern = Pattern.compile("<span>([^\\<]*)</span>&nbsp;<a href=([^\\<]*)");
        Matcher matcher = pattern.matcher(resText);
        while (matcher.find()) {
            String[] author = null;
            String authorStr;
            String title = matcher.group();
            String url = matcher.group();
            title = title.substring(title.lastIndexOf(">") + 1, title.length());
            if (searchResult.getTitle().contains(title) || title.contains(searchResult.getTitle())) {
                //匹配作者名
                Pattern pattern1 = Pattern.compile("<span class=\"subject-cast\">([^\\<]*)");
                Matcher matcher1 = pattern1.matcher(resText);
                while (matcher1.find()) {
                    authorStr = matcher1.group();
                    authorStr = authorStr.substring(authorStr.lastIndexOf(">") + 1, authorStr.length());
                    author = authorStr.replaceAll(" ", "").split("/");
                    if (searchResult.getAuthor().contains(author[0]) || author[0].contains(searchResult.getAuthor())) {
                        url = url.substring(32, url.indexOf("\"", 33));
                        String secondText = getEntity(url,"www.douban.com");
//                        System.out.println(secondText);
                        //匹配评论人数赋值
                        Pattern pattern2 = Pattern.compile("<div class=\"article\">(\\s*)<h2>(\\s*)<span class=\"\">([^\\<]*)");
                        Matcher matcher2 = pattern2.matcher(secondText);
                        while (matcher2.find()) {
                            String comments = matcher2.group();
                            comments = comments.substring(comments.lastIndexOf(">")+1,comments.length());
                            comments = comments.substring(0,comments.indexOf("人"));
                            System.out.print("评论数：" + comments);
                            persons = Double.parseDouble(comments);
                            break;
                        }
                        //匹配得分赋值
                        Pattern pattern3 = Pattern.compile("<div class=\"power\" style=\"width:([0-9]*)px\"></div>([^\\<]*)");
                        Matcher matcher3 = pattern3.matcher(secondText);
                        int i=0;
                        Double score[] = new Double[5];
                        while (matcher3.find()) {
                            String comments = matcher3.group();
                            comments = comments.substring(comments.lastIndexOf(">")+1,comments.length()).trim();
                            comments = comments.substring(0,comments.length()-1);
                            score[i] = Double.parseDouble(comments);
                            i++;
                        }
                        commentRes = (score[0]*5+score[1]*4+score[2]*3+score[3]*2+score[4]*1)/100;
                        commentRes = Double.parseDouble(String.format("%.2f", commentRes));
                        System.out.println("  评价得分："+commentRes);
                        //匹配到作者名并获取两个参数后中断，return
                        returns[0] = persons;
                        returns[1] = commentRes;
                        return returns;

                    }
                }
                System.out.println("————————————————没有找到作者———————————————————");
                //匹配到书名后，没有作者名的中断，return
                returns[0] = null;
                returns[1] = null;
                return returns;
            }
        }
        System.out.println("————————————————没有找到书名———————————————————");
        //书名都没有的return
        returns[0] = null;
        returns[1] = null;
        return returns;
    }

    public String getEntity(String url,String host) throws IOException {
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
        if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
            Header[] headers = response.getHeaders("Location");
            if(headers!=null && headers.length>0){
                String redirectUrl = headers[0].getValue();
                System.out.println("重定向的URL:"+redirectUrl);
                redirectUrl = redirectUrl.replace(" ", "%20");
                redirectUrl += "/collections";
                resText = getEntity(redirectUrl,"book.douban.com");
            }
        } else if(statusCode == HttpStatus.SC_OK)  {
            entity = response.getEntity();
            resText = EntityUtils.toString(entity, "utf-8");
        }else {
            System.out.println("状态码："+statusCode);
        }
        return resText;
    }
//        List<URI> redirectLocations = null;
//        redirectLocations = context.getRedirectLocations();
//        System.out.println(redirectLocations.get(0));

}
