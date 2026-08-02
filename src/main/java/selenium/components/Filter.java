package selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import selenium.pages.products.ProductsPage;

import java.time.Duration;

public class Filter  {

    private final WebDriver driver;
    private final WebDriverWait wait;
    public Filter(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(2));
    }


    public ProductsPage search(String userType, String category) {
        String mainCategoryXpath = "//a[@href='#%s' and normalize-space()='%s']".formatted(userType, userType);
        WebElement categoryElement = driver.findElement(By.xpath(mainCategoryXpath));
        categoryElement.click();
        String subCategoryXpath = "//div[@id='%s']//a[normalize-space()='%s']".formatted(userType, category);
        WebElement subCategoryElement = driver.findElement(By.xpath(subCategoryXpath));
        subCategoryElement.click();
        return new ProductsPage(driver);
    }

    public ProductsPage search(String brand) {
        WebElement selectedBrand = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='brands_products']//a[contains(.,'" + brand + "')]")
        ));
        selectedBrand.click();
        return new ProductsPage(driver);
    }

}
