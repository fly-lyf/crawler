package mains;

import excel.ExcelProcess;
import pojo.GTResult;
import pojo.SearchResult;
import spider.DangdangSpider;

/**
 * Created by Administrator on 2016/9/15.
 */
public class MainDangdang {


    public void dangdangComments() throws Exception {
        //当当
        ExcelProcess reader = new ExcelProcess();
        DangdangSpider dangdangSpider = new DangdangSpider();
        SearchResult[] searchResults = reader.reader("resources/source-init.xls");
        Integer dangdang;
        for (int i = 0; i < searchResults.length; i++) {
            dangdang = dangdangSpider.getCommentsByParams(searchResults[i]);
            reader.writeDangdang(dangdang, searchResults[i]);
            Thread.sleep(3000);
        }
    }
    public static void main(String[] args) throws Exception {
        MainDangdang dangdang = new MainDangdang();
        dangdang.dangdangComments();
//        dangdang.dangdangBookInfo();
    }
}
