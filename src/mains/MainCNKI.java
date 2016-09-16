package mains;

import excel.ExcelProcess;
import pojo.CnkiResult;
import pojo.SearchResult;
import spider.CnkiSpider;

/**
 * Created by Administrator on 2016/9/16.
 */
public class MainCNKI {
    public void paperRun() throws Exception {
        //cnki 总被引，自引，机构自引，论文类型分年度的被引数据
        ExcelProcess reader = new ExcelProcess();
        SearchResult[] searchResults = reader.reader("resources/source-init.xls");
        for (int i = 0; i < searchResults.length; i++) {
            CnkiSpider cnki = new CnkiSpider();
            CnkiResult cnkiResult1;
            //无出版年
            String url = cnki.searchKeyword(searchResults[i]);
            CnkiResult cnkiResult = new CnkiResult();
            cnkiResult.setCount(cnki.getTotalNum(url));
            Thread.sleep(500);
            cnkiResult.setCitation(cnki.getCitations(searchResults[i]));
            Thread.sleep(500);
            cnkiResult.setType(cnki.getTypes(searchResults[i]));
            Thread.sleep(500);
            cnkiResult.setSelfCitation(cnki.getAuthorSelfCitaion(searchResults[i]));
            Thread.sleep(500);
            cnkiResult.setSelfInstituteCitation(cnki.getSelfInstituteCitation(searchResults[i]));
            Thread.sleep(500);
            //有出版年
//            String url1 = cnki.searchKeyword(searchResults[i], 1);
//            cnkiResult1 = cnki.getTotalNum(url1);
//            Integer[] citations = cnki.getCitations(searchResults[i]);
//            cnkiResult1.setCitation(citations);
//            Integer[] selfCitations = cnki.getAuthorSelfCitaion(cnkiResult1.getUrl(), searchResults[i]);
//            cnkiResult1.setSelfCitation(selfCitations[0]);
//            cnkiResult1.setSelfInstituteCitation(selfCitations[1]);

            reader.writeCNKI(cnkiResult, searchResults[i]);
            Thread.sleep(2000);
        }
    }

    public void newpaperCommentRun() throws Exception {
        //报纸评论
        ExcelProcess reader = new ExcelProcess();
        CnkiSpider cnkiSpider = new CnkiSpider();
        String result = "";
        SearchResult[] searchResults = reader.reader("resources/source-init.xls");

        for (int i = 0; i < searchResults.length; i++) {
            String[] keys = null;
            if (searchResults[i].getTitle().contains("——")) {
                keys = searchResults[i].getTitle().split("——");
            } else if (searchResults[i].getTitle().contains(":")) {
                keys = searchResults[i].getTitle().split(":");
            } else if (searchResults[i].getTitle().contains("：")) {
                keys = searchResults[i].getTitle().split("：");
            } else if (searchResults[i].getTitle().contains("--")) {
                keys = searchResults[i].getTitle().split("--");
            }
            if (keys != null) {
                for (int j = 0; j < keys.length; j++) {
                    result = cnkiSpider.getNewPaperComment(keys[j]);
                    System.out.println(keys[j] + "   " + result + "   报纸评论——第" + (i + 1) + "部书的分title");
                    reader.writeCnkiDetail(result, keys[j], searchResults[i].getTitle());
                    Thread.sleep(3000);
                }
            }
            String url = cnkiSpider.getNewPaperComment(searchResults[i].getTitle());
            result = cnkiSpider.getNewPaperComment(url);
            System.out.println(searchResults[i].getTitle() + "   " + result + "   报纸评论——第" + (i + 1) + "部书");
            reader.writeCnkiDetail(result, searchResults[i].getTitle(), null);
            Thread.sleep(3000);
        }
    }

    public void scholarCommentRun() throws Exception {
        //学术评论
        ExcelProcess reader = new ExcelProcess();
        CnkiSpider cnkiSpider = new CnkiSpider();
        String result = "";
        SearchResult[] searchResults = reader.reader("resources/source-init.xls");

        for (int i = 0; i < searchResults.length; i++) {
            String[] keys = null;
            if (searchResults[i].getTitle().contains("——")) {
                keys = searchResults[i].getTitle().split("——");
            } else if (searchResults[i].getTitle().contains(":")) {
                keys = searchResults[i].getTitle().split(":");
            } else if (searchResults[i].getTitle().contains("：")) {
                keys = searchResults[i].getTitle().split("：");
            } else if (searchResults[i].getTitle().contains("--")) {
                keys = searchResults[i].getTitle().split("--");
            }
            if (keys != null) {
                for (int j = 0; j < keys.length; j++) {
                    result = cnkiSpider.getScholarComment(keys[j]);
                    System.out.println(keys[j] + "   " + result + "   学术评论——第" + (i + 1) + "部书的分title");
                    reader.writeCnkiDetail(result, keys[j], searchResults[i].getTitle());
                    Thread.sleep(3000);
                }
            }
            result = cnkiSpider.getScholarComment(searchResults[i].getTitle());
            System.out.println(searchResults[i].getTitle() + "   " + result + "   学术评论——第" + (i + 1) + "部书");
            reader.writeCnkiDetail(result, searchResults[i].getTitle(), null);
            Thread.sleep(3000);
        }
    }

    public static void main(String[] args) throws Exception {
        MainCNKI cnki = new MainCNKI();
        cnki.paperRun();
//        cnki.newpaperCommentRun();
//        cnki.scholarCommentRun();
    }
}
