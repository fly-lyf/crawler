package excel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Number;
import pojo.CnkiResult;
import pojo.GTResult;
import pojo.SearchResult;

import java.io.*;

/**
 * Created by Administrator on 2015/8/29.
 */
public class ExcelReader {

    private int rows;

    public SearchResult[] reader() throws Exception {
        //cnki的读取
        //新建workbook
        InputStream instream = new FileInputStream("resources/source-init.xls");
//        InputStream instream = new FileInputStream("resources/source-init-part.xls");
        Workbook readwb = Workbook.getWorkbook(instream);
        //读表
        Sheet sheet = readwb.getSheet(0);
        SearchResult[] searchResults = new SearchResult[sheet.getRows()];
        for (int i = 0; i < sheet.getRows(); i++) {
            Cell title = sheet.getCell(0, i);
            Cell author = sheet.getCell(1, i);
            Cell publisher = sheet.getCell(2, i);
            Cell add = sheet.getCell(4, i);
            Cell pubYear = sheet.getCell(3, i);

            String pubYearNum = pubYear.getContents();
            //处理xxxx年xx月，41122
            if (pubYearNum.contains("月") || pubYearNum.contains("无") || pubYearNum.contains(".")) {
                pubYearNum = pubYearNum.substring(pubYearNum.indexOf("20"), pubYearNum.indexOf("20") + 4);
            }
            if (pubYearNum.contains("40") || pubYearNum.contains("41") || pubYearNum.contains("42") || pubYearNum.contains("39")) {
                pubYearNum = "" + (Integer.parseInt(pubYearNum) / 365 + 1900);
            }
            if (pubYearNum.contains("-")) {
                pubYearNum = "20" + pubYearNum.substring(0, 2);
            }

//            Cell recYear = sheet.getCell(9, i);
//            String recYearNum = recYear.getContents();
//            if (recYearNum.contains("月") || recYearNum.contains("无") || recYearNum.contains(".")) {
//                recYearNum = recYearNum.substring(recYearNum.indexOf("20"), recYearNum.indexOf("20") + 4);
//            }
//            if (recYearNum.contains("40") || recYearNum.contains("41") || recYearNum.contains("42") || recYearNum.contains("39")) {
//                recYearNum = "" + (Integer.parseInt(recYearNum) / 365 + 1900);
//            }
            searchResults[i] = new SearchResult();
            searchResults[i].setTitle(title.getContents());
            searchResults[i].setAuthor(author.getContents());
            if (pubYearNum != "" && pubYearNum != null) {
                searchResults[i].setPubTime(Integer.parseInt(pubYearNum));
            } else {
                searchResults[i].setPubTime(null);
            }
            searchResults[i].setPublisher(publisher.getContents());
            searchResults[i].setAddress(add.getContents());
        }
        readwb.close();
        return searchResults;
    }

