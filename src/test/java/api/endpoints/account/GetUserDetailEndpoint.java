package api.endpoints.account;

import api.endpoints.base.Endpoint;
import api.endpoints.base.HttpMethod;
import data.DTO.Login;
import io.restassured.response.Response;

public class GetUserDetailEndpoint extends Endpoint {

    private static final String PATH = "/getUserDetailByEmail";
    private static final String SCHEMA = "schemas/account-details-scheme.json";

    public Response getUserDetail(Login form) {
        return execute(newRequest()
                        .param("email", form.getEmail())
                        .param("password", form.getPassword()),
                HttpMethod.GET, PATH, SCHEMA, "User Details Response Schema");
    }
}
