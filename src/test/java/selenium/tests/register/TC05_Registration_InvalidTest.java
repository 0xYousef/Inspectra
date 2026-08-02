package selenium.tests.register;

import data.DTO.Register;
import core.base.BaseUITest;
import core.base.DRIVERS;
import data.expectations.Expectations;
import org.testng.annotations.Test;
import selenium.pages.HomePage;
import selenium.pages.authentication.LoginPage;
import data.provider.RegisterProvider;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TC05_Registration_InvalidTest extends BaseUITest {
    public TC05_Registration_InvalidTest() {
        super(DRIVERS.CHROME);
    }
    @Test(dataProvider = "RegisterInvalidExistEmail",dataProviderClass = RegisterProvider.class)
    public void registerUI_invalidTest(Register form) {
        HomePage homePage = new HomePage(driver);

        LoginPage loginPage = homePage.navigateTo().LoginPage();
        assertTrue(loginPage.isSignupVisible());

        String actual = loginPage.register(form).clickOnSignup().verifyRegisterErrorMessage();
        String expected = Expectations.Ui.Register.EMAIL_EXISTS_MESSAGE;
        assertEquals(actual,expected);
    }
}
