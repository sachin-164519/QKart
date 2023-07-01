package Qkart_QA.Module;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Home {
    ChromeDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app";

    public Home(ChromeDriver driver){
        this.driver = driver;
    }

    public void navigateToHome(){
        if(!this.driver.getCurrentUrl().equals(this.url)){
            this.driver.get(this.url);
        }
    }

    public boolean performLogout(){
        WebElement logoutBtn = driver.findElement(By.xpath("//button[contains(text(),'Logout')]"));
        logoutBtn.click();
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOfElementWithText(By.xpath("//button[contains(text(),'Logout')]"), "Logout"));
        return true;
    }

    public boolean searchForProduct(String product){
        try{
            WebElement searchbox = driver.findElement(By.xpath("//input[@name='search'][1]"));
            searchbox.clear();
            searchbox.sendKeys(product);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.or
            (ExpectedConditions.textToBePresentInElementLocated(By.xpath("//p[contains(@class,'css-yg30e6')]"), product),
            ExpectedConditions.presenceOfElementLocated(By.xpath("//h4[contains(text(),'No products found')]"))
            ));
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<WebElement> getSearchResults(){
        List<WebElement> searchResults = new ArrayList<>();
        try{
            searchResults = driver.findElements(By.xpath("//div[contains(@class,'css-1qw96cp')]"));
            return searchResults;

        }catch(Exception e){
            e.printStackTrace();
            return searchResults;
        }
    }

    public boolean isNoResultFound(){
        boolean status = false;
        try{
            status = driver.findElement(By.xpath("//h4[contains(text(),'No products found')]")).isDisplayed();
            return status;
        }catch(Exception e){
            e.printStackTrace();
            return status;
        }
    }

    public boolean addProductToCart(String productName){
        try{
            List<WebElement> gridContent = driver.findElements(By.xpath("//div[contains(@class,'css-sycj1h')]"));
            for(WebElement cell: gridContent){
                String product = cell.findElement(By.xpath("//p[contains(@class,'css-yg30e6')]")).getText();
                if(productName.contains(product)){
                    cell.findElement(By.xpath("productName")).click();
                    WebDriverWait wait = new WebDriverWait(driver, 30);
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@class='MuiBox-root css-1gjj37g']/div[1][text()='"+productName+"']")));
                    return true;
                }
            }
            System.out.println("Unalbe to find the given product: "+productName);
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean clickCheckout(){
        try{
            WebElement checkoutBtn = driver.findElement(By.className("checkout-btn"));
            checkoutBtn.click();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean changeProductQuantityinCart(String productName, int quantity){
        try {
            WebElement cartParent = driver.findElement(By.className("cart"));
            List<WebElement> cartContents = cartParent.findElements(By.className("css-zgtx0t"));

            int currentQty;
            for (WebElement item : cartContents) {
                if (productName.contains(item.findElement(By.xpath("//*[@class='MuiBox-root css-1gjj37g']/div[1]")).getText())) {
                    currentQty = Integer.valueOf(item.findElement(By.className("css-olyig7")).getText());

                    while (currentQty != quantity) {
                        if (currentQty < quantity) {
                            item.findElements(By.tagName("button")).get(1).click();
                         
                        } else {
                            item.findElements(By.tagName("button")).get(0).click();
                        }

                        synchronized (driver){
                            driver.wait(2000);
                        }

                        currentQty = Integer
                                .valueOf(item.findElement(By.xpath("//div[@data-testid=\"item-qty\"]")).getText());
                    }

                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            if (quantity == 0)
                return true;
            System.out.println(("exception occurred when updating cart"));
            return false;
        }
    }

    /*
     * Return Boolean denoting if the cart contains items as expected
     */
    public Boolean verifyCartContents(List<String> expectedCartContents) {
        try {


            WebElement cartParent = driver.findElement(By.className("cart"));
            List<WebElement> cartContents = cartParent.findElements(By.className("css-zgtx0t"));

            ArrayList<String> actualCartContents = new ArrayList<String>() {
            };
            for (WebElement cartItem : cartContents) {
                actualCartContents.add(cartItem.findElement(By.className("css-1gjj37g")).getText().split("\n")[0]);
            }

            for (String expected : expectedCartContents) {
                if (!actualCartContents.contains(expected.trim())) {
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            System.out.println("Exception while verifying cart contents: " + e.getMessage());
            return false;
        }
    }
    
}
