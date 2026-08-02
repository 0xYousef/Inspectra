package core;
import cache.context.UserContext;
import cache.models.ProductCache;
import cache.models.UserInfo;
import cache.repository.CacheRepository;
import cache.services.CartServiceImpl;
import cache.services.UserProfileServiceImpl;


import java.util.Set;



public class Main {

    public static void main(String[] args) {
        CartServiceImpl cartService = new CartServiceImpl();
        UserProfileServiceImpl userService = new UserProfileServiceImpl();

        ProductCache laptop = ProductCache.builder()
                .productName("Laptop")
                .price(1200)
                .quantity(1)
                .build();

        ProductCache mouse = ProductCache.builder()
                .productName("Mouse")
                .price(50)
                .quantity(2)
                .build();

        ProductCache keyboard = ProductCache.builder()
                .productName("Keyboard")
                .price(100)
                .quantity(1)
                .build();

        System.out.println("=== Scenario 1: Anonymous Shopping ===");
        cartService.addToCart(laptop);
        cartService.addToCart(mouse);

        printCartContents("Anonymous Cart", cartService.viewCartItems());
        System.out.println("Total items: " + cartService.countTotalItems());
        System.out.println("Cart total: $" + cartService.calculateCartTotal());

        System.out.println("\n=== Scenario 2: User Login (Merge Cart) ===");
        UserInfo.builder()
                .email("user1@example.com")
                .firstname("John")
                .lastname("Doe")
                .build();
        String user1_email = "yousef.mohamed.1206@proton.me";
        userService.login(user1_email, "user1");

        cartService.addToCart(keyboard);
        ProductCache laptopExtra = laptop.toBuilder().quantity(1).build();
        cartService.addToCart(laptopExtra);

        printCartContents("User1 Cart After Login", cartService.viewCartItems());
        System.out.println("Total items: " + cartService.countTotalItems());
        System.out.println("Cart total: $" + cartService.calculateCartTotal());

        System.out.println("\n=== Scenario 3: User Switch ===");
        userService.logout();

        ProductCache headphones = ProductCache.builder()
                .productName("Headphones")
                .price(150)
                .quantity(1)
                .build();
        cartService.addToCart(headphones);

        printCartContents("Anonymous Cart Again", cartService.viewCartItems());

        UserInfo.builder()
                .email("user2@example.com")
                .firstname("Jane")
                .lastname("Smith")
                .build();

        String user2_email = "user2@example.com";
        userService.login(user2_email, "user2");

        printCartContents("User2 Cart After Login", cartService.viewCartItems());

        System.out.println("\n=== Scenario 4: User1 Returns ===");
        userService.logout();
        userService.login(user1_email, "user1");

        printCartContents("User1 Cart After Return", cartService.viewCartItems());
        CacheRepository.view_all_sessions().forEach(System.out::println);

        System.out.println("\n=== Scenario 5: Complete Order ===");
        cartService.completeOrder();

        System.out.println("User1 cart after order completion:");
        printCartContents("User1 Cart", cartService.viewCartItems());
        System.out.println("Completed orders: " + CacheRepository.getCompletedOrders());

        System.out.println("\n=== Scenario 6: New Cart After Order ===");
        cartService.addToCart(mouse);
        printCartContents("User1 New Cart", cartService.viewCartItems());

        userService.logout();

    }

    private static void printCartContents(String title, Set<ProductCache> cartItems) {
        System.out.println("\n" + title + ":");
        if (cartItems.isEmpty()) {
            System.out.println("(Empty cart)");
        } else {
            cartItems.forEach(item -> System.out.printf(
                    "- %s: %d x $%d = $%d\n",
                    item.getProductName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getQuantity() * item.getPrice()
            ));
        }


        System.out.println("Current user: " + UserContext.current_user());
    }
}
