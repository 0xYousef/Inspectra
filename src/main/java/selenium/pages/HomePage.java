package selenium.pages;

import selenium.components.RecommendedProducts;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import selenium.components.ProductsService;
import selenium.components.Filter;

import java.util.List;
import java.util.Random;


public class HomePage extends BasePage {


    private final ProductsService productsService;

    @Getter
    private final Filter filter;

    @Getter
    private final RecommendedProducts recommendedProducts;

    @FindBy(css = ".single-widget > h2:nth-child(1)") private WebElement subscription;

    public HomePage(WebDriver driver) {
        super(driver);
        productsService = new ProductsService(driver);
        filter = new Filter(driver);
        recommendedProducts = new RecommendedProducts(driver);
    }


    public ProductsService order() {
        return this.productsService;
    }

    @Step("verify Home page")
    public String verify() {
        WebElement verifyHomePage = driver.findElement(By.tagName("h2"));
        String message = verifyHomePage.getText();
        log.info("Message: {}", message);
        return message;
    }


    public HomePage selectRandomItems(){
        String xpath = "//div[@id='recommended-item-carousel']//div[contains(@class,'item active')]//div[@class='product-image-wrapper']";

        // Wait for visible items inside the active carousel item
        List<WebElement> visibleItems = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));

        if (visibleItems.isEmpty()) {
            log.warn("No recommended items found in the active carousel.");
            return this;
        }

        // Pick a random item
        int randomIndex = new Random().nextInt(visibleItems.size());
        WebElement selectedItem = visibleItems.get(randomIndex);

        // Click the "Add to tests.cart" button within the selected item
        WebElement addToCartBtn = selectedItem.findElement(By.xpath(".//a[contains(@class,'add-to-tests.cart')]"));
        wait.until(ExpectedConditions.elementToBeClickable(addToCartBtn)).click();

        log.info("Clicked Add to tests.cart for visible item at index: {}", randomIndex);
        return this;
    }



}
