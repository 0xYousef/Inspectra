package api.tests.product.add;

import api.base.BaseAPIClient;
import api.endpoints.product.ProductsListEndpoint;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static data.expectations.Expectations.Http.NOT_SUPPORTED;
import static data.expectations.Expectations.Http.NOT_SUPPORTED_MESSAGE;
import static org.testng.Assert.assertEquals;

@Epic("PRODUCTS")
@Feature("ADD NEW PRODUCT")
@Story("POST https://automationexercise.com/api/productsList")
public class AddProductTest extends BaseAPIClient {
    @Description("Add a new product is not allowed")
    @Severity(SeverityLevel.MINOR)
    @Test(groups = {"API"})
    public void addNewProductNotAllowed() {
        Response response = new ProductsListEndpoint().postNotAllowed();
        JsonPath jsonPath = response.jsonPath();
        assertEquals(jsonPath.getInt("responseCode"), NOT_SUPPORTED);
        assertEquals(jsonPath.getString("message"), NOT_SUPPORTED_MESSAGE);
    }
}
