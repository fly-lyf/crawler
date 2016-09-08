package spider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pojo.CnkiResult;
import pojo.SearchResult;

/**
 * @author zhouyi
 * @date 2015-8-31 上午11:06:09
 */
public class CnkiSpider {

    //主查询

    /**
     *
     * @param param 查询条件对象
     * @param flag 加不加出版时间的标志位
     * @return
     * @throws IOException
     */
    public String searchKeyword(SearchResult param, int flag) throws IOException {
        //标题 名字 出版社 时间
        String key1 = param.getTitle();
        String key2 = param.getAuthor();
        String key3 = param.getPublisher();
        String key4 = null;
        if (param.getPubTime() != null && flag == 1) {
            key4 = param.getPubTime().toString();
        } else {
            key4 = "";
        }
//        String year2015 = "&publishdate_from=2015-01-01&publishdate_to=2015-12-31";
        System.out.println("___________________");
        System.out.println("文献名称:" + key1 + "   作者: " + key2 + "   出版社: " + key3 + "   出版时间: " + key4);

        String url = "";
        try {
            if (key1 != "") {
                url = "http://epub.cnki.net/KNS/request/SearchHandler.ashx?action=&NaviCode=*&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCJFN%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND%2CCCJD%2CHBRD&base_special1=%25&magazine_special1=%25&txt_1_sel=RF&txt_1_value1=" + URLEncoder.encode(key1, "utf-8") + "&txt_1_relation=%23CNKI_AND&txt_1_special1=%3D";
                if (key2 == null) {
                    url = url + "&his=0&__=Mon%20Aug%2031%202015%2010%3A52%3A39%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
                }
            }

            if (key1 != "" && key2 != "") {

                url = url + "&txt_2_sel=RF&txt_2_value1=" + URLEncoder.encode(key2, "utf-8") + "&txt_2_logical=and&txt_2_relation=%23CNKI_AND&txt_2_special1=%3D";
                if (key3 == null) {
                    url = url + "&his=0&__=Mon%20Aug%2031%202015%2010%3A52%3A39%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
                }
            }

            if (key1 != "" && key2 != "" && key3 != "") {
                url = url + "&txt_3_sel=RF&txt_3_value1=" + URLEncoder.encode(key3, "utf-8") + "&txt_3_logical=and&txt_3_relation=%23CNKI_AND&txt_3_special1=%3D";
                if (key4 == null) {
                    url = url + "&his=0&__=Mon%20Aug%2031%202015%2010%3A52%3A39%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
                }
            }

            if (key1 != "" && key2 != "" && key3 != "" && key4 != "") {
                url = url + "&txt_4_sel=RF&txt_4_value1=" + URLEncoder.encode(key4, "utf-8") + "&txt_4_logical=and&txt_4_relation=%23CNKI_AND&txt_4_special1=%3D&his=0&__=Mon%20Aug%2031%202015%2010%3A52%3A39%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        getEntity(url);
        try {
            return "http://epub.cnki.net/kns/brief/brief.aspx?pagename=ASP.brief_result_aspx&dbPrefix=SCDB&dbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&research=off&t=1440989559685&keyValue=" + URLEncoder.encode(key1, "utf-8") + "&S=1";
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    //总结果数、论文类型，论文链接
    public CnkiResult getHtml(String url) throws IOException {
        CnkiResult cnkiResult = new CnkiResult();
        String resText = getEntity(url);

        //总结果数量
        Pattern patternCount = Pattern.compile("找到&nbsp;(.*?)&nbsp;条结果&nbsp;");
        Matcher matcherCount = patternCount.matcher(resText);
        if (matcherCount.find()) {
            String countCount = matcherCount.group().substring(8, matcherCount.group().indexOf("&", 8));
            countCount = countCount.replaceAll(",", "");
            System.out.print("找到" + countCount + "个结果  ");
            cnkiResult.setCount(countCount);
        }

        //论文类型
        Pattern patternDB = Pattern.compile("<td(\\s*)class=\"tdrigtxt\">([^\\<]*[辑刊期刊博士硕士会议][^\\<]*)*</td>");
        Matcher matcherDB = patternDB.matcher(resText);
        List<String> DBList = new ArrayList<String>();
        int i = 0;
        while (matcherDB.find()) {
            String countDB = matcherDB.group().substring(32, 34);
//            System.out.print("类型是：" + countDB + "  ");
            DBList.add(i, countDB);
            i++;
        }
        //论文链接
        Pattern patternTitle = Pattern.compile("<a(\\s*)class=\"fz14\"(\\s*)href='(.*?)'");
        Matcher matcherTitle = patternTitle.matcher(resText);
        int j = 0;
        List<String> titleList = new ArrayList<String>();
        while (matcherTitle.find()) {
            String countTitle = matcherTitle.group().substring(22, matcherTitle.group().lastIndexOf("'"));
            countTitle = countTitle.replaceAll("kns", "KCMS");
            countTitle = "http://www.cnki.net" + countTitle;
//            System.out.println("论文链接："+countTitle+ "   ");
            titleList.add(j, countTitle);
            j++;
        }

        String urlPage = "";
        //翻第二页
        Pattern patternPage = Pattern.compile("<a href=\"[?]curpage=2[^\\#]*");
        Matcher matcherPage = patternPage.matcher(resText);
        if (matcherPage.find()) {
            String urlParam = matcherPage.group().substring(9, matcherPage.group().length());
            urlPage = "http://epub.cnki.net/kns/brief/brief.aspx" + urlParam;
            String resText1 = getEntity(urlPage);
//        System.out.println(resText1);
            //论文类型
            patternDB = Pattern.compile("<td(\\s*)class=\"tdrigtxt\">([^\\<]*[辑刊期刊博士硕士会议][^\\<]*)*</td>");
            matcherDB = patternDB.matcher(resText1);
//        System.out.println(matcherDB);
            while (matcherDB.find()) {
                String countDB = matcherDB.group().substring(32, 34);
//                System.out.print("类型是：" + countDB + "   ");
                DBList.add(i, countDB);
                i++;
            }

            //论文链接
            patternTitle = Pattern.compile("<a(\\s*)class=\"fz14\"(\\s*)href='(.*?)'");
            matcherTitle = patternTitle.matcher(resText1);
            while (matcherTitle.find()) {
                String countTitle = matcherTitle.group().substring(22, matcherTitle.group().lastIndexOf("'"));
                countTitle = countTitle.replaceAll("kns", "KCMS");
                countTitle = "http://www.cnki.net" + countTitle;
//                System.out.println("论文链接：" + countTitle + "   ");
                titleList.add(j, countTitle);
                j++;
            }
        }

        //翻第三页
        patternPage = Pattern.compile("<a href=\"[?]curpage=3[^\\#]*");
        matcherPage = patternPage.matcher(resText);
        if (matcherPage.find()) {
            String urlParam = matcherPage.group().substring(9, matcherPage.group().length());
            urlPage = "http://epub.cnki.net/kns/brief/brief.aspx" + urlParam;
            String resText1 = getEntity(urlPage);
//        System.out.println(resText1);
            //论文类型
            patternDB = Pattern.compile("<td(\\s*)class=\"tdrigtxt\">([^\\<]*[辑刊期刊博士硕士会议][^\\<]*)*</td>");
            matcherDB = patternDB.matcher(resText1);
//        System.out.println(matcherDB);
            while (matcherDB.find()) {
                String countDB = matcherDB.group().substring(32, 34);
//                System.out.print("类型是：" + countDB + "   ");
                DBList.add(i, countDB);
                i++;
            }

            //论文链接
            patternTitle = Pattern.compile("<a(\\s*)class=\"fz14\"(\\s*)href='(.*?)'");
            matcherTitle = patternTitle.matcher(resText1);
            while (matcherTitle.find()) {
                String countTitle = matcherTitle.group().substring(22, matcherTitle.group().lastIndexOf("'"));
                countTitle = countTitle.replaceAll("kns", "KCMS");
                countTitle = "http://www.cnki.net" + countTitle;
//                System.out.println("论文链接：" + countTitle + "   ");
                titleList.add(j, countTitle);
                j++;
            }
        }

        //翻第四页
        patternPage = Pattern.compile("<a href=\"[?]curpage=4[^\\#]*");
        matcherPage = patternPage.matcher(resText);
        if (matcherPage.find()) {
            String urlParam = matcherPage.group().substring(9, matcherPage.group().length());
            urlPage = "http://epub.cnki.net/kns/brief/brief.aspx" + urlParam;
            String resText1 = getEntity(urlPage);
//        System.out.println(resText1);
            //论文类型
            patternDB = Pattern.compile("<td(\\s*)class=\"tdrigtxt\">([^\\<]*[辑刊期刊博士硕士会议][^\\<]*)*</td>");
            matcherDB = patternDB.matcher(resText1);
//        System.out.println(matcherDB);
            while (matcherDB.find()) {
                String countDB = matcherDB.group().substring(32, 34);
//                System.out.print("类型是：" + countDB + "   ");
                DBList.add(i, countDB);
                i++;
            }

            //论文链接
            patternTitle = Pattern.compile("<a(\\s*)class=\"fz14\"(\\s*)href='(.*?)'");
            matcherTitle = patternTitle.matcher(resText1);
            while (matcherTitle.find()) {
                String countTitle = matcherTitle.group().substring(22, matcherTitle.group().lastIndexOf("'"));
                countTitle = countTitle.replaceAll("kns", "KCMS");
                countTitle = "http://www.cnki.net" + countTitle;
//                System.out.println("论文链接：" + countTitle + "   ");
                titleList.add(j, countTitle);
                j++;
            }
        }

        //翻第五页
        patternPage = Pattern.compile("<a href=\"[?]curpage=5[^\\#]*");
        matcherPage = patternPage.matcher(resText);
        if (matcherPage.find()) {
            String urlParam = matcherPage.group().substring(9, matcherPage.group().length());
            urlPage = "http://epub.cnki.net/kns/brief/brief.aspx" + urlParam;
            String resText1 = getEntity(urlPage);
//        System.out.println(resText1);
            //论文类型
            patternDB = Pattern.compile("<td(\\s*)class=\"tdrigtxt\">([^\\<]*[辑刊期刊博士硕士会议][^\\<]*)*</td>");
            matcherDB = patternDB.matcher(resText1);
//        System.out.println(matcherDB);
            while (matcherDB.find()) {
                String countDB = matcherDB.group().substring(32, 34);
//                System.out.print("类型是：" + countDB + "   ");
                DBList.add(i, countDB);
                i++;
            }

            //论文链接
            patternTitle = Pattern.compile("<a(\\s*)class=\"fz14\"(\\s*)href='(.*?)'");
            matcherTitle = patternTitle.matcher(resText1);
            while (matcherTitle.find()) {
                String countTitle = matcherTitle.group().substring(22, matcherTitle.group().lastIndexOf("'"));
                countTitle = countTitle.replaceAll("kns", "KCMS");
                countTitle = "http://www.cnki.net" + countTitle;
//                System.out.println("论文链接：" + countTitle + "   ");
                titleList.add(j, countTitle);
                j++;
            }
        }

        //翻第六页
        patternPage = Pattern.compile("<a href=\"[?]curpage=6[^\\#]*");
        matcherPage = patternPage.matcher(resText);
        if (matcherPage.find()) {
            String urlParam = matcherPage.group().substring(9, matcherPage.group().length());
            urlPage = "http://epub.cnki.net/kns/brief/brief.aspx" + urlParam;
            String resText1 = getEntity(urlPage);
//        System.out.println(resText1);
            //论文类型
            patternDB = Pattern.compile("<td(\\s*)class=\"tdrigtxt\">([^\\<]*[辑刊期刊博士硕士会议][^\\<]*)*</td>");
            matcherDB = patternDB.matcher(resText1);
//        System.out.println(matcherDB);
            while (matcherDB.find()) {
                String countDB = matcherDB.group().substring(32, 34);
//                System.out.print("类型是：" + countDB + "   ");
                DBList.add(i, countDB);
                i++;
            }

            //论文链接
            patternTitle = Pattern.compile("<a(\\s*)class=\"fz14\"(\\s*)href='(.*?)'");
            matcherTitle = patternTitle.matcher(resText1);
            while (matcherTitle.find()) {
                String countTitle = matcherTitle.group().substring(22, matcherTitle.group().lastIndexOf("'"));
                countTitle = countTitle.replaceAll("kns", "KCMS");
                countTitle = "http://www.cnki.net" + countTitle;
//                System.out.println("论文链接：" + countTitle + "   ");
                titleList.add(j, countTitle);
                j++;
            }
        }

        String[] DBString = DBList.toArray(new String[DBList.size()]);
        cnkiResult.setType(DBString);
        String[] urls = titleList.toArray(new String[titleList.size()]);
        cnkiResult.setUrl(urls);


        return cnkiResult;
    }

    //获取按年度引用
    public Integer[] getCitations(SearchResult searchResult) throws IOException {
        Integer[] cits = new Integer[9];
        String urlYear = "http://epub.cnki.net/kns/group/DoGroupLeft.ashx?action=1&Param=ASP.brief_result_aspx%23SCDB/%E5%8F%91%E8%A1%A8%E5%B9%B4%E5%BA%A6/%e5%b9%b4%2Ccount%28*%29/%e5%b9%b4/%28%e5%b9%b4%2C%27date%27%29%23%e5%b9%b4%24desc/1000000%24/-/40/40000/ButtonView&cid=0&clayer=0&isAutoInit=1&__=Wed%20Sep%2007%202016%2016%3A14%3A50%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        String resYear = getEntity(urlYear);
        Document docYear = Jsoup.parse(resYear);
        Elements eleYearNode = docYear.select("span[class=GroupItemLinkBlue] a");
        String[] yearsStr = eleYearNode.text().split(" ");
        Elements timesNode = docYear.select("span[style=color:#999;]");
        String[] timesStr = timesNode.text().split(" ");
        for (int i = 0; i < timesStr.length; i++) {
            String time = timesStr[i];
            timesStr[i] = time.substring(1, time.indexOf(")"));
        }
        Integer[] timesInt = new Integer[timesStr.length];
        for (int i = 0; i < timesStr.length; i++) {
            timesInt[i] = Integer.parseInt(timesStr[i]);
        }
        Integer[] yearsInt = new Integer[yearsStr.length];
        for (int i = 0; i < yearsStr.length; i++) {
            yearsInt[i] = Integer.parseInt(yearsStr[i]);
        }
        return cits;
    }

    //获取机构自引和自引
    public Integer[] getSelfCitation(String[] urls, SearchResult param) throws IOException {
        List<String> urlList = new ArrayList<String>();
        Pattern pattern = null;
        Matcher matcher = null;
        Integer selfCitation = 0;
        Integer selfAddCitation = 0;
        for (int i = 0; i < urls.length; i++) {
            String resText = getEntity(urls[i]);
//            System.out.println(resText);
            //自引数
            pattern = Pattern.compile("((\\s*))【作者】((\\s*))<a class=\"KnowledgeNetLink\"([\\s\\S]*?)</p>");
            matcher = pattern.matcher(resText);
            if (matcher.find()) {
                String nameStr = matcher.group();
                pattern = Pattern.compile("<a class=\"KnowledgeNetLink\"([^\\<]*)");
                matcher = pattern.matcher(nameStr);
                while (matcher.find()) {
                    String name = matcher.group();
//                    System.out.println(name);
                    name = name.substring(name.lastIndexOf(">") + 1, name.length());
//                    System.out.println(name);
                    if (name.contains(param.getAuthor())) {
                        selfCitation++;
                    }
                }
            }

            //机构自引数
            pattern = Pattern.compile("((\\s*))【机构】((\\s*))<a class=\"KnowledgeNetLink\"([\\s\\S]*?)</p>");
            matcher = pattern.matcher(resText);
            if (matcher.find()) {
                String addrStr = matcher.group();
                pattern = Pattern.compile("<a class=\"KnowledgeNetLink\"([^\\<]*)");
                matcher = pattern.matcher(addrStr);
//                System.out.println(addrStr);
                while (matcher.find()) {
                    String addr = matcher.group();
//                    System.out.println(addr);
                    addr = addr.substring(addr.lastIndexOf(">") + 1, addr.length());
//                    System.out.println(addr);
                    if (addr.contains(param.getAddress())) {
                        selfAddCitation++;
                    }
                }
            }
        }
        System.out.println("自引：" + selfCitation + "   机构自引：" + selfAddCitation);
        Integer[] result = {selfCitation, selfAddCitation};
        return result;
    }

    //拉取报纸评论并提取数量
    public String searchPaper(String key) throws IOException {
        String url = "http://epub.cnki.net/KNS/request/SearchHandler.ashx?action=&NaviCode=*&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCJFN%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND%2CCCJD%2CHBRD&base_special1=%25&magazine_value1=%E5%85%89%E6%98%8E%E6%97%A5%E6%8A%A5%2B%E6%96%B0%E5%8D%8E%E6%AF%8F%E6%97%A5%E7%94%B5%E8%AE%AF%2B%E6%96%87%E6%B1%87%E6%8A%A5%2B%E4%B8%AD%E5%9B%BD%E7%A4%BE%E4%BC%9A%E7%A7%91%E5%AD%A6%E6%8A%A5&magazine_special1=%3D&txt_1_sel=SU&txt_1_value1=" + URLEncoder.encode(key, "utf-8") + "&txt_1_relation=%23CNKI_AND&txt_1_special1=%3D&his=0&__=Tue%20Sep%2015%202015%2020%3A59%3A58%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        CnkiResult cnkiResult = new CnkiResult();
//            System.out.println(url);
        getEntity(url);
        url = "http://epub.cnki.net/kns/brief/brief.aspx?pagename=ASP.brief_result_aspx&dbPrefix=SCDB&dbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&research=off&t=1442322612064&keyValue=\" + URLEncoder.encode(key, \"utf-8\") + \"&S=1";
        String resText = getEntity(url);
        Document doc = Jsoup.parse(resText);
        Elements pagerTitleCell = doc.select("div[class=pagerTitleCell]");
        String result = pagerTitleCell.get(0).text();
        result = result.substring(result.indexOf("到") + 2, result.indexOf("条") - 1);
        return result;
    }

    //拉取学术评论，并提取数量
    public String searchComment(String key) throws IOException {
        CnkiResult cnkiResult = new CnkiResult();
        String url = "http://epub.cnki.net/KNS/request/SearchHandler.ashx?action=&NaviCode=*&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCJFN%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND%2CCCJD%2CHBRD&base_special1=%25&magazine_special1=%25&txt_1_sel=SU&txt_1_value1=" + URLEncoder.encode(key, "utf-8") + "&txt_1_relation=%23CNKI_AND&txt_1_special1=%3D&his=0&__=Wed%20Sep%2016%202015%2001%3A08%3A25%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)";
        getEntity(url);
        url = "http://epub.cnki.net/kns/brief/brief.aspx?pagename=ASP.brief_result_aspx&dbPrefix=SCDB&dbCatalog=%e4%b8%ad%e5%9b%bd%e5%ad%a6%e6%9c%af%e6%96%87%e7%8c%ae%e7%bd%91%e7%bb%9c%e5%87%ba%e7%89%88%e6%80%bb%e5%ba%93&ConfigFile=SCDB.xml&research=off&t=1442322612064&keyValue=\" + URLEncoder.encode(key, \"utf-8\") + \"&S=1";
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
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        String cookie = "RsPerPage=20; ASP.NET_SessionId=d2ebk255c1rilq55qryct245; kc_cnki_net_uid=081f9718-f8e2-02aa-488e-eb800f5d12ed; Ecp_ClientId=3160831131701137240; LID=WEEvREcwSlJHSldRa1FhdXNXZjNkWmtXQzdwUHhaTERsY1dFcXp2L3NTOD0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4ggI8Fm4gTkoUKaID8j8gFw!!; c_m_LinID=LinID=WEEvREcwSlJHSldRa1FhdXNXZjNkWmtXQzdwUHhaTERsY1dFcXp2L3NTOD0=$9A4hF_YAuvQ5obgVAqNKPCYcEjKensW4ggI8Fm4gTkoUKaID8j8gFw!!&ot=09/08/2016 14:14:48; c_m_expire=2016-09-08 14:14:48; Ecp_LoginStuts={\"IsAutoLogin\":false,\"UserName\":\"sh0301\",\"ShowName\":\"%e5%8d%97%e4%ba%ac%e5%a4%a7%e5%ad%a6\",\"UserType\":\"bk\",\"r\":\"ekZVZW\"}";
        httpGet.setHeader("Cookie", cookie);
        httpGet.setHeader("Host", "epub.cnki.net");
        httpGet.setHeader("Referer", "http://epub.cnki.net/kns/brief/result.aspx?dbprefix=scdb&action=scdbsearch&db_opt=SCDB");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String resText = EntityUtils.toString(entity, "utf-8");
        return resText;
    }

    //单元测试
    public static void main(String[] args) throws IOException {
        CnkiSpider cnki = new CnkiSpider();
        SearchResult searchResult = new SearchResult("马克思主义理论与实践:霍布斯鲍姆史学研究","梁民愫","社会科学文献出版社",2009);
        String url = cnki.searchKeyword(searchResult,1);
        CnkiResult cnkiResult = cnki.getHtml(url);
        Integer[] citations = cnki.getCitations(searchResult);
        cnkiResult.setCitation(citations);
        Integer[] selfCitations = cnki.getSelfCitation(cnkiResult.getUrl(), searchResult);
        cnkiResult.setSelfCitation(selfCitations[0]);
        cnkiResult.setSelfAddCitation(selfCitations[1]);
        System.out.println(cnkiResult);
    }
}

