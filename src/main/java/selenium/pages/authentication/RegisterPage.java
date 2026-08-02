package selenium.pages.authentication;

import data.DTO.Register;
import cache.models.UserInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.pages.BasePage;
import cache.services.UserProfileServiceImpl;
import selenium.validators.ValidationPage;

import java.time.Duration;

import static selenium.mapper.UserMapper.mapRegisterToUserInfo;

public class RegisterPage extends BasePage {

    private final UserProfileServiceImpl userService;
    // XPath locators
    private static final String GENDER_MALE_XPATH = "//input[@id='id_gender1']";
    private static final String NAME_XPATH = "//input[@id='name']";
    private static final String PASSWORD_XPATH = "//input[@id='password']";
    private static final String DAYS_XPATH = "//select[@id='days']";
    private static final String MONTHS_XPATH = "//select[@id='months']";
    private static final String YEARS_XPATH = "//select[@id='years']";
    private static final String NEWSLETTER_XPATH = "//input[@id='newsletter']";
    private static final String SPECIAL_OFFERS_XPATH = "//input[@id='optin']";
    private static final String FIRST_NAME_XPATH = "//input[@id='first_name']";
    private static final String LAST_NAME_XPATH = "//input[@id='last_name']";
    private static final String COMPANY_XPATH = "//input[@id='company']";
    private static final String COUNTRY_XPATH = "//select[@id='country']";
    private static final String ADDRESS1_XPATH = "//input[@id='address1']";
    private static final String ADDRESS2_XPATH = "//input[@id='address2']";
    private static final String STATE_XPATH = "//input[@id='state']";
    private static final String CITY_XPATH = "//input[@id='city']";
    private static final String ZIPCODE_XPATH = "//input[@id='zipcode']";
    private static final String MOBILE_NUMBER_XPATH = "//input[@id='mobile_number']";
    private static final String SUBMIT_XPATH = "//button[contains(@class, 'btn') and contains(text(), 'Create Account')]";

    private UserInfo registeredData = null;
    public RegisterPage(WebDriver driver) {
        super(driver);
        userService = new UserProfileServiceImpl();
    }

    public RegisterPage fillAllRegisterForm(Register form) {
       fillMandatoryRegisterForm(form);

        clickElement(By.xpath(GENDER_MALE_XPATH));

        selectDropdown(By.xpath(DAYS_XPATH), String.valueOf(form.getDay()));
        selectDropdown(By.xpath(MONTHS_XPATH), String.valueOf(form.getMonth()));
        selectDropdown(By.xpath(YEARS_XPATH), String.valueOf(form.getYear()));

        clickIfTrue(By.xpath(NEWSLETTER_XPATH), form.isNewsletter());
        clickIfTrue(By.xpath(SPECIAL_OFFERS_XPATH), form.isSpecialOffers());
        typeText(By.xpath(COMPANY_XPATH), form.getCompany());

        typeText(By.xpath(ADDRESS2_XPATH), form.getAddress2());
        return this;
    }
    public ValidationPage submit() {
        clickElement(By.xpath(SUBMIT_XPATH));
        userService.login(registeredData.getEmail(), registeredData.toString());
        return new ValidationPage(driver);
    }


    public String verifyRegisterErrorMessage() { 
        String ERROR_XPATH = "//p[contains(text(), 'Email Address already exist!')]";
        WebElement errorMessage = driver.findElement(By.xpath(ERROR_XPATH));
        String message = errorMessage.getText();
        log.info("Message: {}", message);
        return message;
    }

    private void clickElement(By locator) {
        waitForElement(locator).click();
    }

    private void typeText(By locator, String text) {
        if (text != null) {
            WebElement element = waitForElement(locator);
            element.clear();
            element.sendKeys(text);
        }
    }

    private void selectDropdown(By locator, String value) {
        if (value != null && !value.isEmpty()) {
            new Select(waitForElement(locator)).selectByValue(value);
        }
    }

    private void clickIfTrue(By locator, boolean shouldClick) {
        if (shouldClick) {
            clickElement(locator);
        }
    }

    private WebElement waitForElement(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }


    public RegisterPage fillMandatoryRegisterForm(Register form) {
        typeText(By.xpath(NAME_XPATH), form.getName());
        typeText(By.xpath(PASSWORD_XPATH), form.getPassword());
        typeText(By.xpath(FIRST_NAME_XPATH), form.getFirstname());
        typeText(By.xpath(LAST_NAME_XPATH), form.getLastname());
        typeText(By.xpath(ADDRESS1_XPATH), form.getAddress());
        selectDropdown(By.xpath(COUNTRY_XPATH), form.getCountry());
        typeText(By.xpath(STATE_XPATH), form.getState());
        typeText(By.xpath(CITY_XPATH), form.getCity());
        typeText(By.xpath(ZIPCODE_XPATH), form.getZipcode());
        typeText(By.xpath(MOBILE_NUMBER_XPATH), form.getPhone());
        registeredData = mapRegisterToUserInfo(form);
        return this;
    }


}