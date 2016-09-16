/**
 * Created by Administrator on 2015/8/18.
 */
public class MainTask {

    public static void main(String[] args) throws Exception {

        //cnki数据去重
//        DuplicateCNKI duplicateCNKI = new DuplicateCNKI();
//        duplicateCNKI.duplicate();

        //国图的图书信息
//        ExcelProcess reader = new ExcelProcess();
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
//        ExcelProcess reader = new ExcelProcess();
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

        //表一
//        ExcelProcess excelReader = new ExcelProcess();
//        String result[][] = excelReader.readConvert();
//        for(int i = 1; i<result.length;i++){
//            excelReader.convert(result[i]);
//        }

        //首次被引间隔
//        ExcelProcess excelReader = new ExcelProcess();
//        Integer result[] = excelReader.readFinal();
    }
}
