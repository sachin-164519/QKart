package QKART_TESTNG.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import QKART_TESTNG.TestResult;

public class ExcelUtils {
    public static String fileName = null;
    private static XSSFSheet Sheet;
    private static XSSFWorkbook WBook;
    private static XSSFCell Cell;
    private static XSSFRow Row;

    public static Object[][] getTableArray(String FilePath, String SheetName) throws IOException {
        Object[][] data = null;
        try {
            // fileName = currentDir + FilePath;
            FileInputStream ExcelFile = new FileInputStream(FilePath);
            WBook = new XSSFWorkbook(ExcelFile);
            Sheet = WBook.getSheet(SheetName);
            Row = Sheet.getRow(0);
            int totalRow = Sheet.getPhysicalNumberOfRows();
            int totalCol = Row.getLastCellNum();
            data = new Object[totalRow - 1][totalCol - 1];
            for (int i = 1; i < totalRow; i++) {
                for (int j = 1; j < totalCol; j++) {
                    Row = Sheet.getRow(i);
                    Cell = Row.getCell(j);
                    data[i - 1][j - 1] = getCellData(i, j);
                }
            }

        } catch (Exception e) {
            System.out.println("The exception is: " + e.getMessage());
        }
        return data;
    }

    public static Object getCellData(int RowNum, int ColNum) throws Exception {

        Object CellData = null;
        try {
            Cell = Sheet.getRow(RowNum).getCell(ColNum);
            switch (Cell.getCellType()) {
                case STRING:
                    CellData = (String) Cell.getStringCellValue();
                    break;
                case NUMERIC:
                    CellData = (Double) Cell.getNumericCellValue();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println("The exception is:" + e.getMessage());
        }
        return CellData;
    }

    public static void setData(List<TestResult> testResults) throws IOException{
        WBook = new XSSFWorkbook();
        Sheet = WBook.createSheet("Results");
        try{
        Row headerRow = Sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Test Case");
        headerRow.createCell(1).setCellValue("Parameters");
        headerRow.createCell(2).setCellValue("Status");

        int rowNum=1;
        for(TestResult testResult: testResults){
            Row row = Sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(testResult.getTestCaseName());
            row.createCell(1).setCellValue(Arrays.toString(testResult.getParameters()));
            row.createCell(2).setCellValue(testResult.getStatus());
        }
        String filePath ="/home/crio-user/workspace/sachin-164519-ME_QKART_QA/app/RunResults.xlsx";
        FileOutputStream fos = new FileOutputStream(filePath);
        WBook.write(fos);
        fos.close();
    }catch(Exception e){
        System.out.println("The exception is:" + e.getMessage());
    }
}
}
