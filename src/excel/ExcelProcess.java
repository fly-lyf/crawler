package excel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import pojo.CnkiResult;
import pojo.GTResult;
import pojo.SearchResult;
import excel.CellName;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/29.
 */
public class ExcelProcess {


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
            if (i == CellName.title.getValue()) {
                content = searchParam.getTitle();
            }
            if (i == CellName.author.getValue()) {
                content = searchParam.getAuthor();
            }
            if (i == CellName.publisher.getValue()) {
                content = searchParam.getPublisher();
            }
            Label label = new Label(i, rows, content);
            ws.addCell(label);
        }

        //出版年
        if (searchParam.getPubTime() != null) {
            Integer content = searchParam.getPubTime();
            Number number = new Number(CellName.pubTime.getValue(), rows, content);
            ws.addCell(number);
        }

        //结果数
        if (cnkiResult.getCount() != null) {
            Integer count = cnkiResult.getCount();
            Number num = new Number(CellName.crawledTotal.getValue(), rows, count);
            ws.addCell(num);
        }

        //类型数
        Integer mst = cnkiResult.getType().get("master");
        Integer dct = cnkiResult.getType().get("doctor");
        Integer mag = cnkiResult.getType().get("magazine");
        Integer conf = cnkiResult.getType().get("conference");
        Number num4 = new Number(CellName.magazine.getValue(), rows, mag);
        Number num2 = new Number(CellName.master.getValue(), rows, mst);
        Number num1 = new Number(CellName.doctor.getValue(), rows, dct);
        Number num3 = new Number(CellName.conference.getValue(), rows, conf);
        ws.addCell(num1);
        ws.addCell(num2);
        ws.addCell(num3);
        ws.addCell(num4);
        //右被引总数
        Number rightTotal = new Number(CellName.rightTotal.getValue(), rows, mag + dct + mst + conf);
        ws.addCell(rightTotal);
        //自引数
        Integer selfCitation = cnkiResult.getSelfCitation();
        Integer selfInstituteCitation = cnkiResult.getSelfInstituteCitation();
        Number citation1 = new Number(CellName.authorSelf.getValue(), rows, selfCitation);
        Number citation2 = new Number(CellName.instituteSelf.getValue(), rows, selfInstituteCitation);
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
            if (CellName.cit2016.getValue() + 2016 - year >= CellName.cit2007.getValue()) {
                continue;
            }
            Number yearCit = new Number(CellName.cit2016.getValue() + 2016 - year, rows, count);
            ws.addCell(yearCit);
        }
        //左被引总数
        Number totleYearCits = new Number(CellName.leftTotal.getValue(), rows, total);
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

    /**
     * cnki学术评论、报纸评论写入
     *
     * @param result
     * @throws Exception
     */
    public void writeCnkiComment(String result, Integer col, int row) throws Exception {
        InputStream instream = new FileInputStream("resources/result.xls");
        Workbook rwb = Workbook.getWorkbook(instream);
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        if (result.contains(",") || result.contains("，")) {
            result = result.replaceAll(",", "");
            result = result.replaceAll("，", "");
            result = result.replaceAll(" ", "");
        }
        Integer count = Integer.parseInt(result);
        Number num = new Number(col, row, count);
        ws.addCell(num);
        wwb.write();
        wwb.close();
        rwb.close();
    }

    /**
     * 国图图书信息写入
     *
     * @param gtResult
     * @throws Exception
     */
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

    /**
     * 当当图书信息写入
     *
     * @param gtResult
     * @throws Exception
     */
    public void writeDangdangBookInfo(GTResult gtResult) throws Exception {
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


}
