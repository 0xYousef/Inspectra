package cache.services;

import cache.context.UserContext;
import cache.models.ProductCache;
import cache.models.SessionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cache.repository.CacheRepository;
import cache.services.interfaces.CartService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CartServiceImpl implements CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    private final SessionServiceImpl sessionService = new SessionServiceImpl();

    public int countTotalItems() {
        return viewCartItems().stream().mapToInt(ProductCache::getQuantity).sum();
    }

    public int calculateCartTotal() {
        return viewCartItems().stream().mapToInt(p -> p.getQuantity() * p.getPrice()).sum();
    }

    @Override
    public void addToCart(ProductCache product) {
        SessionCache session = sessionService.currentSession();

        if (session == null) {
            session = sessionService.getAnonymousSession();
            if (session == null) {
                session = SessionCache.builder()
                        .email("anonymous")
                        .cartItems(new HashSet<>())
                        .build();
                sessionService.createAnonymousSession(session);
            }
        }

        if (session.getCartItems().contains(product)) {
            ProductCache existingItem = session.getCartItems().stream()
                    .filter(p -> p.equals(product))
                    .findFirst()
                    .get();
            existingItem.setQuantity(existingItem.getQuantity() + product.getQuantity());
        } else {
            session.getCartItems().add(product);
        }

        if (UserContext.current_user().equals("anonymous")) {
            sessionService.createAnonymousSession(session);
        } else {
            sessionService.updateSession(session.getEmail(), session);
        }

        logger.info("Added product {} to cart (user: {})",
                product.getProductName(), session.getEmail());
    }

    @Override
    public Set<ProductCache> viewCartItems() {
        SessionCache session = sessionService.currentSession();
        if (session == null) {
            session = sessionService.getAnonymousSession();
        }
        return Optional.ofNullable(session)
                .map(SessionCache::getCartItems)
                .orElse(new HashSet<>());
    }

    @Override
    public void removeFromCart(String productName) {
        SessionCache session = sessionService.currentSession();
        if (session == null) {
            session = sessionService.getAnonymousSession();
        }

        if (session != null && session.getCartItems() != null) {
            boolean removed = session.getCartItems().removeIf(p -> p.getProductName().equals(productName));

            if (removed) {
                if (UserContext.current_user().equals("anonymous")) {
                    sessionService.createAnonymousSession(session);
                } else {
                    sessionService.updateSession(session.getEmail(), session);
                }
                logger.info("Removed product {} from cart", productName);
            }
        }
    }

    @Override
    public void clearCart() {
        SessionCache session = sessionService.currentSession();
        if (session == null) {
            return;
        }

        if (session.getCartItems() != null && !session.getCartItems().isEmpty()) {
            session.getCartItems().clear();

            if (UserContext.current_user().equals("anonymous")) {
                sessionService.createAnonymousSession(session);
            } else {
                sessionService.updateSession(session.getEmail(), session);
            }
            logger.info("Cleared cart for user {}", session.getEmail());
        }
    }

    public void completeOrder() {
        String email = UserContext.current_user();
        if (UserContext.current_user().equals("anonymous")) {
            return;
        }
        CacheRepository.markOrderCompleted(email);
        logger.info("Order completed for user {}", email);
    }
}