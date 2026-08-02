package api.endpoints.product;

import api.endpoints.base.Endpoint;
import api.endpoints.base.HttpMethod;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ProductsListEndpoint extends Endpoint {

    private static final String PATH = "/productsList";
    private static final String PRODUCTS_SCHEMA = "schemas/products-schema.json";
    private static final String RESPONSE_SCHEMA = "schemas/login-response-schema.json";

    public Response getAll() {
        return execute(newRequest(), HttpMethod.GET, PATH, PRODUCTS_SCHEMA, "Products Response Schema");
    }

    public Response postNotAllowed() {
        return execute(newRequest()
                        .accept(ContentType.JSON)
                        .contentType(ContentType.JSON),
                HttpMethod.POST, PATH, RESPONSE_SCHEMA, "Response Schema");
    }
}
