package mains;

import excel.ExcelReader;
import pojo.SearchResult;
import spider.DangdangSpider;
import spider.DoubanSpider;

/**
 * Created by Administrator on 2016/9/15.
 */
public class MainDangdang {
    public static void main(String[] args) throws Exception {
        //当当
        ExcelReader reader = new ExcelReader();
        DangdangSpider dangdangSpider = new DangdangSpider();
        SearchResult[] searchResults = reader.reader("resources/source-init.xls");
        Integer dangdang;
        for (int i = 0; i < searchResults.length; i++) {
            dangdang = dangdangSpider.getCommentsByParams(searchResults[i]);
            reader.writeDangdang(dangdang, searchResults[i]);
            Thread.sleep(3000);
        }
    }
}
