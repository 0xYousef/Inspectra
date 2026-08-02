package selenium.pages;

import core.utils.Configuration;
import selenium.components.Footer;
import selenium.components.Header;
import selenium.components.PageNavigator;
import selenium.components.Scrolling;
import io.qameta.allure.Step;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Duration;


public class BasePage  {

    protected WebDriver driver;
    protected WebDriverWait wait;


    // Authenticated navigation
    public static final Logger log = LoggerFactory.getLogger(BasePage.class);

    public BasePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        PageFactory.initElements(driver, this);
    }

    @Step("Open Website")
    public void open(){
        String baseUrl = Configuration.get("ui.base.url");
        driver.get(baseUrl);
        log.info("Browser: {} baseUrl: {}", driver.getTitle(),baseUrl);
    }
    public PageNavigator navigateTo(){return new PageNavigator(driver);}
    public Header Header() {
        return new Header();
    }

    public Scrolling scroll(){  return new Scrolling(driver);  }

    public Footer Footer() {  return new Footer(driver);  }


}