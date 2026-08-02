package api.tests.brand;

import api.base.BaseAPIClient;
import api.endpoints.brand.BrandsListEndpoint;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static data.expectations.Expectations.Http.NOT_SUPPORTED;
import static data.expectations.Expectations.Http.NOT_SUPPORTED_MESSAGE;
import static org.testng.Assert.assertEquals;

@Epic("BRANDS")
@Feature("UPDATE BRANDS")
@Story("PUT https://automationexercise.com/api/brandsList")
public class UpdateBrands_ValidTest extends BaseAPIClient {

    @Description("update Brands not allowed")
    @Severity(SeverityLevel.MINOR)
    @Test(groups = {"API"})
    public void updateBrandsNotAllowed() {
        Response response = new BrandsListEndpoint().putNotAllowed();
        JsonPath jsonPath = response.jsonPath();
        assertEquals(jsonPath.getInt("responseCode"), NOT_SUPPORTED);
        assertEquals(jsonPath.getString("message"), NOT_SUPPORTED_MESSAGE);
    }
}
