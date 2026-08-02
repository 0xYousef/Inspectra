package data.mapper;

import data.DTO.ProductInfo;
import org.bson.Document;

import java.util.Map;

public class ProductsMapper {

    public static Document toDocument(ProductInfo product) {
        return new Document()
                .append("id", product.getId())
                .append("name", product.getName())
                .append("price", product.getPrice())
                .append("brand", product.getBrand())
                .append("category",
                        new Document()
                                .append("usertype",
                                        new Document("usertype", product.getUserType()))
                                .append("category", product.getCategory()));
    }

    public static ProductInfo fromDocument(Document doc) {
        Document category = doc.get("category", Document.class);
        Document userType = category.get("usertype", Document.class);

        return ProductInfo.builder()
                .id(doc.getInteger("id"))
                .name(doc.getString("name"))
                .price(Integer.parseInt(doc.getString("price")))
                .brand(doc.getString("brand"))
                .userType(userType.getString("usertype"))
                .category(category.getString("category"))
                .build();
    }

    @SuppressWarnings("unchecked")
    public static Document toDocument(Map<String, Object> product) {

        Map<String, Object> category =
                (Map<String, Object>) product.get("category");

        Map<String, Object> userType =
                (Map<String, Object>) category.get("usertype");

        return new Document()
                .append("id", ((Number) product.get("id")).intValue())
                .append("name", product.get("name"))
                .append("price", product.get("price"))
                .append("brand", product.get("brand"))
                .append("userType", userType.get("usertype"))
                .append("category", category.get("category"));
    }
}