package cache.services.interfaces;

import cache.models.ProductCache;

import java.util.Set;

public interface CartService {
    void addToCart(ProductCache product);
    Set<ProductCache> viewCartItems();
    void removeFromCart(String productName);
    void clearCart();
}
