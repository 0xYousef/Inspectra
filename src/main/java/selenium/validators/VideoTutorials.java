package selenium.validators;

import selenium.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class VideoTutorials extends BasePage {

    @FindBy(css = "#page-header > yt-page-header-renderer > yt-page-header-view-model > div" +
    "> div.page-header-view-model-wiz__page-header-headline > div > yt-dynamic-text-view-model > h1")
    private WebElement title;

    public VideoTutorials(WebDriver driver) {
        super(driver);
    }

    public String VerifyPage() {
        return title.getText(); // Expected: AutomationExercise
    }
}
