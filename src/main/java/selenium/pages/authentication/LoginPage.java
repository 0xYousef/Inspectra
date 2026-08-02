package selenium.pages.authentication;

import data.DTO.Login;
import data.DTO.Register;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.BasePage;

import selenium.validators.ValidLoginPage;


public class LoginPage extends BasePage {


    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public ValidLoginPage login(Login form) {

        String EMAIL = form.getEmail();
        String PASSWORD = form.getPassword();

        String EMAIl_XPATH = "//input[@data-qa='login-email']";
        String PASSWORD_XPATH = "//input[@data-qa='login-password']";
        String LOGIN_BUTTON_XPATH = "//button[@data-qa='login-button']";

        WebElement emailField = driver.findElement(By.xpath(EMAIl_XPATH));
        WebElement passwordField = driver.findElement(By.xpath(PASSWORD_XPATH));
        WebElement loginButton = driver.findElement(By.xpath(LOGIN_BUTTON_XPATH));

        emailField.sendKeys(EMAIL);
        passwordField.sendKeys(PASSWORD);
        loginButton.click();
        log.info("login via credentials => EMAIL: {} password: {}", EMAIL,PASSWORD);


        return new ValidLoginPage(driver,EMAIL);
    }

    public LoginPage register(Register form) {
        String NAME_XPATH = "//input[@data-qa='signup-name']";
        String EMAIL_XPATH = "//input[@data-qa='signup-email']";

        WebElement nameField = driver.findElement(By.xpath(NAME_XPATH));
        WebElement emailField = driver.findElement(By.xpath(EMAIL_XPATH));

        nameField.sendKeys(form.getName());
        emailField.sendKeys(form.getEmail());

        log.info("register with name : {} email: {}", form.getName(), form.getEmail());
        return this;
    }
    public RegisterPage clickOnSignup() {
        String REGISTER_BUTTON_XPATH = "//button[@data-qa='signup-button']";
        WebElement registeredButton = driver.findElement(By.xpath(REGISTER_BUTTON_XPATH));
        registeredButton.click();
        return new RegisterPage(driver);
    }

    public boolean isLoginVisible(){
        String LOGIN_FORM_XPATH = "//div[contains(@class,'login-form')]/h2";
        WebElement loginForm = driver.findElement(By.xpath(LOGIN_FORM_XPATH));
        return loginForm.isDisplayed();
    }

    public boolean isSignupVisible() {
        String SIGNUP_XPATH = "//div[contains(@class,'signup-form')]/h2";
        WebElement signupVerify = driver.findElement(By.xpath(SIGNUP_XPATH));
        boolean visible = signupVerify.isDisplayed();
        log.info("verify signup visibility");
        return visible;
    }

}
