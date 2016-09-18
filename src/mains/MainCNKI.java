package mains;

import excel.ExcelProcess;
import pojo.CnkiResult;
import pojo.SearchResult;
import spider.CnkiSpider;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/16.
 */
public class MainCNKI {
    //论文引用情况
    public void paperRun() throws Exception {
        //cnki 总被引，自引，机构自引，论文类型分年度的被引数据
        ExcelProcess reader = new ExcelProcess();
        SearchResult[] searchResults = reader.reader("resources/source-init.xls");
        for (int i = 0; i < searchResults.length; i++) {
            CnkiSpider cnki = new CnkiSpider();
            //全文标题
            String urlTotalTitle = cnki.searchKeyword(searchResults[i], true);
            CnkiResult cnkiResult = new CnkiResult();
            cnkiResult.setCount(cnki.getTotalNum(urlTotalTitle));
            Thread.sleep(500);
            cnkiResult.setCitation(cnki.getCitations(searchResults[i]));
            Thread.sleep(500);
            Integer totalCount = cnkiResult.getCount();
            HashMap cits = cnkiResult.getCitation();
            //获取论文类型并输出清理掉辑刊
            HashMap<String, Integer>[] typesAndCits = cnki.getTypes(searchResults[i], cits, totalCount);
            cnkiResult.setType(typesAndCits[0]);
            //清理分年引用辑刊、总结果数辑刊
            cnkiResult.setCitation(typesAndCits[1]);
            cnkiResult.setCount(typesAndCits[2].get("count"));
            Thread.sleep(500);
            cnkiResult.setSelfCitation(cnki.getAuthorSelfCitaion(searchResults[i]));
            Thread.sleep(500);
            cnkiResult.setSelfInstituteCitation(cnki.getSelfInstituteCitation(searchResults[i]));
            Thread.sleep(500);
            reader.writeCNKI(cnkiResult, searchResults[i]);
            Thread.sleep(500);

            //有出版年

            String urlYear = cnki.searchKeyword(searchResults[i], false);
            CnkiResult cnkiResultYear = new CnkiResult();
            cnkiResultYear.setCount(cnki.getTotalNum(urlYear));
            Thread.sleep(500);
            cnkiResultYear.setCitation(cnki.getCitations(searchResults[i]));
            Thread.sleep(500);
            Integer totalCountYear = cnkiResultYear.getCount();
            HashMap citsYear = cnkiResultYear.getCitation();
            //获取论文类型并输出清理掉辑刊
            HashMap<String, Integer>[] typesAndCitsYear = cnki.getTypes(searchResults[i], citsYear, totalCountYear);
            cnkiResultYear.setType(typesAndCitsYear[0]);
            //清理分年引用辑刊、总结果数辑刊
            cnkiResultYear.setCitation(typesAndCitsYear[1]);
            cnkiResultYear.setCount(typesAndCitsYear[2].get("count"));
            Thread.sleep(500);
            cnkiResultYear.setSelfCitation(cnki.getAuthorSelfCitaion(searchResults[i]));
            Thread.sleep(500);
            cnkiResultYear.setSelfInstituteCitation(cnki.getSelfInstituteCitation(searchResults[i]));
            Thread.sleep(500);
            reader.writeCNKI(cnkiResultYear, searchResults[i]);
            Thread.sleep(500);
        }
    }

    //报纸评论
    public void newpaperCommentRun() throws Exception {
        //报纸评论
        ExcelProcess reader = new ExcelProcess();
        CnkiSpider cnkiSpider = new CnkiSpider();
        String result;
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
//            if (keys != null) {
//                for (int j = 0; j < keys.length; j++) {
//                    if (keys[j].length() >= 6) {
//                        result = cnkiSpider.getNewPaperComment(keys[j]);
//                        System.out.println(keys[j] + "   " + result + "   报纸评论——第" + (i + 1) + "部书的分title");
//                        reader.writeCnkiComment(result, 42 + j, i + 1);
//                        Thread.sleep(2000);
//                    }
//                }
//            }
            result = cnkiSpider.getNewPaperComment(searchResults[i].getTitle());
            System.out.println(searchResults[i].getTitle() + "   " + result + "   报纸评论——第" + (i + 1) + "部书的全title");
            reader.writeCnkiComment(result, 30, i + 1);
            Thread.sleep(2000);
        }
    }

    //学术评论
    public void scholarCommentRun() throws Exception {
        //学术评论
        ExcelProcess reader = new ExcelProcess();
        CnkiSpider cnkiSpider = new CnkiSpider();
        String result;
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
//            if (keys != null) {
//                for (int j = 0; j < keys.length; j++) {
//                    if (keys[j].length() >= 6) {
//                        result = cnkiSpider.getScholarComment(keys[j]);
//                        System.out.println(keys[j] + "   " + result + "   学术评论——第" + (i + 1) + "部书的分title");
//                        reader.writeCnkiComment(result, 45 + j, i + 1);
//                        Thread.sleep(2000);
//                    }
//                }
//            }
            result = cnkiSpider.getScholarComment(searchResults[i].getTitle());
            System.out.println(searchResults[i].getTitle() + "   " + result + "   学术评论——第" + (i + 1) + "部书的全title");
            reader.writeCnkiComment(result, 31, i + 1);
            Thread.sleep(2000);
        }
    }

    public static void main(String[] args) throws Exception {
        MainCNKI cnki = new MainCNKI();
//        cnki.paperRun();
//        cnki.newpaperCommentRun();
        cnki.scholarCommentRun();
    }
}
