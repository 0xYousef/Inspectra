package selenium.pages.orders;


import data.DTO.Payment;
import selenium.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.validators.ValidOrderPage;


public class PaymentPage extends BasePage {
    public PaymentPage(WebDriver driver) {
        super(driver);
    }

    public String verifyTitle() {
        String title_xpath = "//h2[@class='heading' and contains(text(),'Payment')]";
        WebElement title_element = driver.findElement(By.xpath(title_xpath));
        return title_element.getText().strip();
    }


    public PaymentPage fillPaymentForm(Payment form) {
        String cardName_xpath = "//input[@name='name_on_card']";
        String cardNumber_xpath = "//input[@name='card_number']";
        String cvc_xpath = "//input[@name='cvcField']";
        String expiredMonth_xpath = "//input[@name='expiry_month']";
        String expiredYear_xpath = "//input[@name='expiry_year']";

        WebElement cardNameField = driver.findElement(By.xpath(cardName_xpath));
        WebElement cardNumberField = driver.findElement(By.xpath(cardNumber_xpath));
        WebElement cvcField = driver.findElement(By.xpath(cvc_xpath));
        WebElement expiredMonthField = driver.findElement(By.xpath(expiredMonth_xpath));
        WebElement expiredYearField = driver.findElement(By.xpath(expiredYear_xpath));

        cardNameField.clear();
        cardNumberField.clear();
        cvcField.clear();
        expiredMonthField.clear();
        expiredYearField.clear();

        cardNameField.sendKeys(form.getCardName());
        cardNumberField.sendKeys(form.getCardNumber());
        cvcField.sendKeys(form.getCvc());

        // You can return "form.date()"

        expiredMonthField.sendKeys(String.valueOf(form.getMonth()));
        expiredYearField.sendKeys(String.valueOf(form.getYear()));

        return this;
    }

    public ValidOrderPage payAndConfirmOrder() {
        String submitButton_xpath = "//button[@id='submit']";
        WebElement submitButton = driver.findElement(By.xpath(submitButton_xpath));
        submitButton.click();
        return new ValidOrderPage(driver);
    }

}
