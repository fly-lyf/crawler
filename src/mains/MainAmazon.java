package mains;

import excel.ExcelProcess;
import pojo.SearchResult;

/**
 * Created by Administrator on 2016/9/15.
 */
public class MainAmazon {
    public static void main(String[] args) throws Exception {
        //亚马逊
        ExcelProcess reader = new ExcelProcess();
        SearchResult[] searchResults = reader.reader("resources/source-init-part1.xls");
        Double[] amazon;
        for(int i=0;i<searchResults.length;i++) {
            spider.AmazonSpider amazonSpider = new spider.AmazonSpider();
            amazon = amazonSpider.requestAmazon(searchResults[i]);
            reader.writeAmazon(amazon,searchResults[i]);
            Thread.sleep(4000);
        }
    }
}