    /**
     * flag 表示是否加入出版年的查询条件
     *
     * @param searchParam
     * @param flag
     * @throws Exception
     */
    public void writer(SearchResult searchParam, int flag) throws Exception {
        // 这个参数决定有没有recTime和pubTime
        int paramCount = 5;
        Workbook rwb = Workbook.getWorkbook(new File("resources/result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
//        System.out.println("行数：" + rows);
        //写参数
        for (int i = 0; i < 3; i++) {
            WritableCell wc = ws.getWritableCell(rows, i);
            String content = "";
            if (i == 0) {
                content = searchParam.getTitle();
            }
            if (i == 1) {
                content = searchParam.getAuthor();
            }
            if (i == 2) {
                content = searchParam.getPublisher();
            }
            Label label = new Label(i, rows, content);
            ws.addCell(label);
        }
        for (int i = 3; i < 4; i++) {
            Number label = null;
            if (searchParam.getPubTime() != null && flag == 1) {
                Integer content = searchParam.getPubTime();
                label = new Number(3, rows, content);
                ws.addCell(label);
            }
        }
        //写结果
        Integer[] years = searchParam.getYearResult();
        Integer[] counts = searchParam.getCountResult();
        int tpc = 4;
        if (years.length > 0) {
            tpc = 4 + (2015 - years[0]);
        }
        for (int i = 0; i < searchParam.getYearResult().length; i++) {
            Number num = new Number(i + tpc, rows, counts[i]);
            ws.addCell(num);
        }
        wwb.write();
        wwb.close();
        rwb.close();
    }

    public void writeLiXiang(Integer[] result) throws Exception {
        Workbook rwb = Workbook.getWorkbook(new File("resources/result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = 2;
        for (int i = 1; i <= result.length; i++) {
            WritableCell wc = ws.getWritableCell(rows, 17);
            Number num = new Number(17, rows, result[i - 1]);
            rows += 2;
            ws.addCell(num);
        }
        wwb.write();
        wwb.close();
        rwb.close();
    }

    public Integer[] readLiXiang() throws Exception {
        InputStream instream = new FileInputStream("F:\\资料\\后期出版书目名单-2015.5.28.xls");
        Workbook readwb = Workbook.getWorkbook(instream);
        Sheet sheet = readwb.getSheet(0);
        System.out.println(sheet.getRows());
        Integer[] results = new Integer[(sheet.getRows() - 2)];
        for (int i = 2; i < sheet.getRows(); i++) {
            Cell liXiang = sheet.getCell(1, i);
            String time = liXiang.getContents();
            if (time.length() >= 3) {
                time = time.substring(0, 2);
                time = "20" + time;
                Integer number = Integer.parseInt(time);
                results[i - 2] = number;
                System.out.println(results[i - 2]);
            }
        }
        return results;
    }

    public SearchResult[] readOthers() throws Exception {

        //cnki的读取
        //新建workbook
        InputStream instream = new FileInputStream("F:\\资料\\source-init-其他网站.xls");
        Workbook readwb = Workbook.getWorkbook(instream);
        //读表
        Sheet sheet = readwb.getSheet(0);
        SearchResult[] searchResults = new SearchResult[sheet.getRows()];
        for (int i = 0; i < sheet.getRows(); i++) {
            Cell title = sheet.getCell(0, i);
            Cell author = sheet.getCell(1, i);
            Cell publisher = sheet.getCell(2, i);
            Cell add = sheet.getCell(4, i);
            Cell pubYear = sheet.getCell(3, i);

            String pubYearNum = pubYear.getContents();
            if (pubYearNum.contains("月") || pubYearNum.contains("无") || pubYearNum.contains(".")) {
                pubYearNum = pubYearNum.substring(pubYearNum.indexOf("20"), pubYearNum.indexOf("20") + 4);
            }
//
            if (pubYearNum.contains("40") || pubYearNum.contains("41") || pubYearNum.contains("42") || pubYearNum.contains("39")) {
                pubYearNum = "" + (Integer.parseInt(pubYearNum) / 365 + 1900);
            }
            if (pubYearNum.contains("-")) {
                pubYearNum = "20" + pubYearNum.substring(0, 2);
            }

//                Cell recYear = sheet.getCell(9, i);
//                String recYearNum = recYear.getContents();
//                if(recYearNum.contains("月") || recYearNum.contains("无") || recYearNum.contains(".")){
//                    recYearNum = recYearNum.substring(recYearNum.indexOf("20"),recYearNum.indexOf("20")+4);
//                }
//                if(recYearNum.contains("40") || recYearNum.contains("41") || recYearNum.contains("42") || recYearNum.contains("39")){
//                    recYearNum = ""+(Integer.parseInt(recYearNum)/365 +1900);
//                }
            searchResults[i] = new SearchResult();
            searchResults[i].setTitle(title.getContents());
            searchResults[i].setAuthor(author.getContents());
            if (pubYearNum != "" && pubYearNum != null) {
                searchResults[i].setPubTime(Integer.parseInt(pubYearNum));
            } else {
                searchResults[i].setPubTime(null);
            }
            searchResults[i].setPublisher(publisher.getContents());
            searchResults[i].setAddress(add.getContents());
        }
        readwb.close();
        return searchResults;
    }

    public void writeDouBan(SearchResult searchResult, Double[] result) throws Exception {
        Workbook rwb = Workbook.getWorkbook(new File("resources/douban-result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/douban-result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
        Label title = new Label(0, rows, searchResult.getTitle());
        if (result[0] != null) {
            Number num = new Number(5, rows, result[0].intValue());
            Number num1 = new Number(6, rows, result[1]);
            ws.addCell(title);
            ws.addCell(num);
            ws.addCell(num1);
        } else {
            Label num = new Label(5, rows, "--");
            Label num1 = new Label(6, rows, "--");
            ws.addCell(title);
            ws.addCell(num);
            ws.addCell(num1);
        }
        wwb.write();
        wwb.close();
        rwb.close();
    }

    public void writeCNKI(CnkiResult cnkiResult, SearchResult searchParam, int flag) throws Exception {

        Workbook rwb = Workbook.getWorkbook(new File("resources/result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
        //写参数
        for (int i = 0; i < 3; i++) {
            String content = "";
            if (i == 0) {
                content = searchParam.getTitle();
            }
            if (i == 1) {
                content = searchParam.getAuthor();
            }
            if (i == 2) {
                content = searchParam.getPublisher();
            }
            Label label = new Label(i, rows, content);
            ws.addCell(label);
        }

        //出版年
        if (flag == 1 && searchParam.getPubTime() != null) {
            Integer content = searchParam.getPubTime();
            Number number = new Number(3, rows, content);
            ws.addCell(number);
        }

        //结果数
        if (cnkiResult.getCount() != null) {
            Integer count = Integer.parseInt(cnkiResult.getCount());
            WritableCell wc = ws.getWritableCell(rows, 4);
            Number num = new Number(15, rows, count);
            ws.addCell(num);
        }

        //类型数
        Integer mst = 0;
        Integer dct = 0;
        Integer mag = 0;
        Integer conf = 0;
        if (cnkiResult.getType().length > 0) {
            for (String a : cnkiResult.getType()) {
                if (a.equals("博士")) {
                    dct++;
                }
                if (a.equals("硕士")) {
                    mst++;
                }
                if (a.equals("中国") || a.equals("国际")) {
                    conf++;
                }
                if (a.contains("期刊")) {
                    mag++;
                }
            }
        }
        Number num1 = new Number(18, rows, dct);
        Number num2 = new Number(17, rows, mst);
        Number num3 = new Number(19, rows, conf);
        Number num4 = new Number(16, rows, mag);
        ws.addCell(num1);
        ws.addCell(num2);
        ws.addCell(num3);
        ws.addCell(num4);
        //自引数
        Integer selfCitation = cnkiResult.getSelfCitation();
        Integer selfAddCitation = cnkiResult.getSelfAddCitation();
        Number citation1 = new Number(20, rows, selfCitation);
        Number citation2 = new Number(21, rows, selfAddCitation);
        ws.addCell(citation1);
        ws.addCell(citation2);

        //按年度引用数
//        Integer[] yearCitation = cnkiResult.getCitation();
//        int length = yearCitation.length;
//        if ((2016 - yearCitation[0]) == 0) {
//            for (int i = 0; i < yearCitation.length - 1; i++) {
//                if (yearCitation[i+1]==null)
//                {break;}
//                Number yearCit = new Number(4 + i, rows, yearCitation[i + 1]);
//                ws.addCell(yearCit);
//            }
//        } else {
//            for (int i = 0; i < (2016 - yearCitation[0]); i++) {
//                if (yearCitation[i]==null)
//                {break;}
//                Number yearCit0 = new Number(4 + i, rows, 0);
//                ws.addCell(yearCit0);
//            }
//            for (int i = 0; i < yearCitation.length - 1; i++) {
//                if (yearCitation[i+1]==null){
//                    break;
//                }
//                Number yearCit = new Number((2016 - yearCitation[0]) + 4 + i, rows, yearCitation[i + 1]);
//                ws.addCell(yearCit);
//            }
//
//        }
        Integer[] yearCitation = cnkiResult.getCitation();
        for(int i=0;i<yearCitation.length;i++)
        {
            if (yearCitation[i]==null){
                    break;
                }
                Number yearCit = new Number(4+i, rows, yearCitation[i]);
                ws.addCell(yearCit);
        }
        int totleYearCit=0;
        for(int i=0;i<yearCitation.length ;i++)
        {
            if (yearCitation[i]==null){
                break;
            }
            totleYearCit+=yearCitation[i];
        }
        Number totleYearCits = new Number(14, rows, totleYearCit);
        ws.addCell(totleYearCits);
        wwb.write();
        wwb.close();
        rwb.close();
    }

    public void writeAmazon(Double[] amazon, SearchResult searchResult) throws Exception {
        Workbook rwb = Workbook.getWorkbook(new File("resources/amazon-result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/amazon-result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
        Label title = new Label(0, rows, searchResult.getTitle());
        ws.addCell(title);
        if (amazon[0] != null) {
            Number num = new Number(5, rows, amazon[0].intValue());
            Number num1 = new Number(6, rows, amazon[1]);
            ws.addCell(num);
            ws.addCell(num1);
        } else {
            Label num = new Label(5, rows, "--");
            Label num1 = new Label(6, rows, "--");
            ws.addCell(num);
            ws.addCell(num1);
        }

        wwb.write();
        wwb.close();
        rwb.close();
    }

    public void writeDangdang(Integer dangdang, SearchResult searchResult) throws Exception {
        Workbook rwb = Workbook.getWorkbook(new File("resources/dangdang-result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/dangdang-result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
        Label lab = new Label(0, rows, searchResult.getTitle());
        Label lab1 = new Label(1, rows, searchResult.getAuthor());
        ws.addCell(lab);
        ws.addCell(lab1);
        if (dangdang != null) {
            Number num = new Number(5, rows, dangdang);
            Number num1 = new Number(6, rows, 5);
            ws.addCell(num);
            ws.addCell(num1);
        } else {
            Label num = new Label(5, rows, "--");
            Label num1 = new Label(6, rows, "--");
            ws.addCell(num);
            ws.addCell(num1);
        }

        wwb.write();
        wwb.close();
        rwb.close();
    }

    public void writeNlc(GTResult gtResult) throws Exception {
        Workbook rwb = Workbook.getWorkbook(new File("resources/result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
        System.out.println("   行数：" + rows);

        if (gtResult == null) {
            for (int i = 0; i < 12; i++) {
                Label lab = new Label(i, rows, "--");
                ws.addCell(lab);
            }
        } else {
            Label lab1 = new Label(0, rows, gtResult.getTitle());
            Label lab2 = new Label(1, rows, gtResult.getBooktype());
            Label lab3 = new Label(2, rows, gtResult.getMainAuthor());
            Label lab4 = new Label(3, rows, gtResult.getPublisher());
            Label lab5 = new Label(4, rows, gtResult.getPubTime());
            Label lab6 = new Label(5, rows, gtResult.getAllAuthor());
            Label lab7 = new Label(6, rows, gtResult.getISBN());
            Label lab8 = new Label(7, rows, gtResult.getPublishPlace());
            Label lab9 = new Label(8, rows, gtResult.getOtherAuthor());
            Label lab10 = new Label(9, rows, gtResult.getLangguage());
            Label lab11 = new Label(10, rows, gtResult.getSort());
            Label lab12 = new Label(11, rows, gtResult.getPage());
            Label lab13 = new Label(12, rows, gtResult.getAllTitle());
            ws.addCell(lab1);
            ws.addCell(lab2);
            ws.addCell(lab3);
            ws.addCell(lab4);
            ws.addCell(lab5);
            ws.addCell(lab6);
            ws.addCell(lab7);
            ws.addCell(lab8);
            ws.addCell(lab9);
            ws.addCell(lab10);
            ws.addCell(lab11);
            ws.addCell(lab12);
            ws.addCell(lab13);
        }
        wwb.write();
        wwb.close();
        rwb.close();
    }

    public void writeDangdangSpider(GTResult gtResult) throws Exception {
        Workbook rwb = Workbook.getWorkbook(new File("resources/result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
        System.out.println("   行数：" + rows);

        if (gtResult == null) {
            for (int i = 0; i < 13; i++) {
                Label lab = new Label(i, rows, "--");
                ws.addCell(lab);
            }
        } else {
            Label lab1 = new Label(0, rows, gtResult.getTitle());
            Label lab4 = new Label(3, rows, gtResult.getPublisher());
            Label lab5 = new Label(4, rows, gtResult.getPubTime());
            Label lab6 = new Label(5, rows, gtResult.getAllAuthor());
            Label lab7 = new Label(6, rows, gtResult.getISBN());
            Label lab11 = new Label(10, rows, gtResult.getSort());
            Label lab12 = new Label(11, rows, gtResult.getPage());
            Label lab13 = new Label(12, rows, gtResult.getPrice());
            ws.addCell(lab1);
            ws.addCell(lab4);
            ws.addCell(lab5);
            ws.addCell(lab6);
            ws.addCell(lab7);
            ws.addCell(lab11);
            ws.addCell(lab12);
            ws.addCell(lab13);
        }
        wwb.write();
        wwb.close();
        rwb.close();
    }

    public void writeCnkiDetail(String result, String title, String totalTitle) throws Exception {
        Workbook rwb = Workbook.getWorkbook(new File("resources/result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
        if (result.contains(",") || result.contains("，")) {
            result = result.replaceAll(",", "");
            result = result.replaceAll("，", "");
            result = result.replaceAll(" ", "");
        }
        Integer count = Integer.parseInt(result);
        System.out.println("   行数：" + rows);
        Number num = new Number(1, rows, count);
        Label lab = new Label(0, rows, title);
        if (totalTitle != null) {
            Label lab1 = new Label(3, rows, totalTitle);
            ws.addCell(lab1);
        }
        ws.addCell(lab);
        ws.addCell(num);
        wwb.write();
        wwb.close();
        rwb.close();
    }

    public void convert(String result[]) throws Exception {
        Workbook rwb1 = Workbook.getWorkbook(new File("resources/result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb1);//copy
        WritableSheet sheet = wwb.getSheet(0);
        int rows = sheet.getRows();
        String info = "";
        Number num = new Number(0, rows, Integer.parseInt(result[0]));
        sheet.addCell(num);
        Label lab = new Label(1, rows, result[1]);
        sheet.addCell(lab);
        Number lixiangW = new Number(2, rows, Integer.parseInt(result[2]));
        sheet.addCell(lixiangW);
        if (result[3].contains("--")) {
            Label lab4 = new Label(3, rows, result[3]);
            sheet.addCell(lab4);
        } else {
            Number lab4 = new Number(3, rows, Integer.parseInt(result[3]));
            sheet.addCell(lab4);
        }
        wwb.write();
        wwb.close();
        rwb1.close();
    }

    public String[][] readConvert() throws IOException, BiffException {

        Workbook rwb = Workbook.getWorkbook(new File("F:\\资料\\图书信息.xls"));
        Sheet sheet = rwb.getSheet(0);
        int rows = sheet.getRows();
        String[][] result = new String[rows][4];
        String info = "";
        for (int i = 1; i < sheet.getRows(); i++) {
            Cell author = sheet.getCell(7, i);
            Cell title = sheet.getCell(1, i);
            Cell place = sheet.getCell(9, i);
            Cell publisher = sheet.getCell(5, i);
            Cell time = sheet.getCell(6, i);
            String timeFormat = time.getContents();
            String authorFormat = author.getContents();
            Cell otherAuthor = sheet.getCell(10, i);
            Cell lixiang = sheet.getCell(15, i);
            String otherAuthorFormat = otherAuthor.getContents();
            result[i][0] = "" + i;

            if (timeFormat.contains("-") || timeFormat.contains("/")) {
                if (timeFormat.length() > 4) {
                    timeFormat = timeFormat.substring(0, 2);
                    timeFormat = "20" + timeFormat;
                }
            }
            if (authorFormat.contains("等")) {
                authorFormat = authorFormat.replaceAll("等", "");
                otherAuthorFormat = otherAuthorFormat.replaceAll("：", "");
                System.out.println(i + authorFormat + "," + otherAuthorFormat + "," + title.getContents() + "," + place.getContents() + ":" + publisher.getContents() + "," + timeFormat);
                info = authorFormat + "," + otherAuthorFormat + "," + title.getContents() + "," + place.getContents() + ":" + publisher.getContents() + "," + timeFormat;
                result[i][1] = info;

            } else {
                System.out.println(i + authorFormat + "," + title.getContents() + "," + place.getContents() + ":" + publisher.getContents() + "," + timeFormat);
                info = authorFormat + "," + title.getContents() + "," + place.getContents() + ":" + publisher.getContents() + "," + timeFormat;
                result[i][1] = info;
            }
            System.out.println(lixiang.getContents());
            result[i][2] = lixiang.getContents();
            if (timeFormat.contains("--")) {
                System.out.println("--");
                result[i][3] = "--";
            } else {
                System.out.println(Integer.parseInt(timeFormat) - Integer.parseInt(lixiang.getContents()));
                Number zhouqi = new Number(3, rows, Integer.parseInt(timeFormat) - Integer.parseInt(lixiang.getContents()));
                result[i][3] = "" + (Integer.parseInt(timeFormat) - Integer.parseInt(lixiang.getContents()));
            }

        }
        return result;
    }


    public void writeFirCiat(Integer s) {

    }

    public Integer[] readFinal() throws IOException, BiffException {
        Workbook rwb = Workbook.getWorkbook(new File("F:\\资料\\最终结果.xls"));
        Sheet sheet = rwb.getSheet(0);
        int rows = sheet.getRows();
        Integer[] result = new Integer[rows - 1];
        for (int i = 1; i < rows; i++) {
            for (int j = 12; j >= 4; j--) {
                Cell cell = sheet.getCell(j, i);
                if (cell.getContents() != "" && cell.getContents() != null) {
                    Cell year = sheet.getCell(j, 0);
                    Cell pub = sheet.getCell(3, i);
                    result[i - 1] = Integer.parseInt(year.getContents().substring(0, 4)) - Integer.parseInt(pub.getContents());
                    System.out.println(result[i - 1]);
                    break;
                }
                if (j == 4) {
                    result[i - 1] = 99999;
                    System.out.println(result[i - 1]);
                }
            }

        }
        return result;
    }
}
