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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/29.
 */
public class ExcelProcess {

    public enum CellName{
        title(0),
        author(1),
        publisher(2),
        pubTime(3),
        cit2016(4),
        cit2015(5),
        cit2014(6),
        cit2013(7),
        cit2012(8),
        cit2011(9),
        cit2010(10),
        cit2009(11),
        cit2008(12),
        cit2007(13),
        leftTotal(14),
        crawledTotal(15),
        rightTotal(16),
        magazine(17),
        master(18),
        doctor(19),
        conference(20),
        authorSelf(21),
        instituteSelf(22);

        private int code;

        CellName(int i) {
            this.code = i;
        }
    }


    /**
     * 通用读取方法
     *
     * @param path
     * @return
     * @throws Exception
     */
    public SearchResult[] reader(String path) throws Exception {
        //新建workbook
        InputStream instream = new FileInputStream(path);
        Workbook readwb = Workbook.getWorkbook(instream);
        //读表
        Sheet sheet = readwb.getSheet(0);
        SearchResult[] searchResults = new SearchResult[sheet.getRows()];
        for (int i = 0; i < sheet.getRows(); i++) {
            Cell title = sheet.getCell(0, i);
            Cell author = sheet.getCell(1, i);
            Cell publisher = sheet.getCell(2, i);
            Cell pubYear = sheet.getCell(3, i);
            Cell addr = sheet.getCell(4, i);
            Cell spareTitle = sheet.getCell(6, i);
            Cell spareAuthor = sheet.getCell(7, i);

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
            if (pubYearNum.contains("/")) {
                pubYearNum = pubYearNum.substring(0, 4);
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

            if (spareTitle != null && spareTitle.getContents() != "") {
                searchResults[i].setSpareTitle(spareTitle.getContents());
            } else {
                searchResults[i].setSpareTitle(null);
            }

            if (spareAuthor != null && spareAuthor.getContents() != "") {
                searchResults[i].setSpareAuthor(spareAuthor.getContents());
            } else {
                searchResults[i].setSpareAuthor(null);
            }
            searchResults[i].setPublisher(publisher.getContents());
            searchResults[i].setAddress(addr.getContents());
        }
        readwb.close();
        return searchResults;
    }

    /**
     * cnki写入
     *
     * @param cnkiResult
     * @param searchParam
     * @throws Exception
     */
    public void writeCNKI(CnkiResult cnkiResult, SearchResult searchParam) throws Exception {

        Workbook rwb = Workbook.getWorkbook(new File("resources/cnki-result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/cnki-result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
        //写参数
        for (int i = 0; i < 3; i++) {
            String content = "";
            if (i == CellName.title.ordinal()) {
                content = searchParam.getTitle();
            }
            if (i == CellName.author.ordinal()) {
                content = searchParam.getAuthor();
            }
            if (i == CellName.publisher.ordinal()) {
                content = searchParam.getPublisher();
            }
            Label label = new Label(i, rows, content);
            ws.addCell(label);
        }

        //出版年
        if (searchParam.getPubTime() != null) {
            Integer content = searchParam.getPubTime();
            Number number = new Number(CellName.pubTime.ordinal(), rows, content);
            ws.addCell(number);
        }

        //结果数
        if (cnkiResult.getCount() != null) {
            Integer count = cnkiResult.getCount();
            Number num = new Number(CellName.crawledTotal.ordinal(), rows, count);
            ws.addCell(num);
        }

        //类型数
        Integer mst = cnkiResult.getType().get("master");
        Integer dct = cnkiResult.getType().get("doctor");
        Integer mag = cnkiResult.getType().get("magazine");
        Integer conf = cnkiResult.getType().get("conference");
        Number num4 = new Number(CellName.magazine.ordinal(), rows, mag);
        Number num2 = new Number(CellName.master.ordinal(), rows, mst);
        Number num1 = new Number(CellName.doctor.ordinal(), rows, dct);
        Number num3 = new Number(CellName.conference.ordinal(), rows, conf);
        ws.addCell(num1);
        ws.addCell(num2);
        ws.addCell(num3);
        ws.addCell(num4);
        //右被引总数
        Number rightTotal = new Number(CellName.rightTotal.ordinal(), rows, mag+dct+mst+conf);
        ws.addCell(rightTotal);
        //自引数
        Integer selfCitation = cnkiResult.getSelfCitation();
        Integer selfInstituteCitation = cnkiResult.getSelfInstituteCitation();
        Number citation1 = new Number(CellName.authorSelf.ordinal(), rows, selfCitation);
        Number citation2 = new Number(CellName.instituteSelf.ordinal(), rows, selfInstituteCitation);
        ws.addCell(citation1);
        ws.addCell(citation2);

        //按年度引用数
        HashMap<String, Integer> yearCitation = cnkiResult.getCitation();
        int total = 0;
        for (Iterator iterator = yearCitation.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Integer> it = (Map.Entry<String, Integer>) iterator.next();
            Integer year = Integer.parseInt(it.getKey());
            Integer count = it.getValue();
            total += count;
            if(CellName.cit2016.ordinal() + 2016 - year >= CellName.cit2007.ordinal()){
                continue;
            }
            Number yearCit = new Number(CellName.cit2016.ordinal() + 2016 - year, rows, count);
            ws.addCell(yearCit);
        }
        //左被引总数
        Number totleYearCits = new Number(CellName.leftTotal.ordinal(), rows, total);
        ws.addCell(totleYearCits);
        wwb.write();
        wwb.close();
        rwb.close();
    }

    /**
     * amazon写入
     *
     * @param amazon
     * @param searchResult
     * @throws Exception
     */
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

    /**
     * 当当写入
     *
     * @param dangdang
     * @param searchResult
     * @throws Exception
     */
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

    /**
     * 豆瓣写入
     *
     * @param searchResult
     * @param result
     * @throws Exception
     */
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

    public void writeNlc(GTResult gtResult) throws Exception {
        Workbook rwb = Workbook.getWorkbook(new File("F:\\资料\\result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("F:\\资料\\result.xls"), rwb);//copy
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
        Workbook rwb = Workbook.getWorkbook(new File("F:\\资料\\result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("F:\\资料\\result.xls"), rwb);//copy
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

    public void convert(String result[]) throws Exception {
        Workbook rwb1 = Workbook.getWorkbook(new File("F:\\资料\\result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("F:\\资料\\result.xls"), rwb1);//copy
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
