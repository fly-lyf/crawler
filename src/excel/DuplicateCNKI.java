package excel;

import jxl.Cell;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;

/**
 * Created by Administrator on 2015/9/8.
 */
public class DuplicateCNKI {
    public void duplicate() throws Exception {
        Workbook rwb = Workbook.getWorkbook(new File("F:\\资料\\cnki数据.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("F:\\资料\\cnki数据.xls"), rwb);
        WritableSheet ws = wwb.getSheet(0);
        int rows = ws.getRows();
        for(int i=9;i<rows;i+=2){
            Cell[] cells1 = ws.getRow(i);
            String txt1 = cells1[14].getContents();
            String txt2 = ws.getCell(14,i+1).getContents();
            System.out.println(txt1+" "+txt2);
            if(txt1.equals(txt2)){
                for(int j=0;j<cells1.length;j++){
                    ws.addCell(new Label(j,i,""));
                }
            }
        }
        wwb.write();
        wwb.close();
        rwb.close();
    }
}
