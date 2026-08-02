package selenium.tests.subscription;

import core.base.BaseUITest;
import selenium.components.Footer;
import data.expectations.Expectations;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import selenium.pages.HomePage;
import selenium.pages.cart.CartPage;
import data.provider.ProductProvider;
import data.provider.RegisterProvider;

import java.util.Map;

import static core.base.DRIVERS.CHROME;
import static org.testng.Assert.assertEquals;

public class TC11_Subscription_Ensure_In_Cart_Page extends BaseUITest {
    private final HomePage homePage;
    private CartPage cartPage;

    public TC11_Subscription_Ensure_In_Cart_Page(){
        super(CHROME);
        homePage = new HomePage(driver);

    }

    @Test(priority = 1)
    public void ensureHPageIsVisible(){
        String actual = homePage.verify();
        assertEquals(actual, Expectations.Ui.HomePage.WELCOME_MESSAGE);
        ;
    }


    @Test(priority = 2, dataProvider = "generatedProducts", dataProviderClass = ProductProvider.class)
    public void scrollDownInHomePage(Map<String, Integer> products){
        homePage.scroll().downWithArrow();
        homePage.order().addToCart(products);
        CartPage cartPage = homePage.navigateTo().CartPage();
        cartPage.scroll().downWithArrow();

    }


    @Test(priority = 3)
    public void ensureSubscribeInHomepage(){
        String email = "user@example.com";
        Footer footer = cartPage.Footer();
        assertEquals(footer.verifyTextSUBSCRIPTION(), Expectations.Ui.Footer.TITLE);
        assertEquals(footer.enterEmailToSubscribe(email).verifyEmailSubscribed(),Expectations.Ui.Footer.SUCCESS_MESSAGE);
    }
}
