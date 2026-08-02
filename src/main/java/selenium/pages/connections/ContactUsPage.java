package selenium.pages.connections;

import data.DTO.ContactUs;
import selenium.pages.BasePage;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.pages.HomePage;

import java.time.Duration;

public class ContactUsPage extends BasePage {

    private WebDriverWait wait;
    public ContactUsPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }
    public boolean verifyPageTitle() {
        String TITLE_XPATH = "//h2[text()='Get In Touch']";
        WebElement title = driver.findElement(By.xpath(TITLE_XPATH));
        log.info("Verify Contact US page");
        return title.isDisplayed();
    }

    public ContactUsPage fillContactUsForm(ContactUs form) {
        String NAME_XPATH = "//input[@data-qa='name'] ";
        String EMAIL_XPATH = "//input[@data-qa='email']";
        String SUBJECT_XPATH = "//input[@data-qa='subject']";
        String MESSAGE_XPATH = "//textarea[@data-qa='message']";
        String UPLOAD_FILE_XPATH = "//input[@type='file']";

        WebElement name = driver.findElement(By.xpath(NAME_XPATH));
        WebElement email = driver.findElement(By.xpath(EMAIL_XPATH));
        WebElement subject = driver.findElement(By.xpath(SUBJECT_XPATH));
        WebElement message = driver.findElement(By.xpath(MESSAGE_XPATH));
        driver.findElement(By.xpath(UPLOAD_FILE_XPATH));

        typeText(name, form.getName());
        typeText(email, form.getEmail());
        typeText(subject, form.getSubject());
        typeText(message, form.getMessage());
//        typeText(uploadFile,form.getPath());

        log.info("form => name: {} email: {} subject: {} message: {}", form.getName(), form.getEmail(), form.getSubject(), form.getMessage());
        return this;
    }


    public ContactUsPage submit(){
        String SUBMIT_BUTTON_XPATH = "//input[@data-qa='submit-button']";
        WebElement submitButton = driver.findElement(By.xpath(SUBMIT_BUTTON_XPATH));
        submitButton.click();
        log.info("Submit contact us form");
        Alert alert = driver.switchTo().alert();
        alert.accept();
        return this;
    }

    public String verifySuccessMessage() {
        String ALT_SUCCESS_XPATH = "//div[contains(@class,'alert-success') and @style='display: block;']";
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(ALT_SUCCESS_XPATH)));
        String message = successMessage.getText();
        log.info("Success message: {}", message);
        return message;
    }
    public HomePage _gotoHome(){
        String HOME_BUTTON_ICON_XPATH = "//a[.//i[contains(@class,'fa-angle-double-left')]]";
        WebElement homeButton = driver.findElement(By.xpath(HOME_BUTTON_ICON_XPATH));
        homeButton.click();
        log.info("Goto Home Page");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        return new HomePage(driver);
    }

    private void typeText(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }
}
