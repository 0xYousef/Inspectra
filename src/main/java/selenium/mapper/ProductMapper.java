package selenium.mapper;

import data.DTO.ProductInfo;
import cache.models.ProductCache;

public class ProductMapper {
    public static ProductCache InfoToCache(ProductInfo productInfo) {
        return ProductCache.builder()
                .productName(productInfo.getName())
                .price(productInfo.getPrice())
                .quantity(productInfo.getQuantity())
                .build();
    }
}
