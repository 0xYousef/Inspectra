package selenium.pages.cart;

import cache.models.ProductCache;
import data.exceptions.ExceptionHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import selenium.pages.BasePage;
import selenium.pages.products.ProductPage;
import cache.services.CartServiceImpl;

import java.util.List;
import java.util.Map;


public class CartPage extends BasePage  {
    private final List<ProductCache> cartItems = List.of();

    public CartPage(WebDriver driver) {
        super(driver);
    }
    private final CartServiceImpl cartService = new CartServiceImpl();

    public CheckoutPage proceedToCheckout() {
        WebElement checkout = driver.findElement(By.linkText("Proceed To Checkout"));
        checkout.click();
        log.info("Proceeding to checkout");
        return new CheckoutPage(driver);
    }

    public String isCartEmpty() {
        By emptyCartLocator = By.cssSelector("#empty_cart > p");
        WebElement emptyCart = wait.until(ExpectedConditions.visibilityOfElementLocated(emptyCartLocator));
        if (cartService.viewCartItems().isEmpty()) {
            log.info("Cart is empty");
            return emptyCart.getText();
        }
        log.info("Cart is not empty");
        return null;
    }


    public String verifyDisplayedCart() {
        try {
            WebElement breadcrumb = driver.findElement(By.cssSelector(".breadcrumbs .breadcrumb li.active"));
            return breadcrumb.getText().trim();
        } catch (Exception e) {
            ExceptionHandler.handleSilently(e, "verifying breadcrumb");
        }
        return null;
    }

    public boolean verifyItems(Map<String, Integer> items) {

            try {
                for (ProductCache uiProduct : cartService.viewCartItems()) {

                    if (!verifyProductMatchesCache(uiProduct.getProductName(), uiProduct.getQuantity(), uiProduct.getPrice())) {
                        log.warn("Mismatch for product {}: UI quantity {}, UI total {}, doesn't match cache",
                                uiProduct.getProductName(), uiProduct.getQuantity(), uiProduct.getPrice() * uiProduct.getQuantity());
                        return false;
                    }

                    if (items.containsKey(uiProduct.getProductName()) && !items.get(uiProduct.getProductName()).equals(uiProduct.getQuantity())) {
                        log.warn("Expected quantity mismatch for product {}: expected {}, found {}",
                                uiProduct.getProductName(), items.get(uiProduct.getProductName()), uiProduct.getQuantity());
                        return false;
                    }
                }

            } catch (Exception e) {
                ExceptionHandler.handleSilently(e, "verifying product row");
                return false;
            }

        return true;
    }
    private List<ProductCache> viewCartItems_unused() {
        List<WebElement> rows = driver.findElements(By.cssSelector("tbody tr"));
        for (WebElement row : rows) {
            ProductCache uiProduct = extractProductFromRow(row);
            cartItems.add(uiProduct);
        }
        return cartItems;
    }


    public CartPage cancelProduct(String productName) {
        List<WebElement> productRows = driver.findElements(By.cssSelector("tbody tr"));

        for (WebElement row : productRows) {
            ProductCache uiProduct = extractProductFromRow(row);

            if (uiProduct.getProductName().equalsIgnoreCase(productName)) {
                log.info("Verifying product '{}' before removal...", uiProduct.getProductName());

                if (verifyProductMatchesCache(uiProduct.getProductName(), uiProduct.getQuantity(), uiProduct.getPrice() * uiProduct.getQuantity())) {
                    log.warn("Product '{}' in UI does not match cached data. Skipping deletion.", uiProduct.getProductName());
                    return new CartPage(driver);
                }
                WebElement deleteButton = row.findElement(By.cssSelector(".cart_quantity_delete"));
                deleteButton.click();
                removeFromCart(uiProduct.getProductName());
                cartService.removeFromCart(uiProduct.getProductName());
                log.info("Removed product '{}' from UI and cache", uiProduct.getProductName());
                break;
            }
        }
        return this;
    }

    private void removeFromCart(String productName) {
    }

    public ProductPage gotoProductPageWithHere() {
        WebElement productPage = driver.findElement(By.linkText("here"));
        productPage.click();
        return new ProductPage(driver);
    }

    private boolean verifyProductMatchesCache(String productName, int uiQuantity, int uiTotalPrice) {
        for (ProductCache item : cartService.viewCartItems()) {

            String cacheProductName = item.getProductName();
            int cacheQuantity = item.getQuantity();
            int cacheTotalPrice = item.getPrice() * item.getQuantity();

            if (cacheProductName.equalsIgnoreCase(productName) && cacheQuantity == uiQuantity && uiTotalPrice == cacheTotalPrice) {
                log.info("Product # {} verified", cacheProductName);
                return true;
            }
        }
        return false;
    }

    private ProductCache extractProductFromRow(WebElement row) {
        String name = row.findElement(By.cssSelector(".cart_description h4 a")).getText().trim();
        int quantity = Integer.parseInt(row.findElement(By.cssSelector(".cart_quantity button")).getText().trim());
        int totalPrice = Integer.parseInt(row.findElement(By.cssSelector(".cart_total_price")).getText().replaceAll("[^0-9]", ""));
        return ProductCache.builder().productName(name).price(totalPrice).quantity(quantity).build();
    }

}
