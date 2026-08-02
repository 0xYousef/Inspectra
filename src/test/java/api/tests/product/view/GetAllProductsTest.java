package api.tests.product.view;

import api.base.BaseAPIClient;
import data.mongo.ProductsRepository;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import core.utils.AllureUtils;

import java.util.List;
import java.util.Map;

import static data.expectations.Expectations.Http.OK;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

@Epic("PRODUCTS")
@Feature("vIEW PRODUCTS")
@Story("GET https://automationexercise.com/api/productsList")
public class GetAllProductsTest extends BaseAPIClient {
    @Description("view all products")
    @Severity(SeverityLevel.MINOR)
    @Test(groups = {"API"})
    public void getAllProductsTest() {
        AllureUtils.attachJsonSchema("schemas/products-schema.json","Products Response Schema");

        Response response = given()
                .when()
                .get("/productsList")
                .then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/products-schema.json"))
                .extract()
                .response();
        JsonPath jsonPath = response.jsonPath();
        assertEquals(jsonPath.getInt("responseCode"), OK);
        List<Map<String, Object>> products = jsonPath.getList("products");

        ProductsRepository repository = new ProductsRepository();
        repository.saveProducts(products);

        assertEquals(products.size(), 34);
    }
}
