package selenium.tests.contactus;

import data.DTO.ContactUs;
import core.base.BaseUITest;
import data.expectations.Expectations;
import org.testng.annotations.Test;
import selenium.pages.HomePage;
import selenium.pages.connections.ContactUsPage;
import data.provider.ContactUsProvider;
import static core.base.DRIVERS.CHROME;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TC06_ContactUs_Test extends BaseUITest {

    public TC06_ContactUs_Test() {
        super(CHROME);
    }

    @Test(dataProvider = "SendValidInfo",dataProviderClass = ContactUsProvider.class)
    public void sendContactUsFormTest(ContactUs form){
        HomePage homePage = new HomePage(driver);

        String verify_homePage = homePage.verify();
        assertEquals(verify_homePage, Expectations.Ui.HomePage.WELCOME_MESSAGE);

        ContactUsPage contactUsPage = homePage.navigateTo().ContactUsPage();
        assertTrue(contactUsPage.verifyPageTitle());

        String actual = contactUsPage.fillContactUsForm(form).submit().verifySuccessMessage();
        assertEquals(actual,Expectations.Ui.ContactUs.MESSAGE);

        homePage = contactUsPage._gotoHome();
        assertEquals(homePage.verify(),Expectations.Ui.HomePage.WELCOME_MESSAGE);
    }

}
