package data.provider;

import data.DTO.ProductInfo;
import cache.models.ProductCache;
import org.testng.annotations.DataProvider;

import data.variables.ProductVariables;

import java.util.*;
import java.util.Map.Entry;

import static data.util.ProductsGenerator.generateProducts;
import static data.util.ProductsGenerator.generateRecommendedProducts;


public class ProductProvider {

    @DataProvider(name = "Products")
    public Object[][] getGeneratedProducts() {
        Set<ProductInfo> products = generateProducts(5);
        return new Object[][] { { products } };
    }

    @DataProvider(name = "needOneProduct")
    public Object[][] selectProduct() {
        Set<ProductInfo> product = generateProducts(1);
        return new Object[][] { { product } };
    }
    @DataProvider(name = "RecommendedProducts")
    public Object[][] getGenerateRecommendedProducts() {
        Set<ProductInfo> products = generateRecommendedProducts(5);
        return new Object[][] { { products } };
    }

    @DataProvider(name = "EmptyProducts")
    public Object[][] dataProvider() {
        Set<ProductCache> products = new HashSet<>();
        return new Object[][] { { products } };
    }

    @DataProvider(name = "SearchValues")
    public static Object[][] getRandomSearchValue() {
        List<Entry<String, Integer>> entries = new ArrayList<>(ProductVariables.SEARCH_MAP.entrySet());
        Entry<String, Integer> randomEntry = entries.get(new Random().nextInt(entries.size()));

        return new Object[][] {
                { randomEntry.getKey(), randomEntry.getValue() }
        };
    }
}
