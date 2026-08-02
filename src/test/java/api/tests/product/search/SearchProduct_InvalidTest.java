package api.tests.product.search;

import api.base.BaseAPIClient;
import api.endpoints.product.SearchProductEndpoint;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static data.expectations.Expectations.Http.*;
import static org.testng.Assert.assertEquals;

@Epic("PRODUCTS")
@Feature("SEARCH ABOUT PRODUCTS")
@Story("POST https://automationexercise.com/api/searchProduct")
public class SearchProduct_InvalidTest extends BaseAPIClient {
    @Description("Invalid search about products missing parameter search_product")
    @Severity(SeverityLevel.NORMAL)
    @Test(groups = {"API"})
    public void invalidSearchProduct() {
        Response response = new SearchProductEndpoint().searchWithoutParam();
        JsonPath jsonPath = response.jsonPath();
        assertEquals(jsonPath.getInt("responseCode"), BAD_REQUEST);
        assertEquals(jsonPath.getString("message"), String.format(REQUIRED_REGISTER,"search_product"));
    }
}
