package mains;

import excel.ExcelProcess;
import spider.DoubanSpider;

/**
 * Created by Administrator on 2016/9/15.
 */
public class MainDouban {
    public static void main(String[] args) throws Exception {
        //豆瓣
        ExcelProcess reader = new ExcelProcess();
        pojo.SearchResult[] searchResults = reader.reader("resources/source-init-part.xls");
        Double[] results = null;
        for (int i = 0; i < searchResults.length; i++) {
            DoubanSpider douban = new DoubanSpider();
            results = douban.requestDouban(searchResults[i]);
            reader.writeDouBan(searchResults[i], results);
            Thread.sleep(4000);
        }
    }
}
