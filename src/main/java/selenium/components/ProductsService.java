package selenium.components;

import cache.models.ProductCache;
import data.exceptions.ExceptionHandler;
import data.expectations.Expectations;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import selenium.pages.cart.CartPage;
import selenium.pages.products.ProductPage;
import cache.services.CartServiceImpl;

import java.time.Duration;
import java.util.*;

public class ProductsService  {

    // Initialization
    private static final Logger log = LoggerFactory.getLogger(ProductsService.class);
    private final WebDriver driver;
    private final Actions actions;
    private final WebDriverWait wait;
    private final CartServiceImpl cartService;

    public int verifyAllProducts(){
        String PRODUCT_XPATH= "//div[@class='productinfo text-center']";
        List<WebElement> products = driver.findElements(By.xpath(PRODUCT_XPATH));
        log.info("Found {} products", products.size());
        return products.size();
    }

    public ProductsService(WebDriver driver) {
       this.driver = driver;
       this.actions = new Actions(driver);
       this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
       cartService = new CartServiceImpl();
    }

    @Step("Add Product to Cart")
    public ProductsService addToCart(String productName,int quantity) {
        return this.addToCart(Map.of(productName, quantity));
    }



    @Step("Ensure the the Product added to Cart")
    public boolean verifyAddMessage() {
        WebElement verifyAddMessage = driver.findElement(By.cssSelector("#cartModal > div > div > div.modal-body > p:nth-child(1)"));
        wait.until(ExpectedConditions.visibilityOf(verifyAddMessage));
        return verifyAddMessage.isDisplayed() && verifyAddMessage.getText().contentEquals(Expectations.Ui.Cart.MESSAGE);
    }

    @Step("Continue Shopping")
    public ProductsService continueShopping() {
        By model = new By.ById("cartModal");
        WebElement cartModel = wait.until(ExpectedConditions.visibilityOfElementLocated(model));
        if (verifyAddMessage()) {
            WebElement continueShopping = cartModel.findElement(By.cssSelector("div > div > div.modal-footer > button"));
            continueShopping.click();
        }
        return this;
    }

    @Step("Add selected products")
    public ProductsService addToCart(Map<String, Integer> products) {

        //show products and quantity for each one
        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            log.info("Product: {} - Quantity: {}", entry.getKey(), entry.getValue());
        }

        // save all web elements to do actions on
        List<WebElement> matchedCards = new ArrayList<>();
        for (String name : products.keySet()) {
            WebElement matchedCard = driver.findElement(By.xpath(
                    "//div[contains(@class,'single-products') and .//div[contains(@class,'productinfo')]//p[normalize-space()='%s']]"
                            .formatted(name)));
            matchedCards.add(matchedCard);
        }
        int prodIndex=0;

        for (Map.Entry<String, Integer> product : products.entrySet()) {
            WebElement matchedCard = matchedCards.get(prodIndex);


            try {
                for (int q = 0; q < product.getValue(); q++) {
                    actions.moveToElement(matchedCard).perform();
                    WebElement overlay = wait.until(ExpectedConditions.visibilityOf(
                            matchedCard.findElement(By.cssSelector(".product-overlay"))
                    ));

                    WebElement addToCart = overlay.findElement(By.cssSelector("a.add-to-cart"));

                    actions.moveToElement(addToCart).perform();
                    wait.until(ExpectedConditions.elementToBeClickable(addToCart)).click();
                    log.info("Added '{}' to tests.cart (qty: {}/{})", product.getKey(), q + 1, product.getValue());


                    boolean isLastProduct = (prodIndex == products.size() - 1);
                    boolean isLastQuantity = (q == product.getValue() - 1);

                    if (!(isLastProduct && isLastQuantity)) {
                        waitForCartModal();
                        continueShopping();
                        waitForCartModalToDisappear();
                    }
                    if (q == product.getValue() - 1)
                        log.info("Product #{} Added", prodIndex + 1);
                }

            } catch (Exception e) {
                ExceptionHandler.handleSilently(e, "adding product #" + prodIndex + " to cart");
            }
            prodIndex++;

        }
        return this;
    }



    @Step("View Cart")
    public CartPage viewCart() {

        WebElement viewCartLink = driver.findElement(By.linkText("View Cart"));
        wait.until(ExpectedConditions.elementToBeClickable(viewCartLink)).click();

        return new CartPage(driver);
    }

    public ProductPage viewProduct() {
        List<ProductCache> cached = cartService.viewCartItems().stream().toList();
        Random random = new Random();
        ProductCache productCache =  cached.get(random.nextInt(cached.size()));
        gotoProductPage(productCache.getProductName());
        return new ProductPage(driver);
    }

    public ProductPage viewProduct(String productName) {
        gotoProductPage(productName);
        return new ProductPage(driver);
    }


    // helper functions
    private void waitForCartModalToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("cartModal")));
        } catch (Exception e) {
            ExceptionHandler.handleSilently(e, "waiting for cart modal to disappear");
        }
    }

    private void waitForCartModal() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cartModal")));
    }

    private Scrolling scroll(){
        return new Scrolling(driver);
    };
    private void gotoProductPage(String productName) {
        String XPATH_PRODUCT_CARD = "//div[contains(@class,'product-image-wrapper') and .//p[normalize-space()='%s']]"
                .formatted(productName);
        WebElement productCard = driver.findElement(By.xpath(XPATH_PRODUCT_CARD));
        actions.moveToElement(productCard).perform();

        String XPATH_OVERLAY_BUTTON = "//div[@class='overlay-content']/p[normalize-space()='%s']/following-sibling::a"
                .formatted(productName);
        WebElement overlayBtn = driver.findElement(By.xpath(XPATH_OVERLAY_BUTTON));
        String productId = overlayBtn.getAttribute("data-product-id");

        log.info("Hovered on '{}' → ID = {}", productName, productId);

        String XPATH_VIEW_PRODUCT = "//a[contains(@href,'/product_details/%s')]".formatted(productId);
        WebElement viewProductLink = driver.findElement(By.xpath(XPATH_VIEW_PRODUCT));
        scroll().downWithArrow();
        scroll().downWithArrow();
        viewProductLink.click();
    }




}
