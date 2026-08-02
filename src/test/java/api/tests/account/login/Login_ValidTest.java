package api.tests.account.login;


import data.DTO.Login;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import data.provider.AuthProvider;
import api.base.BaseAPIClient;
import jdk.jfr.Description;
import org.testng.annotations.Test;
import core.utils.AllureUtils;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;


@Epic("ACCOUNT")
@Feature("LOGIN")
@Story("POST https://automationexercise.com/api/verifyLogin")
public class Login_ValidTest extends BaseAPIClient {



    @Description("Login By valid Credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Login By valid Credentials", dataProvider = "loginValidCredentials",
            dataProviderClass = AuthProvider.class
            , groups = {"API"})
    public void LoginByValidCredentials(Login form){

        AllureUtils.attachJsonSchema("schemas/login-response-schema.json", "Login Response Schema");
        Response response = given()
                .contentType(ContentType.MULTIPART)
                .multiPart("email", form.getEmail())
                .multiPart("password", form.getPassword())
                .when()
                .post("/verifyLogin")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/login-response-schema.json"))
                .statusCode(200)
                .extract()
                .response();
        var response_payload = response.getBody().jsonPath();
        assertEquals(response_payload.getInt("responseCode"), form.getExpectation().getStatusCode());
        assertEquals(response_payload.get("message"), form.getExpectation().getMessage());
    }
}
