package selenium.tests.subscription;

import core.base.BaseUITest;
import selenium.components.Footer;
import data.expectations.Expectations;
import org.testng.annotations.Test;
import selenium.pages.HomePage;

import static core.base.DRIVERS.EDGE;
import static org.testng.Assert.assertEquals;

public class TC10_Subscription_Ensure_In_Home_Page extends BaseUITest {
    private HomePage homePage;
    public TC10_Subscription_Ensure_In_Home_Page() {
        super(EDGE);
    }

    @Test(priority = 1)
    public void ensureHomePageIsVisible(){
        String actual = homePage.verify();
        assertEquals(actual, Expectations.Ui.HomePage.WELCOME_MESSAGE);
    }


    @Test(priority = 2)
    public void scrollDownInHomePage(){
        homePage.scroll().downWithArrow();
    }


    @Test(priority = 3)
    public void ensureSubscribeInHomepage(){
        String email = "user@example.com";
        homePage.scroll().upWithMouse();
        Footer footer = homePage.Footer();
        assertEquals(footer.verifyTextSUBSCRIPTION(), Expectations.Ui.Footer.TITLE);
        assertEquals(footer.enterEmailToSubscribe(email).verifyEmailSubscribed(),Expectations.Ui.Footer.SUCCESS_MESSAGE);
    }
}
