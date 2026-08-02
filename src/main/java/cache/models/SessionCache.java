package cache.models;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder(toBuilder = true)
public class SessionCache {
    private String email;
    private String fullName;
    private String accountInfo;
    private Set<ProductCache> cartItems;
    private String createdAt;
    private String lastUpdated;
}
