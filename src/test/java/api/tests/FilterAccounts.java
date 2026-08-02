package api.tests;

import api.endpoints.account.VerifyLoginEndpoint;
import data.DTO.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import data.mongo.AuthRepository;
import data.mongo.RegisterRepository;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static data.expectations.Expectations.Http.OK;


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
        Response response = new VerifyLoginEndpoint().login(login);
        JsonPath res = response.jsonPath();
        return res.getInt("responseCode")== OK;
    }

}
