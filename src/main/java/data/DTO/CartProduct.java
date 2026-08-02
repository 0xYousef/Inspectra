package data.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CartProduct {
    private String productName;
    private int quantity;
    private int price;
    private int totalPrice;
}
