package spider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.CnkiResult;
import pojo.SearchResult;

/**
 * @author zhouyi
 * @date 2015-8-31 上午11:06:09
 */
public class CnkiSpider {

    private Util util = new Util();

    //传入关键字
    public String searchKeyword(SearchResult param, boolean totalTitle) throws IOException {
        //标题 名字 出版社 时间
        String key1 = param.getTitle();
        String key2 = param.getAuthor();
        String key3 = param.getPublisher();
        String key4;
        if (param.getPubTime() != null) {
            key4 = param.getPubTime().toString();
        } else {
            key4 = "";
        }
        System.out.println("___________________");
        System.out.println("文献名称:" + key1 + "   作者: " + key2 + "   出版社: " + key3 + "   出版时间: " + key4);
        //格式化查询条件
        String[] formatted = util.formatSearchResult(param);
        key1 = formatted[0];
        key2 = formatted[1];
        System.out.println("用于查询的标题:" + key1 + "   用于查询的作者名: " + key2);

        String url = "";
        try {
            if (!Objects.equals(key1, "")) {
                url = "http://epub.cnki.net/KNS/request/SearchHandler.ashx?action=&NaviCode=*&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCJFN%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND%2CCCJD%2CHBRD&base_special1=%25&magazine_special1=%25&txt_1_sel=RF&txt_1_value1=" + URLEncoder.encode(key1, "utf-8") + "&txt_1_relation=%23CNKI_AND&txt_1_special1=%3D";
                if (key2 == null) {
                    url = url + "&his=0&__=Mon%20Aug%2031%202015%2010%3A52%3A39%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
                }
            }

            if (!Objects.equals(key1, "") && !Objects.equals(key2, "")) {
                url = url + "&txt_2_sel=RF&txt_2_value1=" + URLEncoder.encode(key2, "utf-8") + "&txt_2_logical=and&txt_2_relation=%23CNKI_AND&txt_2_special1=%3D";
                if (key3 == null) {
                    url = url + "&his=0&__=Mon%20Aug%2031%202015%2010%3A52%3A39%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
                }
            }

            if (!Objects.equals(key1, "") && !Objects.equals(key2, "") && !Objects.equals(key3, "")) {
                url = url + "&txt_3_sel=RF&txt_3_value1=" + URLEncoder.encode(key3, "utf-8") + "&txt_3_logical=and&txt_3_relation=%23CNKI_AND&txt_3_special1=%3D";
                url = url + "&his=0&__=Mon%20Aug%2031%202015%2010%3A52%3A39%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";

            }

            if (!Objects.equals(key1, "") && !Objects.equals(key2, "") && !Objects.equals(key3, "") && !Objects.equals(key4, "")) {
                url = url + "&txt_4_sel=RF&txt_4_value1=" + URLEncoder.encode(key4, "utf-8") + "&txt_4_logical=and&txt_4_relation=%23CNKI_AND&txt_4_special1=%3D&his=0&__=Mon%20Aug%2031%202015%2010%3A52%3A39%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getEntity(url);
        try {
            return "http://epub.cnki.net/kns/brief/brief.aspx?pagename=ASP.brief_result_aspx&dbPrefix=SCDB&dbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&research=off&t=1473963723471&keyValue=" + URLEncoder.encode(key1, "utf-8") + "&S=1";
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    //总结果数
    public Integer getTotalNum(String url) throws IOException {
        CnkiResult cnkiResult = new CnkiResult();
        String resText = getEntity(url);
        //总结果数量
        Pattern patternCount = Pattern.compile("找到&nbsp;(.*?)&nbsp;条结果&nbsp;");
        Matcher matcherCount = patternCount.matcher(resText);
        if (matcherCount.find()) {
            String countCount = matcherCount.group().substring(8, matcherCount.group().indexOf("&", 8));
            countCount = countCount.replaceAll(",", "");
            System.out.println("总结果数（未处理辑刊）：" + countCount);
            Integer countNum = Integer.parseInt(countCount);
            return countNum;
        }
        return -1;
    }

    //获取按年度引用
    public HashMap<String, Integer> getCitations(SearchResult searchResult) throws IOException {
        HashMap<String, Integer> cits = new HashMap<String, Integer>();
        String urlYear = "http://epub.cnki.net/kns/group/DoGroupLeft.ashx?action=1&Param=ASP.brief_result_aspx%23SCDB/%E5%8F%91%E8%A1%A8%E5%B9%B4%E5%BA%A6/%e5%b9%b4%2Ccount%28*%29/%e5%b9%b4/%28%e5%b9%b4%2C%27date%27%29%23%e5%b9%b4%24desc/1000000%24/-/40/40000/ButtonView&cid=0&clayer=0&isAutoInit=1&__=Wed%20Sep%2007%202016%2016%3A14%3A50%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        String resYear = getEntity(urlYear);
        Document docYear = Jsoup.parse(resYear);
        Elements eleYearNode = docYear.select("span[class=GroupItemLinkBlue] a");
        String[] yearsStr = eleYearNode.text().split(" ");
        Elements timesNode = docYear.select("span[style=color:#999;]");
        String[] timesStr = timesNode.text().split(" ");
        if (timesStr.length == 0 || (timesStr.length == 1 && timesStr[0].equals(""))) {
            return cits;
        }
        for (int i = 0; i < timesStr.length; i++) {
            String time = timesStr[i];
            timesStr[i] = time.substring(1, time.indexOf(")"));
        }
        Integer[] timesInt = new Integer[timesStr.length];
        for (int i = 0; i < timesStr.length; i++) {
            timesInt[i] = Integer.parseInt(timesStr[i]);
        }

        for (int i = 0; i < timesInt.length; i++) {
            if (cits.get(yearsStr[i]) != null) {
                Integer num = cits.get(yearsStr[i]);
                cits.put(yearsStr[i], num + timesInt[i]);
            } else {
                cits.put(yearsStr[i], timesInt[i]);
            }
        }
        return cits;
    }

    //获取论文类型
    public HashMap<String, Integer>[] getTypes(SearchResult searchResult, HashMap<String, Integer> cits, Integer count) throws IOException, InterruptedException {
        HashMap<String, Integer> types = new HashMap<>();
        String url = "http://epub.cnki.net/kns/group/DoGroupLeft.ashx?action=21&Param=ASP.brief_result_aspx%23SCDB/ButtonView/&cid=21&clayer=0&__=Fri%20Sep%2016%202016%2000%3A12%3A31%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        Document doc = Jsoup.parse(getEntity(url));
        types.put("master", 0);
        types.put("doctor", 0);
        types.put("conference", 0);
        types.put("magazine", 0);
        Elements sourceNameNodes = doc.select("span[name=GroupItemALink]");
        for (Iterator<Element> iterator = sourceNameNodes.iterator(); iterator.hasNext(); ) {
            Element sourceNameNode = iterator.next();
            Element sourceCountNode = sourceNameNode.nextElementSibling();
            String sourceName = sourceNameNode.text();
            String sourceCountStr = sourceCountNode.text();
            if (sourceName.contains("硕士")) {
                Integer sourceCount = Integer.parseInt(sourceCountStr.substring(1, sourceCountStr.indexOf(")")));
                sourceCount += types.get("master");
                types.put("master", sourceCount);
            } else if (sourceName.contains("博士")) {
                Integer sourceCount = Integer.parseInt(sourceCountStr.substring(1, sourceCountStr.indexOf(")")));
                sourceCount += types.get("doctor");
                types.put("doctor", sourceCount);
            } else if (sourceName.contains("会议")) {
                Integer sourceCount = Integer.parseInt(sourceCountStr.substring(1, sourceCountStr.indexOf(")")));
                sourceCount += types.get("conference");
                types.put("conference", sourceCount);
            } else if (sourceName.contains("辑刊")) {
                //辑刊只有一个数据库，所以这个分支只进入一次
                Integer sourceCount = Integer.parseInt(sourceCountStr.substring(1, sourceCountStr.indexOf(")")));
                Thread.sleep(1000);
                String[] formatted = util.formatSearchResult(searchResult);
                String key1 = formatted[0];
                String queryID = "";
                String innerUrl = "http://epub.cnki.net/kns/brief/brief.aspx?pagename=ASP.brief_result_aspx&dbPrefix=SCDB&dbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&research=off&t=1473963723471&keyValue=" + URLEncoder.encode(key1, "utf-8") + "&S=1";
                String innerRes = getEntity(innerUrl);
                Document innerDoc = Jsoup.parse(innerRes);
                Elements items = innerDoc.select("table[class=GridTableContent] tr[bgcolor]");
                Elements aPages = innerDoc.select("table[class=pageBar_bottom] a");
                if (aPages.size() > 0) {
                    String href = aPages.get(0).attr("href");
                    queryID = href.substring(href.indexOf("QueryID=")+8, href.indexOf("&ID="));
                }
                for (Iterator<Element> elementIterator = items.iterator(); elementIterator.hasNext(); ) {
                    Element next = elementIterator.next();
                    String timeStr = next.select("td[class=tdrigtxt]").get(0).text();
                    String typeStr = next.select("td[class=tdrigtxt]").get(1).text();
                    if (typeStr.contains("辑刊")) {
                        timeStr = timeStr.substring(0, 4);
                        if (cits.get(timeStr) != null && cits.get(timeStr) != 0) {
                            Integer cit = cits.get(timeStr);
                            cits.put(timeStr, cit - 1);
                        }
                    }
                }
                //第二页开始
                for (int i = 2; i <= count / 50 + 1; i++) {
                    Thread.sleep(1000);
                    innerUrl = "http://epub.cnki.net/kns/brief/brief.aspx?curpage=" + i + "&RecordsPerPage=50&QueryID=" + queryID + "&ID=&turnpage=1&tpagemode=L&dbPrefix=SCDB&Fields=&DisplayMode=listmode&PageName=ASP.brief_result_aspx#J_ORDER";
                    innerRes = getEntity(innerUrl);
                    innerDoc = Jsoup.parse(innerRes);
                    aPages = innerDoc.select("table[class=pageBar_bottom] a");
                    if (aPages.size() > 0) {
                        String href = aPages.get(0).attr("href");
                        queryID = href.substring(href.indexOf("QueryID=")+8, href.indexOf("&ID="));
                    }
                    items = innerDoc.select("table[class=GridTableContent] tr[bgcolor]");
                    for (Iterator<Element> elementIterator = items.iterator(); elementIterator.hasNext(); ) {
                        Element next = elementIterator.next();
                        String timeStr = next.select("td[class=tdrigtxt]").get(0).text();
                        String typeStr = next.select("td[class=tdrigtxt]").get(1).text();
                        if (typeStr.contains("辑刊")) {
                            timeStr = timeStr.substring(0, 4);
                            if (cits.get(timeStr) != 0 && cits.get(timeStr) != null) {
                                Integer cit = cits.get(timeStr);
                                cits.put(timeStr, cit - 1);
                            }
                        }
                    }
                }
                //修改总数
                count -= sourceCount;
            } else {
                Integer sourceCount = Integer.parseInt(sourceCountStr.substring(1, sourceCountStr.indexOf(")")));
                sourceCount += types.get("magazine");
                types.put("magazine", sourceCount);
            }
        }
        HashMap<String, Integer> countMap = new HashMap<>();
        countMap.put("count", count);
        HashMap<String, Integer>[] result = new HashMap[3];
        System.out.println("总结果数（已处理辑刊）：" + count);
        for (Iterator<Map.Entry<String, Integer>> iterator = types.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Integer> it = iterator.next();
            System.out.println("论文类型: " + it.getKey() + "   被引次数： " + it.getValue());
        }
        for (Iterator<Map.Entry<String, Integer>> iterator = cits.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Integer> it = iterator.next();
            System.out.println("年份: " + it.getKey() + "   被引次数： " + it.getValue());
        }
        result[0] = types;
        result[1] = cits;
        result[2] = countMap;
        return result;
    }

    //获取作者自引
    public Integer getAuthorSelfCitaion(SearchResult param) throws IOException {
        String url = "http://epub.cnki.net/kns/group/DoGroupLeft.ashx?action=1&Param=ASP.brief_result_aspx%23SCDB/%e4%bd%9c%e8%80%85/groupcodename%28SYS_AUTHORCODENAME%29%2Ccount%28*%29%2CID%23%e4%bd%9c%e8%80%85%e4%bb%a3%e7%a0%81/%e4%bd%9c%e8%80%85%e4%bb%a3%e7%a0%81/%28%e4%bd%9c%e8%80%85%e4%bb%a3%e7%a0%81%2C%27minteger%27%29/40000/-/40/40000/ButtonView&cid=2&clayer=0&__=Fri%20Sep%2016%202016%2000%3A12%3A34%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        String author = util.formatSearchResult(param)[1];
        Integer selfAuthorCitation = getSelfCitaion(url, author, param);
        System.out.println("自引：" + selfAuthorCitation);
        return selfAuthorCitation;
    }

    //获取机构自引
    public Integer getSelfInstituteCitation(SearchResult param) throws IOException {
        String url = "http://epub.cnki.net/kns/group/DoGroupLeft.ashx?action=1&Param=ASP.brief_result_aspx%23SCDB/%e6%9c%ba%e6%9e%84/groupcodename%28SYS_INSTCODENAME%29%2Ccount%28*%29%2CID%23%e6%9c%ba%e6%9e%84%e4%bb%a3%e7%a0%81/%e6%9c%ba%e6%9e%84%e4%bb%a3%e7%a0%81/%28%e6%9c%ba%e6%9e%84%e4%bb%a3%e7%a0%81%2C%27minteger%27%29/40000/-/40/40000/ButtonView&cid=3&clayer=0&__=Fri%20Sep%2016%202016%2000%3A12%3A35%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        String address = param.getAddress();
        Integer selfInstituteCitation = getSelfCitaion(url, address, param);
        System.out.println("机构自引：" + selfInstituteCitation);
        return selfInstituteCitation;
    }

    //获取自引基函数
    public Integer getSelfCitaion(String url, String searchParam, SearchResult param) throws IOException {
        Integer self = 0;
        Document doc = Jsoup.parse(getEntity(url));
        Elements selfCitNodes = doc.select("span[name=GroupItemALink]");
        for (Iterator<Element> iterator = selfCitNodes.iterator(); iterator.hasNext(); ) {
            Element selfCitNode = iterator.next();
            Element selfCitCountNode = selfCitNode.nextElementSibling();
            String selfCitName = selfCitNode.text();
            if (selfCitName.contains(searchParam)) {
                String selfCitCountStr = selfCitCountNode.text();
                self = Integer.parseInt(selfCitCountStr.substring(1, selfCitCountStr.indexOf(")")));
                return self;
            }
        }
        return self;
    }


    //拉取报纸评论并提取数量
    public String getNewPaperComment(String key) throws IOException {
        String url = "http://epub.cnki.net/KNS/request/SearchHandler.ashx?action=&NaviCode=*&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCJFN%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND%2CCCJD%2CHBRD&base_special1=%25&magazine_value1=%E5%85%89%E6%98%8E%E6%97%A5%E6%8A%A5%2B%E6%96%B0%E5%8D%8E%E6%AF%8F%E6%97%A5%E7%94%B5%E8%AE%AF%2B%E6%96%87%E6%B1%87%E6%8A%A5%2B%E4%B8%AD%E5%9B%BD%E7%A4%BE%E4%BC%9A%E7%A7%91%E5%AD%A6%E6%8A%A5&magazine_special1=%3D&txt_1_sel=SU&txt_1_value1=" + URLEncoder.encode(key, "utf-8") + "&txt_1_relation=%23CNKI_AND&txt_1_special1=%3D&his=0&__=Tue%20Sep%2015%202015%2020%3A59%3A58%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        getEntity(url);
        url = "http://epub.cnki.net/kns/brief/brief.aspx?pagename=ASP.brief_result_aspx&dbPrefix=SCDB&dbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&research=off&t=1442322612064&keyValue=" + URLEncoder.encode(key, "utf-8") + "&S=1";
        String resText = getEntity(url);
        Document doc = Jsoup.parse(resText);
        Elements pagerTitleCell = doc.select("div[class=pagerTitleCell]");
        String result = pagerTitleCell.get(0).text();
        result = result.substring(result.indexOf("到") + 2, result.indexOf("条") - 1);
        return result;
    }

    //拉取学术评论，并提取数量
    public String getScholarComment(String key) throws IOException {
        CnkiResult cnkiResult = new CnkiResult();
        String url = "http://epub.cnki.net/KNS/request/SearchHandler.ashx?action=&NaviCode=*&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCJFN%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND%2CCCJD%2CHBRD&base_special1=%25&magazine_special1=%25&txt_1_sel=SU&txt_1_value1=" + URLEncoder.encode(key, "utf-8") + "&txt_1_relation=%23CNKI_AND&txt_1_special1=%3D&his=0&__=Wed%20Sep%2016%202015%2001%3A08%3A25%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        getEntity(url);
        url = "http://epub.cnki.net/kns/brief/brief.aspx?pagename=ASP.brief_result_aspx&dbPrefix=SCDB&dbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&research=off&t=1442322612064&keyValue=" + URLEncoder.encode(key, "utf-8") + "&S=1";
        String resText = getEntity(url);
//        System.out.println(resText);
        Document doc = Jsoup.parse(resText);
        Elements pagerTitleCell = doc.select("div[class=pagerTitleCell]");
        String result = pagerTitleCell.get(0).text();
        result = result.substring(result.indexOf("到") + 2, result.indexOf("条") - 1);
        return result;
    }

    //发送http请求
    public String getEntity(String url) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "*/*");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.89 Safari/537.36");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        String cookie = "ASP.NET_SessionId=ovfqaz45lgpgfff0r3be0s55; kc_cnki_net_uid=12a842dd-7598-ef41-c9a8-1d8d396fb584; Ecp_ClientId=1160907225902061955; RsPerPage=50; LID=WEEvREcwSlJHSldRa1FhdkJkdjFtWXFlcjVEMVBhMWZlbVd3dGRrblNjWT0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4ggI8Fm4gTkoUKaID8j8gFw!!";
        httpGet.setHeader("Cookie", cookie);
        httpGet.setHeader("Host", "epub.cnki.net");
        httpGet.setHeader("Referer", "http://epub.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String resText = EntityUtils.toString(entity, "utf-8");
        return resText;
    }

    //允许传入自定义头部的http请求
    public String getEntity(String url, HashMap<String, String> headers) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "*/*");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.89 Safari/537.36");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        String cookie = "ASP.NET_SessionId=ovfqaz45lgpgfff0r3be0s55; kc_cnki_net_uid=12a842dd-7598-ef41-c9a8-1d8d396fb584; RsPerPage=20; Ecp_ClientId=1160907225902061955; LID=WEEvREcwSlJHSldRa1FhdXNXZjNkWmdvMmxhbEQrVGcrcHd6V2hLdVJCdz0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4ggI8Fm4gTkoUKaID8j8gFw!!";
        httpGet.setHeader("Cookie", cookie);
        httpGet.setHeader("Host", "epub.cnki.net");
        httpGet.setHeader("Referer", "http://epub.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
        for (Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> it = iterator.next();
            httpGet.setHeader(it.getKey(), it.getValue());
        }
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String resText = EntityUtils.toString(entity, "utf-8");
        return resText;
    }

    //单元测试
    public static void main(String[] args) throws IOException, InterruptedException {
        CnkiSpider cnki = new CnkiSpider();
        SearchResult searchResult = new SearchResult("哈代诗歌研究", "颜学军", "人民文学出版社", 0, "东北师范大学");
        String url = cnki.searchKeyword(searchResult, false);

        CnkiResult cnkiResult = new CnkiResult();
        cnkiResult.setCount(cnki.getTotalNum(url));
        cnkiResult.setCitation(cnki.getCitations(searchResult));
        HashMap cits = cnkiResult.getCitation();
        Integer totalCount = cnkiResult.getCount();
        //获取论文类型并输出清理掉辑刊后的分年引用
        HashMap<String, Integer>[] typesAndCits = cnki.getTypes(searchResult, cits, totalCount);
        cnkiResult.setType(typesAndCits[0]);
        //清理分年引用辑刊
        cnkiResult.setCitation(typesAndCits[1]);
        //清理总结果辑刊
        cnkiResult.setCount(typesAndCits[2].get("count"));
        cnkiResult.setSelfCitation(cnki.getAuthorSelfCitaion(searchResult));
        cnkiResult.setSelfInstituteCitation(cnki.getSelfInstituteCitation(searchResult));
        System.out.println(cnkiResult);
    }
}

