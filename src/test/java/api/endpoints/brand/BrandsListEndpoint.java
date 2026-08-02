package api.endpoints.brand;

import api.endpoints.base.Endpoint;
import api.endpoints.base.HttpMethod;
import io.restassured.response.Response;

public class BrandsListEndpoint extends Endpoint {

    private static final String PATH = "/brandsList";
    private static final String BRANDS_SCHEMA = "schemas/brands-schema.json";
    private static final String RESPONSE_SCHEMA = "schemas/login-response-schema.json";

    public Response getAll() {
        return execute(newRequest(), HttpMethod.GET, PATH, BRANDS_SCHEMA, "Brands Response Schema");
    }

    public Response putNotAllowed() {
        return execute(newRequest(), HttpMethod.PUT, PATH, RESPONSE_SCHEMA, "Response Schema");
    }
}
