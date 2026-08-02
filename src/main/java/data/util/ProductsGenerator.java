package data.util;

import data.DTO.ProductInfo;
import data.exceptions.ExceptionHandler;
import data.exceptions.InvalidDataException;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cache.services.CartServiceImpl;

import java.io.IOException;
import java.util.*;

import static data.variables.ProductVariables.*;

public class ProductsGenerator {
    private static final Logger log = LoggerFactory.getLogger(ProductsGenerator.class);


    public static void loadProducts() {
        ALL_PRODUCTS.clear();
        RECOMMENDED_PRODUCTS.clear();
        log.info("Loading products.....");
        try {
            String url = "https://automationexercise.com";
            Document doc = Jsoup.connect(url).get();

            loadAllProducts(doc);
            loadRecommendedProducts(doc);

            log.info("Downloaded {} all products and {} recommended products.",
                    ALL_PRODUCTS.size(), RECOMMENDED_PRODUCTS.size());

        } catch (IOException e) {
            ExceptionHandler.handleSilently(e, "loading product data from website");
        }
    }
    public static Set<ProductInfo> generateProducts(int count) {
        return selectProducts(count, ALL_PRODUCTS);
    }

    public static Set<ProductInfo> generateRecommendedProducts(int count) {
        return selectProducts(count, RECOMMENDED_PRODUCTS);
    }


    private static void loadAllProducts(Document doc) {
        Elements allProductBlocks = doc.select(".product-overlay .overlay-content");
        log.info("Loading all products.....");
        loadProducts(allProductBlocks, ALL_PRODUCTS);
    }

    private static void loadRecommendedProducts(Document doc) {
        Elements recommendedProductsBlocks = doc.select(".recommended_items .productinfo.text-center");
        log.info("Loading recommended products.....");
        loadProducts(recommendedProductsBlocks, RECOMMENDED_PRODUCTS);
    }

    private static void loadProducts(Elements productBlocks,Map<String,Integer> products) {
        for (Element product : productBlocks) {
            String priceText = Objects.requireNonNull(product.selectFirst("h2")).text();
            String name = Objects.requireNonNull(product.selectFirst("p")).text();
            int price = parsePrice(priceText);
            products.put(name, price);
        }
    }

    private static int parsePrice(String priceText) {
        return Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
    }

    private static Set<ProductInfo> selectProducts(int count, Map<String, Integer> products) {
        if (count > products.size()) {
            throw new InvalidDataException("Requested more items than available.");
        }

        List<Map.Entry<String, Integer>> productList = new ArrayList<>(products.entrySet());
        Set<ProductInfo> selectedProducts = new HashSet<>();
        Random random = new Random();
        Set<Integer> indexSet = new HashSet<>();

        while (indexSet.size() < count) {
            indexSet.add(random.nextInt(productList.size()));
        }

        for (Integer index : indexSet) {
            Map.Entry<String, Integer> entry = productList.get(index);
            String name = entry.getKey();
            int price = entry.getValue();
            int quantity = random.nextInt(5) + 1;

            selectedProducts.add(ProductInfo.builder()
                    .name(name)
                    .price(price)
                    .quantity(quantity)
                    .build());
        }
        return selectedProducts;
    }

}
