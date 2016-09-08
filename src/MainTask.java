import excel操作.DuplicateCNKI;
import pojo.CnkiResult;
import pojo.GTResult;
import pojo.SearchResult;
import spider.CnkiSpider;
import spider.DangdangSpider;
import spider.DoubanSpider;
import spider.NlcSpider;

/**
 * Created by Administrator on 2015/8/18.
 */
public class MainTask {

    public static void main(String[] args) throws Exception {

        //cnki 总被引，自引，机构自引，论文类型，不能处理分年度的被引数据
        ExcelReader reader = new ExcelReader();
        SearchResult[] searchResults = reader.reader();
        for(int i=0;i<searchResults.length;i++) {
            CnkiSpider cnki = new CnkiSpider();
            CnkiResult cnkiResult;
            CnkiResult cnkiResult1;
            CnkiResult citation;
            cnki.getSessionId();
            //无出版年
//            String url = cnki.searchKeyword(searchResults[i],0);
//            cnkiResult = cnki.getCitations(searchResults[i]);
//            cnkiResult = cnki.getHtml(url);
//            citation = cnki.getSelfCitation(cnkiResult, searchResults[i]);
//            cnkiResult.setSelfCitation(citation.getSelfCitation());
//            cnkiResult.setSelfAddCitation(citation.getSelfAddCitation());
            //有出版年
            String url1 = cnki.searchKeyword(searchResults[i],1);
            cnkiResult1 = cnki.getHtml(url1);
            System.out.println(cnkiResult1);
            citation = cnki.getSelfCitation(cnkiResult1, searchResults[i]);
            cnkiResult1.setSelfCitation(citation.getSelfCitation());
            cnkiResult1.setSelfAddCitation(citation.getSelfAddCitation());
            //要加参数写入
//            reader.writeCNKI(cnkiResult,searchResults[i],0);
            reader.writeCNKI(cnkiResult1,searchResults[i],1);
            Thread.sleep(6000);
        }

//         cnki webdriver 分年度获取被引数量
//        ExcelReader reader = new ExcelReader();
//        pojo.SearchResult params[] = reader.reader();
//        for(int i=0;i<params.length;i++){
//            pojo.SearchResult searchResult;
//            TestWebDriver web = new TestWebDriver();
//            searchResult = web.getResult(params[i]);
//            reader.writer(searchResult,0);
//            searchResult = web.getTimeResult(params[i]);
//            reader.writer(searchResult,1);
//            Thread.sleep(4000);
//        }

        //豆瓣
//        ExcelReader reader = new ExcelReader();
//        pojo.SearchResult[] searchResults = reader.reader();
//        Double[] results = null;
//        for(int i=0;i<searchResults.length;i++) {
//            DoubanSpider douban = new DoubanSpider();
//            results = douban.requestDouban(searchResults[i]);
//            reader.writeDouBan(results);
//            Thread.sleep(5000);
//        }


        //当当
//        ExcelReader reader = new ExcelReader();
//        TestWebDriver web = new TestWebDriver();
//        pojo.SearchResult[] searchResults = reader.reader();
//        Integer dangdang;
//        for(int i=0;i<searchResults.length;i++) {
//            dangdang = web.getDangDangResult(searchResults[i]);
//            reader.writeDangdang(dangdang,searchResults[i]);
//        }

        //当当spider
//        ExcelReader reader = new ExcelReader();
//        spider.DangdangSpider dangdangSpider = new spider.DangdangSpider();
//        SearchResult[] searchResults = reader.reader();
//        Integer dangdang;
//        for(int i=0;i<searchResults.length;i++) {
//            dangdang = dangdangSpider.getParams(searchResults[i]);
//            reader.writeDangdang(dangdang,searchResults[i]);
//            Thread.sleep(5000);
//        }

        //亚马逊
//        ExcelReader reader = new ExcelReader();
//        SearchResult[] searchResults = reader.reader();
//        Double[] amazon;
//        for(int i=0;i<searchResults.length;i++) {
//            spider.AmazonSpider amazonSpider = new spider.AmazonSpider();
//            amazon = amazonSpider.getParams(searchResults[i]);
//            reader.writeAmazon(amazon);
//            Thread.sleep(5000);
//        }


        //cnki数据去重
//        DuplicateCNKI duplicateCNKI = new DuplicateCNKI();
//        duplicateCNKI.duplicate();

        //国图的图书信息
//        ExcelReader reader = new ExcelReader();
//        SearchResult[] searchResults = reader.readOthers();
//        GTResult gtResult = new GTResult();
//        NlcSpider nlcSpider = new NlcSpider();
//        SearchResult searchResult = new SearchResult();
//        searchResult.setTitle("大国崛起制高点");
//        searchResult.setPublisher("人民出版社");
//        searchResult.setAuthor("胡雪梅");
//        String url = "";
//        url = nlcSpider.getParams(searchResult);
//        for(int i=0;i<searchResults.length;i++) {
//
//            url = nlcSpider.getParams(searchResults[i]);
//            if(url != null) {
//                gtResult = nlcSpider.getTable(url);
//                reader.writeNlc(gtResult);
//            }else{
//                reader.writeNlc(null);
//            }
//
//            Thread.sleep(3000);
//        }

        //当当的图书信息
//        ExcelReader reader = new ExcelReader();
//        GTResult gtResult = new GTResult();
//        SearchResult[] searchResults = reader.readOthers();
//        DangdangSpider dangdangSpider = new DangdangSpider();
////        SearchResult searchResult = new SearchResult();
////        searchResult.setTitle("《青史》（足本）");
////        searchResult.setAuthor("王启龙");
////        searchResult.setPublisher("中国社会科学出版社");
//        String url = "";
////        url = dangdangSpider.getParams(searchResult);
////        gtResult = dangdangSpider.getTable(url);
//        for(int i=0;i<searchResults.length;i++) {
//            url = dangdangSpider.getParams(searchResults[i]);
//            if(url != null && url != "") {
//                Thread.sleep(2000);
//                gtResult = dangdangSpider.getTable(url);
//                reader.writeDangdangSpider(gtResult);
//            }else{
//                reader.writeDangdangSpider(null);
//            }
//            Thread.sleep(2000);
//        }
        //报纸评论
//        ExcelReader reader = new ExcelReader();
//        CnkiSpider cnkiSpider = new CnkiSpider();
//        cnkiSpider.getSessionId();
//        String result = "";
//        SearchResult[] searchResults = reader.reader();
//
//        for (int i = 0; i < searchResults.length; i++) {
//            String[] keys = null;
//            if (searchResults[i].getTitle().contains("——")) {
//                keys = searchResults[i].getTitle().split("——");
//            } else if (searchResults[i].getTitle().contains(":")) {
//                keys = searchResults[i].getTitle().split(":");
//            } else if (searchResults[i].getTitle().contains("：")) {
//                keys = searchResults[i].getTitle().split("：");
//            } else if (searchResults[i].getTitle().contains("--")) {
//                keys = searchResults[i].getTitle().split("--");
//            }
//            if (keys != null) {
//                for (int j = 0; j < keys.length; j++) {
//                    String url = cnkiSpider.searchPaper(keys[j]);
//                    result = cnkiSpider.getPaperCount(url);
//                    System.out.println(keys[j] + "   " + result + "   报纸评论——第" + (i + 1) + "部书的分title");
//                    reader.writeCnkiDetail(result, keys[j], searchResults[i].getTitle());
//                    Thread.sleep(3000);
//                }
//            }
//            String url = cnkiSpider.searchPaper(searchResults[i].getTitle());
//            result = cnkiSpider.getPaperCount(url);
//            System.out.println(searchResults[i].getTitle() + "   " + result + "   报纸评论——第" + (i + 1) + "部书");
//            reader.writeCnkiDetail(result, searchResults[i].getTitle(), null);
//            Thread.sleep(3000);
//        }

        //学术评论
//        ExcelReader reader = new ExcelReader();
//        CnkiSpider cnkiSpider = new CnkiSpider();
//        cnkiSpider.getSessionId();
//        String result = "";
//        SearchResult[] searchResults = reader.reader();
//
//        for (int i = 0; i < searchResults.length; i++) {
//            String[] keys = null;
//            if (searchResults[i].getTitle().contains("——")) {
//                keys = searchResults[i].getTitle().split("——");
//            } else if (searchResults[i].getTitle().contains(":")) {
//                keys = searchResults[i].getTitle().split(":");
//            } else if (searchResults[i].getTitle().contains("：")) {
//                keys = searchResults[i].getTitle().split("：");
//            } else if (searchResults[i].getTitle().contains("--")) {
//                keys = searchResults[i].getTitle().split("--");
//            }
//            if (keys != null) {
//                for (int j = 0; j < keys.length; j++) {
//                    String url = cnkiSpider.searchComment(keys[j]);
//                    result = cnkiSpider.getCommentCount(url);
//                    System.out.println(keys[j] + "   " + result + "   学术评论——第" + (i + 1) + "部书的分title");
//                    reader.writeCnkiDetail(result, keys[j], searchResults[i].getTitle());
//                    Thread.sleep(3000);
//                }
//            }
//            String url = cnkiSpider.searchComment(searchResults[i].getTitle());
//            result = cnkiSpider.getCommentCount(url);
//            System.out.println(searchResults[i].getTitle() + "   " + result + "   学术评论——第" + (i + 1) + "部书");
//            reader.writeCnkiDetail(result, searchResults[i].getTitle(), null);
//            Thread.sleep(3000);
//        }
        //表一
//        ExcelReader excelReader = new ExcelReader();
//        String result[][] = excelReader.readConvert();
//        for(int i = 1; i<result.length;i++){
//            excelReader.convert(result[i]);
//        }

        //首次被引间隔
//        ExcelReader excelReader = new ExcelReader();
//        Integer result[] = excelReader.readFinal();

        //表二

    }
}
