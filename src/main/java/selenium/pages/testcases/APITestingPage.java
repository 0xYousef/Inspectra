package selenium.pages.testcases;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.pages.BasePage;

public class APITestingPage extends BasePage {

    @FindBy(tagName = "h2") private WebElement title;

    public APITestingPage(WebDriver driver) {
        super(driver);
    }

    @Step("verify API Testing page")
    public String getTitle() {
        String titleText = title.getText();
        log.info("Verify title API Testing page: {}", titleText);
        return titleText;
    }

}
