package api.tests.product.search;

import api.base.BaseAPIClient;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import data.provider.ProductProvider;

import static data.expectations.Expectations.Http.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;
import static core.utils.AllureUtils.attachJsonSchema;

@Epic("PRODUCTS")
@Feature("SEARCH ABOUT PRODUCTS")
@Story("POST https://automationexercise.com/api/searchProduct")
public class SearchProduct_ValidTest extends BaseAPIClient {
    @Description("valid search about products using parameter search_product")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "SearchValues", dataProviderClass = ProductProvider.class,groups = {"API"})
    public void validSearchProduct(String search_product,int amount) {
        attachJsonSchema("/schemas/products-schema.json","Response Schema");
        Response response = given()
                .contentType(ContentType.MULTIPART)
                .multiPart("search_product", search_product)
                .when()
                .post("/searchProduct")
                .then()
                .body(matchesJsonSchemaInClasspath("schemas/products-schema.json")).extract().response();
        JsonPath jsonPath = response.jsonPath();
        assertEquals(jsonPath.getInt("responseCode"), OK);
        assertEquals(jsonPath.getList("products").size(),amount);
    }
}
