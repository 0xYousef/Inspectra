package api.tests.account.delete;


import data.DTO.Login;
import api.base.BaseAPIClient;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import data.provider.AuthProvider;
import core.utils.AllureUtils;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;

@Epic("ACCOUNT")
@Feature("DELETE OPERATION")
public class Delete_InvalidTest extends BaseAPIClient {
    @Story("https://automationexercise.com/api/verifyLogin")
    @Description("Invalid Delete an account with /verifyLogin")
    @Severity(SeverityLevel.CRITICAL)
    @Test( dataProvider = "deletedAccount",
            dataProviderClass = AuthProvider.class
            , groups = {"API"})
    public void LoginByInValidCredentials(Login form){

        AllureUtils.attachJsonSchema("schemas/login-response-schema.json", "Login Response Schema");
        Response response = given()
                .header("Content-Type", "application/json")
                .contentType(ContentType.MULTIPART)
                .multiPart("email", form.getEmail())
                .multiPart("password", form.getPassword())
                .when().delete("/verifyLogin")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/login-response-schema.json"))
                .statusCode(200)
                .extract()
                .response();
        assertEquals(response.getBody().jsonPath().getInt("responseCode"), form.getExpectation().getStatusCode());
        assertEquals(response.getBody().jsonPath().get("message"), form.getExpectation().getMessage());
    }
}
