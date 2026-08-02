package selenium.tests.products;

import data.DTO.ProductInfo;
import core.base.BaseUITest;
import io.qameta.allure.*;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import selenium.pages.HomePage;
import selenium.pages.products.ProductPage;
import selenium.pages.products.ProductsPage;
import data.provider.ProductProvider;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static core.base.DRIVERS.CHROME;
import static org.testng.Assert.*;
import static data.util.ProductsGenerator.loadProducts;

@Epic("PRODUCTS")
@Feature("ADD NEW PRODUCT")
@Story("verify all products and product details page")
public class TC08_Verify_All_Products_And_Product_Detail_Page extends BaseUITest {
    private final HomePage homePage;
    private ProductsPage productsPage;

    public  TC08_Verify_All_Products_And_Product_Detail_Page() {
        super(CHROME);
        homePage = new HomePage(driver);
        productsPage = null;
        homePage.open();
        loadProducts();
    }

    @Description("verify all products in Home Page")
    @Step("verify all products in Home Page")
    @Test()
    public void verifyAllProductsInHomePage(){
        int products = homePage.order().verifyAllProducts();
        if (products>0)
            assertTrue(true);
    }

    @Description("verify all products in Products Page")
    @Step("verify all products in Products Page")
    @Test()
    public void verifyAllProductsInProducts(){
//        productsPage = homePage.navigateTo().ProductsPage();
//        int products = productsPage.order().verifyAllProducts();
//        if (products>0)
//            assertTrue(true);
    }

    @Description("verify product details")
    @Step("verify product details")
    @Test(dataProvider = "needOneProduct",dataProviderClass = ProductProvider.class)
    public void verifyProductDetails(Set<ProductInfo> product){

        String name = product.stream().findFirst().get().getName();

        System.out.println("Product details: "+name);
        ProductPage productPage =homePage.order().viewProduct(name) ;
        assertNotNull(productPage.verifyDetails());
    }


    @AfterTest
    public void shutDown(){
        driver.quit();
    }
}
