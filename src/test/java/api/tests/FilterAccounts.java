package api.tests;

import data.DTO.Login;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import data.mongo.AuthRepository;
import data.mongo.RegisterRepository;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static data.expectations.Expectations.Http.OK;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


public class FilterAccounts {
    private AuthRepository repository;
    private RegisterRepository registerRepository;
    @BeforeTest
    public void setup() {
        repository = new AuthRepository();
        registerRepository = new RegisterRepository();
    }
    @Test
    public void filterAccounts() {
        List<Login> accounts = repository.getAllUsers();
        boolean found ;
        for (Login login : accounts) {
            found = isCorrect(login);
            if (!found) {
                registerRepository.deleteUser(login.getEmail());
            }
        }

    }

    private boolean isCorrect(Login login) {
        Response response = given()
                .contentType(ContentType.MULTIPART)
                .multiPart("email", login.getEmail())
                .multiPart("password", login.getPassword())
                .when().post("https://automationexercise.com/api/verifyLogin")
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/login-response-schema.json"))
                .statusCode(200)
                .extract()
                .response();
        JsonPath res = response.jsonPath();
        return res.getInt("responseCode")== OK;
    }

}
