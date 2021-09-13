import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaceOrder {

    private static final String[] orderDetails = new String[]{
            "darziauto@gmail.com", "Abdul", "Hadi", "03224971876",
            "323 St 14 GG Phase 4 GG Phase 4 DHA", "Pakistan", "Punjab", "Lahore"};

    public static List<String[]> grabURLFromRecord(int searchColumnIndex, String searchString) throws IOException {

        String line;
        String splitBy = ",";
        String[] record;
        List<String[]> unplacedOrders = new ArrayList<>();
        //parsing a CSV file into BufferedReader class constructor
        BufferedReader br = new BufferedReader(new FileReader("Khaadi_TwoPiece.csv"));
        while ((line = br.readLine()) != null) {
            String[] order = line.split(splitBy);    // use comma as separator
            if(order[searchColumnIndex].equals(searchString)) {
                record = line.split(splitBy);
                unplacedOrders.add(new String[] {record[0], record[1], record[2], record[3],
                        record[4], record[5], record[6], record[7], record[8]});
            }
        }
        br.close();

        return unplacedOrders;
    }

    public static void automateOrder() throws IOException, InterruptedException {

        List<String[]> record = grabURLFromRecord(8, "F");

        System.setProperty("webdriver.chrome.driver","libs/chromedriver");
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver,20);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        driver.get("https://www.google.com/");
        driver.manage().window().maximize();
        ((JavascriptExecutor)driver).executeScript("window.open()");
        js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        int i=0;

        for(String[] temp : record) {
            driver.switchTo().window(tabs.get(i));
            i++;
            String URL = temp[7];
            driver.get(URL);
            String expectedSKU = temp[2];
            String expectedAvailability = "In stock";
            String actualSKU = driver.findElement(By.xpath("//div[@itemprop='sku']")).getText();
            String actualAvailability = driver.findElement(
                    By.xpath("//span[text()='Availability:']//following-sibling::span")).getText();
            assert Objects.equals(actualAvailability, expectedAvailability);
            assert Objects.equals(actualSKU, expectedSKU);

            Thread.sleep(5000);

            WebElement addToBag =
                    wait.until(ExpectedConditions.elementToBeClickable(By.id("product-addtocart-button")));
            addToBag.click();

            WebElement continueShopping =
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Continue Shopping']")));
            continueShopping.click();
        }

        WebElement bagIcon =
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='bottom-panel']")));
        bagIcon.click();

        WebElement checkoutButton =
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("top-cart-btn-checkout")));
        assert checkoutButton.isDisplayed();
        checkoutButton.click();

        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("shipping")));
        assert form.isDisplayed();

        WebElement emailElement = driver.findElement(By.id("customer-email"));
        WebElement firstNameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@name='shippingAddress.firstname']//input[@name='firstname']")));
        WebElement lastNameElement = driver.findElement(By.xpath("//div[@name='shippingAddress.lastname']//input[@name='lastname']"));
        WebElement cellNumberElement = driver.findElement(By.xpath("//div[@name='shippingAddress.telephone']//input[@name='telephone']"));
        WebElement addressElement = driver.findElement(By.xpath("//div[@name='shippingAddress.street.0']//input[@name='street[0]']"));
        WebElement countryElement = driver.findElement(By.xpath("//select[@name='country_id']"));
        WebElement provinceElement = driver.findElement(By.xpath("//select[@name='region_id']"));
        WebElement cityElement = driver.findElement(By.xpath("//select[@name='city_id']"));

        emailElement.sendKeys(orderDetails[0]);
        firstNameElement.sendKeys(orderDetails[1]);
        lastNameElement.sendKeys(orderDetails[2]);
        cellNumberElement.sendKeys(orderDetails[3]);
        addressElement.sendKeys(orderDetails[4]);
        countryElement.sendKeys(orderDetails[5]);
        provinceElement.sendKeys(orderDetails[6]);
        cityElement.sendKeys(orderDetails[7]);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        automateOrder();
    }
}