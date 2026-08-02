package selenium.tests.login;

import data.DTO.Login;
import core.base.BaseUITest;
import core.base.DRIVERS;
import data.expectations.Expectations;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import selenium.pages.HomePage;
import data.provider.AuthProvider;

import static org.testng.Assert.assertEquals;

public class TC03_Login_InvalidTest  extends BaseUITest {
    public TC03_Login_InvalidTest(){
        super(DRIVERS.CHROME);

    }

    @Test(dataProvider = "loginInvalidIncorrectPassword",dataProviderClass = AuthProvider.class)
    public void loginInvalidIncorrectPassword(Login form) {
        HomePage homePage = new HomePage(driver);
        String expected_message = Expectations.Ui.Login.INCORRECT_CREDENTIALS;
        String actual_message = homePage.navigateTo().LoginPage().login(form).incorrectLogin();
        assertEquals(actual_message, expected_message);
    }

}
