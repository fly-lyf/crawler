import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/8/3.
 */
public class ResultGetter {

    public String Crawler(String title, String author, String year, String publisher) {
        HttpEntity entity = null;
        String result= null;
        HttpGet httpget = null;
        System.out.println("------------------------------------");
        // 创建默认的客户端实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String matcherStr = null;
        Date date = new Date();
        Long time = date.getTime();
        int cookieYear = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();
        int hour = date.getHours();
        int minute = date.getMinutes();
        int second = date.getSeconds();
        String cookieTime = "0" + month + "/" + day + "/" + cookieYear + " " + hour + ":" + minute + ":" + second;
        System.out.println(cookieTime);
        // 创建get请求实例
        try {
            title = URLEncoder.encode(title, "UTF-8");
            author = URLEncoder.encode(author, "UTF-8");
            year = URLEncoder.encode(title, "UTF-8");
            publisher = URLEncoder.encode(publisher, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url1 = "http://epub.cnki.net/KNS/request/SearchHandler.ashx?action=&NaviCode=*&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCJFN%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND%2CCCJD%2CHBRD&base_special1=%25&magazine_special1=%25&txt_1_sel=RF&txt_1_value1=%E9%99%88%E7%8B%AC%E7%A7%80&txt_1_relation=%23CNKI_AND&txt_1_special1=%3D&his=0&__=Fri%20Aug%2028%202015%2000%3A40%3A25%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        String urlStart = "http://epub.cnki.net/kns/brief/brief.aspx?pagename=ASP.brief_result_aspx&dbPrefix=SCDB&dbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&research=off&";
        String urlVar = "t=" + time + "&keyValue=" + title;
        String urlEnd = "&S=1";
        String url = urlStart + urlVar + urlEnd;
        System.out.println(url1);
        HttpResponse response = null;
        httpget = new HttpGet(url1);
        try {
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36");
            httpget.setHeader("Cookie", "RsPerPage=20; ASP.NET_SessionId=rnl5prffqhzpibbmtlpsu5yz; LID=WEEvREcwSlJHSldSdnQ0UDlMSFdVWTd2dE1kZC9UcUlWb0NwTFBiOE9uN2VZanIwREYyRys1emhyZzFzaFhveGNnPT0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4IQMovwHtwkF4VYPoHbKxJw!!");
            httpget.setHeader("Host", "epub.cnki.net");
            httpget.setHeader("Referer", "http://epub.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
            httpget.setHeader("Accept-", "zh-CN,zh;q=0.8");
            httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

//            response = httpClient.execute(httpget);
//            entity = response.getEntity();
//            result = EntityUtils.toString(entity, "utf-8");
//            System.out.println(result);
            System.out.println("____________________________________");
            httpget = new HttpGet(url);
            httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36");
            httpget.setHeader("Cookie", "RsPerPage=20; ASP.NET_SessionId=rnl5prffqhzpibbmtlpsu5yz; LID=WEEvREcwSlJHSldSdnQ0UDlMSFdVWTd2dE1kZC9UcUlWb0NwTFBiOE9uN2VZanIwREYyRys1emhyZzFzaFhveGNnPT0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4IQMovwHtwkF4VYPoHbKxJw!!");
            httpget.setHeader("Host", "epub.cnki.net");
            httpget.setHeader("Referer", "http://epub.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
            httpget.setHeader("Accept-", "zh-CN,zh;q=0.8");
            httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            response = httpClient.execute(httpget);
//                // 服务器响应状态行
//            Header[] heads = response.getAllHeaders();
//                System.out.println("____________________________________");
//                // 打印所有响应头
//                for (Header h : heads) {
//                    System.out.println(h.getName() + ":" + h.getValue());
//                }
            // 获取响应消息实体
            entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            System.out.println(result);
//            System.out.println("------------------------------------");
            if (entity != null) {
                //响应内容
//                result = EntityUtils.toString(entity, "utf-8");
                System.out.println(result);
//                if (result.contains("id=\"resultcount\" name=\"resultcount\"")) {
//                    result = result.substring(result.indexOf("id=\"resultcount\" name=\"resultcount\""), result.indexOf("<input type=\"hidden\" id=\"krsUrl\""));
////                    System.out.println(result);
//                }
//                Pattern pattern = Pattern.compile("[id=\"resultcount\" name=\"resultcount\" value=\"]\\d+[\" />]");
//                Matcher matcher = pattern.matcher(result);
//                if (matcher.find()) {
//                    matcherStr = matcher.group();
//                    matcherStr = matcherStr.substring(1, matcherStr.lastIndexOf("\""));
//                    return matcherStr;
//                }

            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "(**&(^";
    }

}


