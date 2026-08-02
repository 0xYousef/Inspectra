package data.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ProductInfo {
    private int id;
    private String brand;
    private String userType;
    private String category;
    private String name;
    private int price;
    private int quantity;
}
