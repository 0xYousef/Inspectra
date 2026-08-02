package data.provider;

import data.DTO.ReviewProduct;
import org.testng.annotations.DataProvider;
import static data.variables.ReviewProductVariables.*;

public class ReviewProductProvider {
    @DataProvider(name = "ProductReviewGenerator")
    public Object[][] getReviewProductData() {
        return new Object[][] {new ReviewProduct[]{ReviewProduct.builder().name(name).email(email)
                .message(message).build()}
        };
    }

}
