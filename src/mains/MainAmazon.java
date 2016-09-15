package mains;

import excel.ExcelReader;
import pojo.SearchResult;
import spider.DangdangSpider;

/**
 * Created by Administrator on 2016/9/15.
 */
public class MainAmazon {
    public static void main(String[] args) throws Exception {
        //亚马逊
        ExcelReader reader = new ExcelReader();
        SearchResult[] searchResults = reader.reader("resources/source-init-part.xls");
        Double[] amazon;
        for(int i=0;i<searchResults.length;i++) {
            spider.AmazonSpider amazonSpider = new spider.AmazonSpider();
            amazon = amazonSpider.requestAmazon(searchResults[i]);
            reader.writeAmazon(amazon,searchResults[i]);
            Thread.sleep(4000);
        }
    }
}
