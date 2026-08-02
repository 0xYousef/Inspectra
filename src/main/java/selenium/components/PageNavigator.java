package selenium.components;

import org.openqa.selenium.support.FindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import selenium.pages.HomePage;
import selenium.pages.authentication.LoginPage;
import selenium.pages.cart.CartPage;
import selenium.pages.connections.ContactUsPage;
import selenium.pages.products.ProductsPage;
import selenium.pages.testcases.APITestingPage;
import selenium.pages.testcases.TestCasePage;
import cache.services.UserProfileServiceImpl;
import selenium.validators.ValidationPage;
import selenium.validators.VideoTutorials;


public class PageNavigator {
    private final WebDriver driver;
    private final UserProfileServiceImpl userService = new UserProfileServiceImpl();

    @FindBy(xpath = "//a[contains(@href,'/') and i[contains(@class,'fa-home')]]")
    protected WebElement homeLink;

    @FindBy(xpath = "//a[contains(@href,'/products')]")
    private WebElement productsLink;

    @FindBy(xpath = "//a[contains(@href,'/view_cart')]")
    private WebElement cartLink;

    @FindBy(xpath = "//a[i[@class='fa fa-list'] and contains(@href,'/test_cases')]")
    private WebElement testCasesLink;

    @FindBy(xpath = "//a[i[@class='fa fa-list'] and contains(@href,'/api_list')]")
    private WebElement apiTestingLink;

    @FindBy(xpath = "//a[i[@class='fa fa-youtube-play']]")
    private WebElement videoTutorialLink;

    @FindBy(xpath = "//a[@href='/contact_us']")
    private WebElement contactUsLink;

    public static final Logger log = LoggerFactory.getLogger(PageNavigator.class);

    public PageNavigator(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @Step("Go to Home page")
    public HomePage HomePage() {
        homeLink.click();
        log.info("Navigated to home page");
        return new HomePage(driver);
    }

    @Step("Go To Products page")
    public ProductsPage ProductsPage() {
        productsLink.click();
        log.info("Navigated to products page");
        return new ProductsPage(driver);
    }

    @Step("Go To Cart Page")
    public CartPage CartPage() {
        cartLink.click();
        log.info("Navigated to Cart page");
        return new CartPage(driver);
    }

    @Step("Go to Signup/Login Page")
    public LoginPage LoginPage() {
        WebElement signup_login = driver.findElement(By.linkText("Signup / Login"));
        signup_login.click();
        log.info("Navigated to Login page");
        return new LoginPage(driver);
    }

    @Step("Go To Contact Us Page")
    public ContactUsPage ContactUsPage() {
        contactUsLink.click();
        log.info("Navigated to contact us page");
        return new ContactUsPage(driver);
    }

    @Step("Go to Test Cases Page")
    public TestCasePage TestCasesPage() {
        testCasesLink.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/test_cases"));
        log.info("Navigated to test cases page");
        return new TestCasePage(driver);
    }

    @Step("Go to API Testing page")
    public APITestingPage APITestingPage() {
        apiTestingLink.click();
        log.info("Navigated to API Testing page");
        return new APITestingPage(driver);
    }

    @Step("Go to Video Tutorial page")
    public VideoTutorials VideoTutorialPage() {
        videoTutorialLink.click();
        log.info("Navigated to Video Tutorial page");
        return new VideoTutorials(driver);
    }

    @Step("Delete an account")
    public ValidationPage DeleteAccountPage() {
        WebElement deleteAccountLink = driver.findElement(By.linkText("Delete Account"));
        deleteAccountLink.click();
        log.info("Account deleted");
        return new ValidationPage(driver);
    }
    @Step("Logout")
    public void Logout() {
        WebElement logoutLink = driver.findElement(By.linkText("Logout"));
        logoutLink.click();
        log.info("Logout");
        userService.logout();
    }
}
