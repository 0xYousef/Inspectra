package selenium.tests.train;


import data.DTO.ProductInfo;
import core.base.BaseUITest;
import core.base.DEVICES;
import core.base.DRIVERS;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import selenium.pages.HomePage;
import data.provider.ProductProvider;
import selenium.components.Filter;

import java.util.Map;
import java.util.Set;

import static data.util.ProductsGenerator.loadProducts;

@Epic("Current Test")
@Feature("Test Multiple Browsers")
public class TestCurrentDev extends BaseUITest {
    HomePage homePage;
    SoftAssert softAssert;
    Filter filter;

    public TestCurrentDev(DRIVERS driverType, DEVICES devicesType) {
        super(driverType,devicesType);
    }



    @BeforeTest
    public void setUp() {
        softAssert = new SoftAssert();
        loadProducts();
    }


    @Description("Navigate to APITesting Page")
    @Test(dataProvider = "RecommendedProducts", dataProviderClass = ProductProvider.class)
    public void testCurrentDev(Set<ProductInfo> products) throws InterruptedException {
        homePage = new HomePage(driver);
        homePage.getRecommendedProducts().selectProducts(products);
        homePage.navigateTo().CartPage();
        //        ReviewProduct review = ReviewProduct.builder().email("A@aa").name("Yousef").message("22222").build();
//        String actual =  homePage.order().viewProduct().setQuantity(3).writeYourReview(review).ensureReviewSuccess();
//        String expected = "Thank you for your review.";
//        softAssert.assertTrue(actual.equals(expected));
    }

}


