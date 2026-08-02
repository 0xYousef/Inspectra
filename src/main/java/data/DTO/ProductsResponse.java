package data.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ProductsResponse {
    private int responseCode;
    private List<ProductInfo> products;
}