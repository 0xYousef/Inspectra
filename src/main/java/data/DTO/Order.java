package data.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Order {
    private String orderId;
    private String status;
}
