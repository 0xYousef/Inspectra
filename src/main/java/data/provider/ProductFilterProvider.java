package data.provider;

import data.mongo.ProductsRepository;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.Random;

public class ProductFilterProvider {
    private static final ProductsRepository repository = new ProductsRepository();
    private static final Random random = new Random();

    @DataProvider(name = "RandomBrand")
    public Object[][] randomBrand() {
        return new Object[][] { { pick(repository.getBrands()) } };
    }

    @DataProvider(name = "RandomCategory")
    public Object[][] randomCategory() {
        return new Object[][] {
                { pick(repository.getUserTypes()), pick(repository.getCategories()) }
        };
    }

    private static String pick(List<String> values) {
        return values.isEmpty() ? null : values.get(random.nextInt(values.size()));
    }
}
