package QKART_TESTNG;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import io.github.bonigarcia.wdm.WebDriverManager;

public class QKART_Tests {
    static ChromeDriver driver;
    public static String lastGeneratedUserName;
    @BeforeSuite(alwaysRun = true)
    public static void createDriver(){
        WebDriverManager.chromedriver().timeout(30).setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        System.out.println("createDriver()");
    }

    @Test(description = "Verify registration happens correctly",dataProviderClass = DP.class, priority = 1, groups = { "Sanity_test" },
     dataProvider ="data-provider")
    // @Parameters({ "TC1_Username", "TC1_Password" })

    public void TestCase01(String TC1_Username, String TC1_Password) throws InterruptedException {
        Boolean status;

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(TC1_Username, TC1_Password, true);
        Assert.assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();

        status = login.PerformLogin(lastGeneratedUserName, TC1_Password);

        Assert.assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();
    }

    /*
     * Verify that an existing user is not allowed to re-register on QKart
     */
    @Test(description = "Verify re-registering an already registered user fails", dataProviderClass = DP.class, priority = 2, groups = {
            "Sanity_test"},  dataProvider ="data-provider")
    // @Parameters({ "TC2_Username", "TC2_Password" })
    public void TestCase02(String TC2_Username, String TC2_Password)
            throws InterruptedException {
        Boolean status;

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(TC2_Username, TC2_Password, true);
        Assert.assertTrue(status, "User registration failed");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, TC2_Password, false);

        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
        Assert.assertFalse(status, "Re-registration succeeded");
    }

    // /*
    //  * Verify the functinality of the search text box
    //  */
    @Test(description = "Verify the functionality of search text box", priority = 3, groups = { "Sanity_test" },
    dataProviderClass = DP.class, dataProvider ="data-provider")
    // @Parameters("TC3_ProductNameToSearchFor")
    public void TestCase03(String TC3_ProductNameToSearchFor) throws InterruptedException {
        boolean status = false;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct(TC3_ProductNameToSearchFor);
        Assert.assertTrue(status, "Unable to search for given product - " + TC3_ProductNameToSearchFor);

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        Assert.assertFalse(searchResults.size() == 0,"There were no results for the given search string - " + TC3_ProductNameToSearchFor);

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            Assert.assertTrue(elementText.contains(TC3_ProductNameToSearchFor),
                    "Test Results contains un-expected values: " + elementText);
        }

        // Search for product
        status = homePage.searchForProduct("Gesundheit");

        // Verify no search results are found
        searchResults = homePage.getSearchResults();

        Boolean isResultsEmpty = searchResults.size() == 0 && homePage.isNoResultFound();

        Assert.assertTrue(isResultsEmpty, "Expected: no results, Actual: Results were available");
    }

    /*
     * Verify the presence of size chart and check if the size chart content is as
     * expected
     */
    @Test(description = "Verify the existence of size chart for certain items and validate contents of size chart", priority = 4, groups = {
            "Regression_Test" }, dataProviderClass = DP.class, dataProvider ="data-provider"
    )
    // @Parameters("TC4_ProductNameToSearchFor")
    public void TestCase04(String TC4_ProductNameToSearchFor) throws InterruptedException {
        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        homePage.searchForProduct(TC4_ProductNameToSearchFor);
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            Boolean isSizeChartExists = result.verifySizeChartExists();

            Assert.assertTrue(isSizeChartExists, "Size Chart Link does not exist");

            if (isSizeChartExists) {
                // Verify if size dropdown exists
                Boolean isSizeDropdownExist = result.verifyExistenceofSizeDropdown(driver);
                Assert.assertTrue(isSizeDropdownExist, "Size dropdown doesn't exist");

                // Open the size chart
                Boolean isSizeChartOpenSuccess = result.openSizechart();
                Assert.assertTrue(isSizeChartOpenSuccess, "Failed to open Size Chart");

                if (isSizeChartOpenSuccess) {
                    // Verify if the size chart contents matches the expected values
                    Boolean isChartContentMatching = result.validateSizeChartContents(expectedTableHeaders,
                            expectedTableBody, driver);
                    Assert.assertTrue(isChartContentMatching, "Failure while validating contents of Size Chart Link");

                    // Close the size chart modal
                    Boolean isSizeChartClosed = result.closeSizeChart(driver);
                    Assert.assertTrue(isSizeChartClosed, "Closing size chart failed");
                }

            }
        }
    }

    /*
     * Verify the complete flow of checking out and placing order for products is
     * working correctly
     */
    @Test(description = "Verify that a new user can add multiple products in to the cart and Checkout", priority = 5, groups = {
            "Sanity_test" }, dataProviderClass = DP.class, dataProvider ="data-provider"
    )
    // @Parameters({ "TC5_ProductNameToSearchFor", "TC5_ProductNameToSearchFor2", "TC5_AddressDetails" })
    public void TestCase05(String TC5_ProductNameToSearchFor, String TC5_ProductNameToSearchFor2,
            String TC5_AddressDetails) throws InterruptedException {
        Boolean status;

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Registration failed");

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "Login failed");

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct(TC5_ProductNameToSearchFor);
        homePage.addProductToCart(TC5_ProductNameToSearchFor);
        status = homePage.searchForProduct(TC5_ProductNameToSearchFor2);
        homePage.addProductToCart(TC5_ProductNameToSearchFor2);

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(TC5_AddressDetails);
        checkoutPage.selectAddress(TC5_AddressDetails);

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thanks page
        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue(status, "Placing order didn't redirect to Thanks page");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();
    }

    /*
     * Verify the quantity of items in cart can be updated
     */
    @Test(description = "Verify that the contents of the cart can be edited", priority = 6, groups = {
            "Regression_Test" }, dataProviderClass = DP.class, dataProvider ="data-provider"
    )
    // @Parameters({ "TC6_ProductNameToSearch1", "TC6_ProductNameToSearch2" })
    public void TestCase06(String TC6_ProductNameToSearch1, String TC6_ProductNameToSearch2)
            throws InterruptedException {
        Boolean status;

        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Registration failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "Login failed");

        homePage.navigateToHome();
        status = homePage.searchForProduct(TC6_ProductNameToSearch1);
        homePage.addProductToCart(TC6_ProductNameToSearch1);

        status = homePage.searchForProduct(TC6_ProductNameToSearch2);
        homePage.addProductToCart(TC6_ProductNameToSearch2);

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(TC6_ProductNameToSearch1, 2.0);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(TC6_ProductNameToSearch2, 0.0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart(TC6_ProductNameToSearch1, 1.0);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            Assert.assertTrue(false, "Error while placing order: " + e.getMessage());
        }

        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue(status, "Wasn't redirected to the Thanks page");

        homePage.navigateToHome();
        homePage.PerformLogout();
    }

    /*
     * Verify that the cart contents are persisted after logout
     */
    @Test(description = "Verify that the contents made to the cart are saved against the user's login details", priority = 7, groups = {
            "Regression_Test" }, dataProviderClass = DP.class, dataProvider ="data-provider"
    )
    // @Parameters("TC7_ListOfProductsToAddToCart")
    public void TestCase07(String TC7_ListOfProductsToAddToCart) throws InterruptedException {
        Boolean status = false;

        List<String> expectedResult = Arrays.asList(TC7_ListOfProductsToAddToCart.split(";"));

        Register registration = new Register(driver);
        Login login = new Login(driver);
        Home homePage = new Home(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Registration failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "Login failed");

        homePage.navigateToHome();
        status = homePage.searchForProduct(expectedResult.get(0));
        homePage.addProductToCart(expectedResult.get(0));

        status = homePage.searchForProduct(expectedResult.get(1));
        homePage.addProductToCart(expectedResult.get(1));

        homePage.PerformLogout();

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");

        status = homePage.verifyCartContents(expectedResult);
        Assert.assertTrue(status, "Verifying cart contents after logging out and logging in failed");

        homePage.PerformLogout();
    }

    /*
     * Verify insufficient balance message is shown when order price is more than
     * wallet balance
     */
    @Test(description = "Verify that insufficient balance error is thrown when the wallet balance is not enough", priority = 8, groups = {
            "Sanity_test"}, dataProviderClass = DP.class, dataProvider ="data-provider"
    )
    // @Parameters({ "TC8_ProductName", "TC8_Qty" })
    public void TestCase08(String TC8_ProductName, String Qty) throws InterruptedException {

        Double TC8_Qty = Double.parseDouble(Qty);
        Boolean status;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Registration failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "Login failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(TC8_ProductName);
        homePage.addProductToCart(TC8_ProductName);

        homePage.changeProductQuantityinCart(TC8_ProductName, TC8_Qty);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();
        Assert.assertTrue(status, "Insufficient balance message not shown");
    }

    /*
     * Verify that product added to cart is available when a new tab is opened
     */
    @Test(description = "Verify that a product added to a cart is available when a new tab is added", priority = 10, dependsOnMethods = {
            "TestCase10" }, groups = { "Regression_Test" })
    public void TestCase09() throws InterruptedException {
        Boolean status = false;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Registration failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "Login failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);
        Assert.assertTrue(status, "Verification for product in card when new tab is opened failed");

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
    }

    /*
     * Verify that the Privacy Policy, About Us are displayed correctly
     */
    @Test(description = "Verify that privacy policy and about us links are working fine", priority = 9, groups = {
            "Regression_Test" })
    public void TestCase10() throws InterruptedException {
        Boolean status = false;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Registration failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "Login failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        Assert.assertTrue(status, "Parent page url changed on privacy policy link click");

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        Assert.assertTrue(status, "New tab opened doesn't have Privacy Policy page heading content");

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        Assert.assertTrue(status, "New tab opened doesn't have Terms of Service page heading content");

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
    }

    /*
     * Verify that the Contact Us option is working correctly
     */
    @Test(description = "Verify that the contact us dialog works fine", priority = 11, groups = { "Regression_Test" },
    dataProviderClass = DP.class, dataProvider ="data-provider")
    // @Parameters({ "TC11_ContactusUserName", "TC11_ContactUsEmail", "TC11_QueryContent" })
    public void TestCase11(String TC11_ContactusUserName, String TC11_ContactUsEmail, String TC11_QueryContent)
            throws InterruptedException {
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys(TC11_ContactusUserName);
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys(TC11_ContactUsEmail);
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys(TC11_QueryContent);

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));
    }

    /*
     * Verify that the links on the QKART advertisement are clickable
     */
    @Test(description = "Ensure that the Advertisement Links on the QKART page are clickable", priority = 12, groups = {
            "Sanity_test" }, dataProviderClass = DP.class, dataProvider ="data-provider"
    )
    // @Parameters({ "TC12_ProductNameToSearch", "TC12_AddresstoAdd" })
    public void TestCase12(String TC12_ProductNameToSearch, String TC12_AddresstoAdd) throws InterruptedException {
        Boolean status = false;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Registration failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "Login failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct(TC12_ProductNameToSearch);
        homePage.addProductToCart(TC12_ProductNameToSearch);
        homePage.changeProductQuantityinCart(TC12_ProductNameToSearch, 1.0);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(TC12_AddresstoAdd);
        checkoutPage.selectAddress(TC12_AddresstoAdd);
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;
        Assert.assertTrue(status, "Exactly 3 ads with iframes weren't available");

        WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = driver.getCurrentUrl().equals(currentURL);
        // Assert.assertFalse(status, "Clicking on the 'Buy now' button in the ad should redirect the main page");
        Assert.assertFalse(status, "Clicking on the 'Buy now' button in the ad should redirect the main page");
        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = driver.getCurrentUrl().equals(currentURL);
        Assert.assertFalse(status, "Clicking on the 'Buy now' button in the ad should redirect the main page");
    }
    
    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.close();
    }

}
