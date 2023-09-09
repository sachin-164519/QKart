package QKART_TESTNG;

import org.openqa.selenium.chrome.ChromeDriver;

import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import io.github.bonigarcia.wdm.WebDriverManager;

public class QKART_Tests {
    static ChromeDriver driver;
    public static String lastGeneratedusername;
    public static void createDriver(){
        WebDriverManager.chromedriver().timeout(30).setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        System.out.println("createDriver()");
    }

    public void TestCase01(String username, String password) throws InterruptedException{
        Boolean status;
        Register registration =  new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(username, password, true);
        // assertTrue(status,"Failed to register new user");
        lastGeneratedusername = registration.lastGeneratedUsername;
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedusername, password);
        Home home = new Home(driver);
        status = home.PerformLogout();
    }


}
