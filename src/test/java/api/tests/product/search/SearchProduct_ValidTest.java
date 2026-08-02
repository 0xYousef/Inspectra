package api.tests.product.search;

import api.base.BaseAPIClient;
import api.endpoints.product.SearchProductEndpoint;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import data.provider.ProductProvider;

import static data.expectations.Expectations.Http.OK;
import static org.testng.Assert.assertEquals;

@Epic("PRODUCTS")
@Feature("SEARCH ABOUT PRODUCTS")
@Story("POST https://automationexercise.com/api/searchProduct")
public class SearchProduct_ValidTest extends BaseAPIClient {
    @Description("valid search about products using parameter search_product")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "SearchValues", dataProviderClass = ProductProvider.class,groups = {"API"})
    public void validSearchProduct(String search_product,int amount) {
        Response response = new SearchProductEndpoint().search(search_product);
        JsonPath jsonPath = response.jsonPath();
        assertEquals(jsonPath.getInt("responseCode"), OK);
        assertEquals(jsonPath.getList("products").size(),amount);
    }
}
