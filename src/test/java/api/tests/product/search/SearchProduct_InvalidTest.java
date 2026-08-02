package api.tests.product.search;

import api.base.BaseAPIClient;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static data.expectations.Expectations.Http.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;
import static core.utils.AllureUtils.attachJsonSchema;

@Epic("PRODUCTS")
@Feature("SEARCH ABOUT PRODUCTS")
@Story("POST https://automationexercise.com/api/searchProduct")
public class SearchProduct_InvalidTest extends BaseAPIClient {
    @Description("Invalid search about products missing parameter search_product")
    @Severity(SeverityLevel.NORMAL)
    @Test(groups = {"API"})
    public void invalidSearchProduct() {
        attachJsonSchema("/schemas/login-response-schema.json","Response Schema");
        Response response = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .when()
                .post("/searchProduct")
                .then()
                .body(matchesJsonSchemaInClasspath("schemas/login-response-schema.json")).extract().response();
        JsonPath jsonPath = response.jsonPath();
        assertEquals(jsonPath.getInt("responseCode"), BAD_REQUEST);
        assertEquals(jsonPath.getString("message"), String.format(REQUIRED_REGISTER,"search_product"));

    }
}
