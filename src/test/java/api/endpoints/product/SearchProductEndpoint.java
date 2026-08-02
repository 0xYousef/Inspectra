package api.endpoints.product;

import api.endpoints.base.Endpoint;
import api.endpoints.base.HttpMethod;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class SearchProductEndpoint extends Endpoint {

    private static final String PATH = "/searchProduct";
    private static final String PRODUCTS_SCHEMA = "schemas/products-schema.json";
    private static final String RESPONSE_SCHEMA = "schemas/login-response-schema.json";

    public Response search(String searchProduct) {
        return execute(newRequest()
                        .contentType(ContentType.MULTIPART)
                        .multiPart("search_product", searchProduct),
                HttpMethod.POST, PATH, PRODUCTS_SCHEMA, "Response Schema");
    }

    public Response searchWithoutParam() {
        return execute(newRequest()
                        .accept(ContentType.JSON)
                        .contentType(ContentType.JSON),
                HttpMethod.POST, PATH, RESPONSE_SCHEMA, "Response Schema");
    }
}
