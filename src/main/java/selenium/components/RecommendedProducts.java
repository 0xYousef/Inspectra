package selenium.components;

import data.DTO.ProductInfo;
import data.exceptions.ExceptionHandler;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cache.services.CartServiceImpl;

import java.time.Duration;
import java.util.Set;

import static selenium.mapper.ProductMapper.InfoToCache;

public class RecommendedProducts {

    private static final Logger log = LoggerFactory.getLogger(RecommendedProducts.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;
    private final ModelDialog modal ;
    private final CartServiceImpl cartService ;

    public RecommendedProducts(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.actions = new Actions(driver);
        this.modal = new ModelDialog(driver);
        this.cartService = new CartServiceImpl();
    }

    public void selectProducts(Set<ProductInfo> products) {
        String titleXpath = "//div[@class='recommended_items']//h2[@class='title text-center']";
        WebElement title = driver.findElement(By.xpath(titleXpath));
        if (title != null)
            log.info("verify Title : {}", title.getText());
        actions.moveToElement(driver.findElement(By.id("footer")));
        for (ProductInfo product : products) {
            boolean found = addToCart(product.getName(), product.getQuantity());
            if (found) {
                cartService.addToCart(InfoToCache(product));
                log.info("Selected product {} with quantity {}", product.getName(), product.getQuantity());
            }
        }
    }

    private boolean addToCart(String productName, int quantity) {

        String productXpath = String.format(
                "//div[@id='recommended-item-carousel']" +
                "//div[contains(@class,'item active')]" +
                "//div[@class='productinfo text-center'][p[normalize-space(text())='%s']]",
                productName
        );
        String activeSlideXpath = "//div[@id='recommended-item-carousel']//div[contains(@class,'item active')]";
        String firstActiveClass = driver.findElement(By.xpath(activeSlideXpath)).getAttribute("class");

        while (true) {
            try {

                WebElement product = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(productXpath)));
                actions.moveToElement(product).perform();
                WebElement productLink = wait.until(ExpectedConditions.elementToBeClickable(product.findElement(By.tagName("a"))));
                log.info("Found product '{}' in active slide. Adding {} times...", productName, quantity);
                for (int i = 0; i < quantity; i++) {
                    actions.moveToElement(productLink).click().perform();
                    modal.continueButton();
                    log.info("Added '{}' to cart ({}/{})", productName, i+1, quantity);
                }
                return true;
            } catch (TimeoutException | ElementNotInteractableException | StaleElementReferenceException e) {
                ExceptionHandler.handleSilently(e, "locating product '" + productName + "' in recommended items slider");
                nextBar();
                WebElement currentActiveSlide = driver.findElement(By.xpath(activeSlideXpath));
                if (currentActiveSlide.getAttribute("class").equals(firstActiveClass)) {
                    log.warn("Product '{}' not found in any recommended items slider.", productName);
                    return false;
                }
                log.info("Product '{}' not found in this slide. Moving to next...", productName);
            }
        }
    }


    public void nextBar() {
        try {
            WebElement next = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".recommended-item-control.right")));
            actions.moveToElement(next).click().perform();
        } catch (Exception e) {
            ExceptionHandler.handleSilently(e, "clicking next carousel arrow");
        }
    }

    public void previousBar() {
        try {
            WebElement prev = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".recommended-item-control.left")));
            actions.moveToElement(prev).click().perform();
        } catch (Exception e) {
            ExceptionHandler.handleSilently(e, "clicking previous carousel arrow");
        }
    }
}
