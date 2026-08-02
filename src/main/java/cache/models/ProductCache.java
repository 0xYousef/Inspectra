package cache.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ProductCache {
    private String productName;
    private int quantity;
    private int price;

    public int totalPrice() {
        return price * quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCache that)) return false;
        return java.util.Objects.equals(productName, that.productName);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(productName);
    }
}
