package selenium.tests.login;

import data.DTO.Login;
import core.base.BaseUITest;
import core.base.DRIVERS;
import selenium.components.Header;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import selenium.pages.HomePage;
import data.provider.AuthProvider;

public class TC02_Login_ValidTest extends BaseUITest {

    public SoftAssert softAssert;

    public TC02_Login_ValidTest() {
        super(DRIVERS.CHROME);
        softAssert = new SoftAssert();
    }

    @Test(dataProvider = "loginValidCredentials",dataProviderClass = AuthProvider.class)
    public void loginValidCredentials(Login form) {
        HomePage homePage = new HomePage(driver);
        Header header=homePage.navigateTo().LoginPage().login(Login.builder().email(form.getEmail()).password(form.getPassword()).build()).correctLogin();
        softAssert.assertNotNull(header.getLoggedInUsername());
    }

}
