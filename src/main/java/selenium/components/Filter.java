package selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import selenium.pages.products.ProductsPage;
import selenium.support.enums.BRAND;
import selenium.support.enums.CATEGORY;

import java.time.Duration;

public class Filter  {

    private final WebDriver driver;
    private final WebDriverWait wait;
    public Filter(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(2));
    }


    public ProductsPage search(CATEGORY category) {
        String womenMain = "//a[@href='#%s' and normalize-space()='%s']".formatted(category.getMainCategory(), category.getMainCategory());
        WebElement categoryElement = driver.findElement(By.xpath(womenMain));
        categoryElement.click();
        String womenTops = "//div[@id='%s']//a[normalize-space()='%s']".formatted(category.getMainCategory(),category.getText());
        WebElement topsElement = driver.findElement(By.xpath(womenTops));
        topsElement.click();
        return new ProductsPage(driver);
    }

    public ProductsPage search(BRAND brand) {
        WebElement selectedBrand = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='brands_products']//a[contains(.,'" + brand.toString() + "')]")
        ));
        selectedBrand.click();
        return new ProductsPage(driver);
    }

}
