package api.endpoints.account;

import api.endpoints.base.Endpoint;
import api.endpoints.base.HttpMethod;
import data.DTO.Login;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class DeleteAccountEndpoint extends Endpoint {

    private static final String PATH = "/deleteAccount";
    private static final String SCHEMA = "schemas/login-response-schema.json";

    public Response delete(Login form) {
        return execute(newRequest()
                        .contentType(ContentType.MULTIPART)
                        .multiPart("email", form.getEmail())
                        .multiPart("password", form.getPassword()),
                HttpMethod.DELETE, PATH, SCHEMA, "Delete Account Response Schema");
    }
}
