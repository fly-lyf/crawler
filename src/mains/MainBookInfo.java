package mains;

import excel.ExcelProcess;
import pojo.GTResult;
import pojo.SearchResult;
import spider.DangdangSpider;
import spider.NlcSpider;

/**
 * Created by Administrator on 2015/8/18.
 */
public class MainBookInfo {
    //当当上获取图书信息
    public void dangdangBookInfo() throws Exception {
        ExcelProcess reader = new ExcelProcess();
        SearchResult[] searchResults = reader.reader("resources/source-init.xls");
        DangdangSpider dangdangSpider = new DangdangSpider();
        //拉取书目详情url，这个跟获取评价逻辑类似，之前的叫getPrams，返回一个字符串，已经删掉了
        String url = "";
        for(int i=0;i<searchResults.length;i++) {
            if(url != null && url != "") {
                Thread.sleep(2000);
                GTResult gtResult = dangdangSpider.getBookInfo(url);
                reader.writeDangdangBookInfo(gtResult);
            }else{
                reader.writeDangdangBookInfo(null);
            }
            Thread.sleep(2000);
        }
    }

    //国图的图书信息
    public void nlcBookInfo() throws Exception {
        ExcelProcess reader = new ExcelProcess();
        SearchResult[] searchResults = reader.reader("resources/source-init.xls");
        GTResult gtResult = new GTResult();
        NlcSpider nlcSpider = new NlcSpider();
        SearchResult searchResult = new SearchResult();
        searchResult.setTitle("大国崛起制高点");
        searchResult.setPublisher("人民出版社");
        searchResult.setAuthor("胡雪梅");
        String url = "";
        url = nlcSpider.getParams(searchResult);
        for(int i=0;i<searchResults.length;i++) {
            url = nlcSpider.getParams(searchResults[i]);
            if(url != null) {
                gtResult = nlcSpider.getTable(url);
                reader.writeNlc(gtResult);
            }else{
                reader.writeNlc(null);
            }

            Thread.sleep(3000);
        }
    }

    public static void main(String[] args) throws Exception {
        MainBookInfo bookInfo = new MainBookInfo();
        ExcelProcess excelProcess = new ExcelProcess();
//        bookInfo.nlcBookInfo();
//        bookInfo.dangdangBookInfo();
    }
}
