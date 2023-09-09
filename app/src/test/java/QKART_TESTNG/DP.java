package QKART_TESTNG;

import java.lang.reflect.Method;

import org.testng.annotations.DataProvider;

import QKART_TESTNG.utils.ExcelUtils;

public class DP {
    @DataProvider (name = "data-provider")
    public Object[][] dpMethod (Method m) throws Exception{

        Object data[][] = ExcelUtils.getTableArray("C:\\Users\\91798\\Git workspace\\Qkart_QA\\app\\src\\test\\resources\\Dataset.xlsx", m.getName());

        return data;
    }
}
