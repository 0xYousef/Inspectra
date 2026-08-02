package selenium.pages.testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.pages.BasePage;

public class TestCasePage extends BasePage {

    @FindBy(tagName = "h2") private WebElement Title;

    public TestCasePage(WebDriver driver) {
        super(driver);
    }
    public String getTitle() {
        log.info("Getting title of test case page");
        return Title.getText();
    }

    public TestCasePage clickOnTestCase(String testCaseName) {
        WebElement element = driver.findElement(By.linkText(testCaseName));
        element.click();
        log.info("Clicked on Test Case : {}", testCaseName);
        return this;
    }
}
