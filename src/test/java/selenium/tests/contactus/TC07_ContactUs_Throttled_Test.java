package selenium.tests.contactus;

import core.base.BaseUITest;
import core.throttle.CpuProfile;
import core.throttle.NetworkProfile;
import core.throttle.ThrottleManager;
import data.DTO.ContactUs;
import data.expectations.Expectations;
import data.provider.ContactUsProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import selenium.pages.HomePage;
import selenium.pages.connections.ContactUsPage;

import static core.base.DRIVERS.CHROME;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Simple throttled variant of {@code TC06_ContactUs_Test}: the contact us flow
 * runs under a fixed CPU/network throttle, which is reset afterwards.
 */
public class TC07_ContactUs_Throttled_Test extends BaseUITest {

    public TC07_ContactUs_Throttled_Test() {
        super(CHROME);
    }

    @AfterMethod
    public void resetThrottling() {
        new ThrottleManager(driver).reset();
    }

    @Test(dataProvider = "SendValidInfo", dataProviderClass = ContactUsProvider.class)
    public void sendContactUsFormUnderThrottling(ContactUs form) {
        new ThrottleManager(driver).apply(
                CpuProfile.custom(50),
                NetworkProfile.custom(150_000, 100_000, 100));

        HomePage homePage = new HomePage(driver);
        String verify_homePage = homePage.verify();
        assertEquals(verify_homePage, Expectations.Ui.HomePage.WELCOME_MESSAGE);

        ContactUsPage contactUsPage = homePage.navigateTo().ContactUsPage();
        assertTrue(contactUsPage.verifyPageTitle());

        String actual = contactUsPage.fillContactUsForm(form).submit().verifySuccessMessage();
        assertEquals(actual, Expectations.Ui.ContactUs.MESSAGE);
    }
}
