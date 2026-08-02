package selenium.pages.products;

import data.DTO.ReviewProduct;
import data.exceptions.ExceptionHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import selenium.pages.BasePage;
import selenium.pages.cart.CartPage;


public class ProductPage extends BasePage {

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String verifyDetails() {
        String BASE_XPATH = "//div[@class='product-information']";

        String NAME_XPATH = BASE_XPATH + "/h2";
        String CATEGORY_XPATH = BASE_XPATH + "/p[contains(text(),'Category')]";
        String RATING_XPATH = BASE_XPATH + "/img[contains(@src,'rating')]";
        String PRICE_XPATH = BASE_XPATH + "//span/span";
        String QUANTITY_XPATH = BASE_XPATH + "//input[@id='quantity']";
        String PRODUCT_ID_XPATH = BASE_XPATH + "//input[@id='product_id']";
        String AVAILABILITY_XPATH = BASE_XPATH + "/p[b[contains(text(),'Availability')]]";
        String CONDITION_XPATH = BASE_XPATH + "/p[b[contains(text(),'Condition')]]";
        String BRAND_XPATH = BASE_XPATH + "/p[b[contains(text(),'Brand')]]";

        String OUTPUT = "";
        try {
            String name = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(NAME_XPATH))).getText();
            String category = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CATEGORY_XPATH))).getText();
            String rating = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(RATING_XPATH))).getAttribute("src");

            int price = Integer.parseInt(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(PRICE_XPATH)))
                            .getText().replace("Rs.", "").trim()
            );
            int quantity = Integer.parseInt(
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(QUANTITY_XPATH)))
                            .getAttribute("value")
            );
            int productId = Integer.parseInt(
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PRODUCT_ID_XPATH)))
                            .getAttribute("value")
            );

            String availability = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AVAILABILITY_XPATH))).getText();
            String condition = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CONDITION_XPATH))).getText();
            String brand = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BRAND_XPATH))).getText();

            OUTPUT = String.format(
                    "Product Details:\n" +
                            "Name: %s\n" +
                            "Category: %s\n" +
                            "Rating Image: %s\n" +
                            "Price: %d\n" +
                            "Quantity: %d\n" +
                            "Product ID: %d\n" +
                            "Availability: %s\n" +
                            "Condition: %s\n" +
                            "Brand: %s",
                    name, category, rating, price, quantity, productId, availability, condition, brand
            );

            if (OUTPUT.isEmpty()) {
                log.error("Something is wrong");
            }

            log.info(
                    "Product #{} Details: {{}, Price: {},  {}, {}, {}}",
                    name, category, price, availability, condition, brand
            );

        } catch (Exception e) {
            ExceptionHandler.handleSilently(e, "getting product details");
        }

        return OUTPUT;
    }

    public ProductPage setQuantity(int quantity) {

        WebElement quantityElement = driver.findElement(By.xpath("//div[@class='product-information']//input[@id='quantity']"));
        quantityElement.clear();
        quantityElement.sendKeys(String.valueOf(quantity));
        log.info("Selected Quantity: {}", quantity);
        return this;
    }

    public ProductPage writeYourReview(ReviewProduct form) {


        WebElement nameElement = driver.findElement(By.xpath("//form[@id='review-form']//input[@id='name']"));
        WebElement emailElement = driver.findElement(By.xpath("//form[@id='review-form']//input[@id='email']"));
        WebElement messageElement = driver.findElement(By.xpath("//form[@id='review-form']//textarea[@id='review']"));
        WebElement submitButton = driver.findElement(By.xpath("//form[@id='review-form']//button[@id='button-review']"));

        nameElement.clear();
        emailElement.clear();
        messageElement.clear();

        nameElement.sendKeys(form.getName());
        emailElement.sendKeys(form.getEmail());
        messageElement.sendKeys(form.getMessage());

        submitButton.click();
        return this;
    }


    public ProductPage addToCart() {
        WebElement addToCartButton = driver.findElement(By.xpath("//button[contains(@class, 'tests.cart')]"));
        addToCartButton.click();
        return this;
    }


    public ProductPage continueShopping() {
        WebElement continueButton = wait
                .until(ExpectedConditions
                        .visibilityOfElementLocated(
                                By.xpath("//button[@class='btn btn-success close-modal btn-block']")
                        )
                );
        continueButton.click();
        return this;
    }

    public CartPage viewCart() {
        WebElement viewCartButton = wait
                .until(ExpectedConditions
                        .visibilityOfElementLocated(
                                By.xpath("//a[@href='/view_cart']\n")
                        )
                );
        viewCartButton.click();
        return new CartPage(driver);
    }

    public String ensureReviewSuccess() {
        WebElement message = driver.findElement(By.xpath("//div[@id='review-section']//div[@class='alert-success alert']//span"));
        return message.getText();
    }
}
