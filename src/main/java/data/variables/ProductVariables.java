package data.variables;

import java.util.HashMap;
import java.util.Map;

public class ProductVariables {
    public static final Map<String, Integer> SEARCH_MAP = Map.of(
            "top", 14,
            "dress", 9,
            "jeans", 3,
            "tshirt", 6
    );

    public static Map<String,Integer> ALL_PRODUCTS = new HashMap<>();
    public static Map<String,Integer> RECOMMENDED_PRODUCTS = new HashMap<>();
}
