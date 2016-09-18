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
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/29.
 */
public class ExcelProcess {

    public enum CellName {
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
        instituteSelf(22),
        年均引用(37),
        首次被引时间(39),
        峰值间隔(41);

        private int value;

        CellName(int value) {
            this.value = value;
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
            if (i == CellName.title.value) {
                content = searchParam.getTitle();
            }
            if (i == CellName.author.value) {
                content = searchParam.getAuthor();
            }
            if (i == CellName.publisher.value) {
                content = searchParam.getPublisher();
            }
            Label label = new Label(i, rows, content);
            ws.addCell(label);
        }

        //出版年
        if (searchParam.getPubTime() != null) {
            Integer content = searchParam.getPubTime();
            Number number = new Number(CellName.pubTime.value, rows, content);
            ws.addCell(number);
        }

        //结果数
        if (cnkiResult.getCount() != null) {
            Integer count = cnkiResult.getCount();
            Number num = new Number(CellName.crawledTotal.value, rows, count);
            ws.addCell(num);
        }

        //类型数
        Integer mst = cnkiResult.getType().get("master");
        Integer dct = cnkiResult.getType().get("doctor");
        Integer mag = cnkiResult.getType().get("magazine");
        Integer conf = cnkiResult.getType().get("conference");
        Number num4 = new Number(CellName.magazine.value, rows, mag);
        Number num2 = new Number(CellName.master.value, rows, mst);
        Number num1 = new Number(CellName.doctor.value, rows, dct);
        Number num3 = new Number(CellName.conference.value, rows, conf);
        ws.addCell(num1);
        ws.addCell(num2);
        ws.addCell(num3);
        ws.addCell(num4);
        //右被引总数
        Number rightTotal = new Number(CellName.rightTotal.value, rows, mag + dct + mst + conf);
        ws.addCell(rightTotal);
        //自引数
        Integer selfCitation = cnkiResult.getSelfCitation();
        Integer selfInstituteCitation = cnkiResult.getSelfInstituteCitation();
        Number citation1 = new Number(CellName.authorSelf.value, rows, selfCitation);
        Number citation2 = new Number(CellName.instituteSelf.value, rows, selfInstituteCitation);
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
            if (CellName.cit2016.value + 2016 - year >= CellName.cit2007.value) {
                continue;
            }
            Number yearCit = new Number(CellName.cit2016.value + 2016 - year, rows, count);
            ws.addCell(yearCit);
        }
        //左被引总数
        Number totleYearCits = new Number(CellName.leftTotal.value, rows, total);
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
        Workbook rwb = Workbook.getWorkbook(new File("resources/result.xls"));
        WritableWorkbook wwb = this.writeSheet("resources/result.xls");
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

