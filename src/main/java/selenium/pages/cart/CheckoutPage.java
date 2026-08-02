package selenium.pages.cart;

import data.DTO.CartItem;
import selenium.pages.BasePage;
import cache.models.ProductCache;
import cache.models.SessionCache;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import selenium.pages.orders.PaymentPage;
import cache.services.CartServiceImpl;
import cache.services.SessionServiceImpl;

import java.util.List;

public class CheckoutPage extends BasePage {

    private final SessionServiceImpl sessionService ;
    private final CartServiceImpl cartService;
    public CheckoutPage(WebDriver driver) {
        super(driver);
        sessionService = new SessionServiceImpl();
        cartService = new CartServiceImpl();
    }

    public boolean verifyOrderItems(){
        String TOTAL_PRICE_XPATH = "//div[@id='cart_info']//tr[last()]/td/p[@class='cart_total_price']";
        List<WebElement> productRows = driver.findElements(By.cssSelector(".cart_info > table > tbody > tr"));


        for (WebElement productRow : productRows) {
            CartItem item = getProductsCheckout(productRow);
            for (ProductCache cacheItem : cartService.viewCartItems()) {

                boolean condition = item.getProductName().equals(cacheItem.getProductName())
                        || item.getPrice()==cacheItem.getPrice()
                        || item.getQuantity()==cacheItem.getQuantity()
                        || item.getTotalPrice()==cacheItem.totalPrice();

                if (condition)
                    log.info("Product {} verified", item.getProductName());
                else {
                    log.warn("Product {} not verified", item.getProductName());
                    return false;
                }

            }
        }
        WebElement totalElement = driver.findElement(By.xpath(TOTAL_PRICE_XPATH));
        int totalPrice = Integer.parseInt(totalElement.getText().replaceAll("\\D+", ""));
        int expectedTotalPrice = cartService.calculateCartTotal();
        if(expectedTotalPrice != totalPrice)
        {
            log.info("Total price is not verified");
            return false;
        }
        log.info("Total price verified: {}",expectedTotalPrice);
        return true;
    }
    public boolean reviewOrderDetails() {
            if (verifyDeliveryAddress() || verifyBillingAddress() || verifyOrderItems())
            {
                log.info("Order details verified");
                return true;
            }
            log.warn("Order details not verified");
            return false;
    }

    public boolean verifyDeliveryAddress(){
        String DELIVERY_ADDRESS_XPATH = "//div[@id='address_delivery']";
        WebElement delivery = driver.findElement(By.xpath(DELIVERY_ADDRESS_XPATH));
        return verifyPersonalDetails(delivery);
    }
    public boolean verifyBillingAddress(){
        String BILLING_ADDRESS_XPATH = "//ul[@id='address_invoice']";
        WebElement billing = driver.findElement(By.xpath(BILLING_ADDRESS_XPATH));
        return verifyPersonalDetails(billing);
    }


    public CheckoutPage addComment(String comment) {
        String COMMENT_XPATH = "//div[@id='ordermsg']//textarea[@name='message']";
        WebElement review = driver.findElement(By.xpath(COMMENT_XPATH));
        review.click();
        review.sendKeys(comment);
        return this;
    }

    public PaymentPage submitOrder() {
        String SUBMIT_ORDER_XPATH = "//a[contains(@class, 'check_out') and @href='/payment' and normalize-space(text())='Place Order']";
        WebElement submitOrder = driver.findElement(By.xpath(SUBMIT_ORDER_XPATH));
        submitOrder.click();
        log.info("Clicked on Place Order");
        return new PaymentPage(driver);
    }




    private CartItem getProductsCheckout(WebElement row){
        String name = row.findElement(By.cssSelector(".cart_description h4 a")).getText();

        String priceText = row.findElement(By.cssSelector(".cart_price p")).getText().trim();
        int price = Integer.parseInt(priceText.replaceAll("\\D", ""));

        String quantityText = row.findElement(By.cssSelector(".cart_quantity button")).getText().trim();
        int quantity = Integer.parseInt(quantityText);

        String totalText = row.findElement(By.cssSelector(".cart_total_price")).getText().trim();
        int itemTotal = Integer.parseInt(totalText.replaceAll("\\D", ""));

        return CartItem.builder()
                .productName(name)
                .price(price)
                .quantity(quantity)
                .totalPrice(itemTotal)
                .build();
    }

    private boolean verifyPersonalDetails(WebElement element){
        String fullName = element.findElement(By.xpath("./li[2]")).getText().trim();
        String company = element.findElement(By.xpath("./li[3]")).getText().trim();
        String address1 = element.findElement(By.xpath("./li[4]")).getText().trim();
        String address2 = element.findElement(By.xpath("./li[5]")).getText().trim();
        String cityStateZip = element.findElement(By.xpath("./li[6]")).getText().trim();
        String country = element.findElement(By.xpath("./li[7]")).getText().trim();
        String phone = element.findElement(By.xpath("./li[8]")).getText().trim();

        String actual = String.join("\n",
                fullName,
                company,
                address1,
                address2,
                cityStateZip,
                country,
                phone
        );
        SessionCache sessionCache = sessionService.currentSession();
        return actual.equals(sessionCache.getAccountInfo());
    }

}