    /**
     * 写入立项时间数据，这个代码基本是不能用的
     *
     * @throws Exception
     */
    public void addLiXiang() throws Exception {
        //读
        InputStream instream = new FileInputStream("resources/2015年书目.xls");
        Workbook readwb = Workbook.getWorkbook(instream);
        Sheet sheet = readwb.getSheet(1);
        Integer[] results = new Integer[(sheet.getRows() - 2)];
        String[] title = new String[(sheet.getRows() - 2)];
        String[] author = new String[(sheet.getRows() - 2)];
        for (int i = 2; i < sheet.getRows(); i++) {
            Cell liXiang = sheet.getCell(1, i);
            author[i - 2] = sheet.getCell(5, i).getContents();
            title[i - 2] = sheet.getCell(3, i).getContents();
            String time = liXiang.getContents();
            if (time.length() >= 3) {
                time = time.substring(0, 2);
                time = "20" + time;
                Integer number = Integer.parseInt(time);
                results[i - 2] = number;
                System.out.println(results[i - 2]);
            }
        }

        //写
        Workbook rwb = Workbook.getWorkbook(new File("resources/result.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb);//copy
        WritableSheet ws = wwb.getSheet(0);
        Sheet resultSheet = rwb.getSheet(0);
        HashMap<String, String> resultAuthor = new HashMap<>();
        System.out.println(resultSheet.getRows());
        for (int i = 1; i < resultSheet.getRows(); i++) {
            String result16 = resultSheet.getCell(0, i).getContents();
            if (resultAuthor.get(result16) != null) {
                System.out.println(result16);
                System.out.println(resultAuthor.get(result16));
                System.out.println(i);
            }
            resultAuthor.put(result16, resultSheet.getCell(1, i).getContents());
        }
        int rows = 2;
        for (int i = 1; i <= results.length; i++) {
            for (Iterator<Map.Entry<String, String>> iterator = resultAuthor.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> it = iterator.next();
                if (author[i - 1].contains(it.getValue()) && title[i - 1].contains(it.getKey())) {
                    Number num = new Number(23, rows, results[i - 1]);
                    ws.addCell(num);
                    break;
                } else if (author[i - 1].contains(it.getValue())) {
                    System.out.println(title[i - 1] + " " + author[i - 1] + " " + results[i - 1]);
                }
            }
            rows++;
        }
        wwb.write();
        wwb.close();
        rwb.close();
    }


    public void 计算年均引用(String path) throws IOException, BiffException, WriteException {
        Sheet sheet = this.readSheet(path, 0);
        Integer rows = sheet.getRows();
        Double[] results = new Double[rows - 1];
        for (int i = 1; i < rows; i++) {
            Integer duration = null;
            Integer year = 2007;
            Double total = 0.0;
            Cell[] cells = new Cell[CellName.cit2007.value - CellName.cit2015.value + 1];
            for (int j = CellName.cit2007.value; j > CellName.cit2015.value; j--) {
                cells[j - CellName.cit2015.value] = sheet.getCell(j, i);
                String cellStr = cells[j - CellName.cit2015.value].getContents();
                if (!cellStr.equals("") && duration == null) {
                    System.out.println(year);
                    duration = 2015 - year;

                } else if (!cellStr.equals("")) {
                    total += Integer.parseInt(cellStr);
                }
                year++;
            }
            if (duration == null) {
                results[i - 1] = null;
            } else {
                results[i - 1] = total / duration;
            }
            System.out.println(results[i - 1]);
        }

        WritableWorkbook wwb = this.writeSheet(path);
        WritableSheet ws = wwb.getSheet(0);
        for (int i = 1; i < ws.getRows(); i++) {
            if (results[i - 1] != null) {
                Number cell = new Number(CellName.年均引用.value, i, results[i - 1]);
                ws.addCell(cell);
            } else {
                Label cell = new Label(CellName.年均引用.value, i, "null");
                ws.addCell(cell);
            }
        }
        this.closeSheet(wwb);
    }

    public void 计算首次被引时间(String path) throws IOException, WriteException, BiffException {
        Sheet sheet = this.readSheet(path, 0);
        Integer rows = sheet.getRows();
        Integer[] results = new Integer[rows - 1];
        for (int i = 1; i < rows; i++) {
            Integer firstCitYear = null;
            Integer year = 2007;
            Cell[] cells = new Cell[CellName.cit2007.value - CellName.cit2015.value + 1];
            for (int j = CellName.cit2007.value; j >= CellName.cit2015.value; j--) {
                cells[j - CellName.cit2015.value] = sheet.getCell(j, i);
                String cellStr = cells[j - CellName.cit2015.value].getContents();
                if (!cellStr.equals("")) {
                    firstCitYear = year;
                    break;
                }
                year++;
            }

            if (firstCitYear == null || firstCitYear == 2016) {
                results[i - 1] = null;
            } else {
                results[i - 1] = year;
            }
            System.out.println(results[i - 1]);
        }

        WritableWorkbook wwb = this.writeSheet(path);
        WritableSheet ws = wwb.getSheet(0);
        for (int i = 1; i < ws.getRows(); i++) {
            if (results[i - 1] != null) {
                Number cell = new Number(CellName.首次被引时间.value, i, results[i - 1]);
                ws.addCell(cell);
            } else {
                Label cell = new Label(CellName.首次被引时间.value, i, "null");
                ws.addCell(cell);
            }
        }
        this.closeSheet(wwb);
    }

    public void 计算峰值间隔(String path) throws IOException, BiffException, WriteException {
        Sheet sheet = this.readSheet(path, 0);
        Integer rows = sheet.getRows();
        Integer[] results = new Integer[rows - 1];
        for (int i = 1; i < rows; i++) {
            Integer max = 0;
            Integer maxYear = null;
            Integer year = 2015;
            Cell[] cells = new Cell[CellName.cit2007.value - CellName.cit2015.value + 1];
            for (int j = CellName.cit2015.value; j <= CellName.cit2007.value; j++) {
                cells[j - CellName.cit2015.value] = sheet.getCell(j, i);
                String cellStr = cells[j - CellName.cit2015.value].getContents();
                Integer cellVal;
                if (cellStr.equals("")) {
                    cellVal = 0;
                } else {
                    cellVal = Integer.parseInt(cellStr);
                }
                if (cellVal >= max) {
                    max = cellVal;
                    maxYear = year;
                }
                year--;
            }
            if (max == 0) {
                maxYear = null;
            }

            if (maxYear == null) {
                results[i - 1] = null;
            } else {
                results[i - 1] = 2015 - maxYear;
            }
            System.out.println(results[i - 1]);
        }

        WritableWorkbook wwb = this.writeSheet(path);
        WritableSheet ws = wwb.getSheet(0);
        for (int i = 1; i < ws.getRows(); i++) {
            if (results[i - 1] != null) {
                Number cell = new Number(CellName.峰值间隔.value, i, results[i - 1]);
                ws.addCell(cell);
            } else {
                Label cell = new Label(CellName.峰值间隔.value, i, "null");
                ws.addCell(cell);
            }
        }
        this.closeSheet(wwb);
    }

    /**
     * 通用读表
     *
     * @param path
     * @return
     */
    public Sheet readSheet(String path, int sheetNum) throws IOException, BiffException {
        InputStream instream = new FileInputStream(path);
        Workbook readwb = Workbook.getWorkbook(instream);
        Sheet sheet = readwb.getSheet(sheetNum);
        return sheet;
    }

    /**
     * 通用写表
     *
     * @param path
     * @return
     */
    public WritableWorkbook writeSheet(String path) throws IOException, BiffException, WriteException {
        InputStream instream = new FileInputStream(path);
        Workbook rwb = Workbook.getWorkbook(instream);
        WritableWorkbook wwb = Workbook.createWorkbook(new File("resources/result.xls"), rwb);//copy
        return wwb;

    }

    /**
     * 通用关闭表格
     *
     * @param wwb
     * @throws IOException
     * @throws WriteException
     */
    public void closeSheet(WritableWorkbook wwb) throws IOException, WriteException {
        wwb.write();
        wwb.close();
    }

    public static void main(String[] args) throws WriteException, IOException, BiffException {
        ExcelProcess process = new ExcelProcess();
//        process.计算峰值间隔("reso/urces/result.xls");
        process.计算首次被引时间("resources/result.xls");
//        process.计算年均引用("resources/result.xls");
    }
}
