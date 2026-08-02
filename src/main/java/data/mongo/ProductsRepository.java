package data.mongo;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import core.db.COLLECTIONS;
import core.db.MongoDBClient;
import data.DTO.ProductInfo;
import data.exceptions.ExceptionHandler;
import data.mapper.ProductsMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductsRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductsRepository.class);
    private static final String COLLECTION = COLLECTIONS.PRODUCTS.toString();
    private static final AtomicBoolean INDEXED = new AtomicBoolean(false);

    private static MongoCollection<Document> collection() {
        MongoCollection<Document> collection =
                Objects.requireNonNull(MongoDBClient.getDatabase()).getCollection(COLLECTION);
        if (INDEXED.compareAndSet(false, true)) {
            // Create only once (MongoDB ignores it if it already exists)
            collection.createIndex(
                    new Document("id", 1),
                    new IndexOptions().unique(true)
            );
        }
        return collection;
    }

    public void insertProduct(ProductInfo product) {
        try {
            collection().insertOne(ProductsMapper.toDocument(
                    Map.of(
                            "id", product.getId(),
                            "name", product.getName(),
                            "price", product.getPrice(),
                            "brand", product.getBrand(),
                            "category", Map.of(
                                    "usertype", Map.of("usertype", product.getUserType()),
                                    "category", product.getCategory()
                            )
                    )
            ));
            log.info("Inserted product {}", product.getName());
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "inserting product " + product.getName());
        }
    }

    public void saveProducts(List<Map<String, Object>> products) {
        try {
            List<Document> documents = products.stream()
                    .map(ProductsMapper::toDocument)
                    .toList();

            collection().insertMany(documents, new InsertManyOptions().ordered(false));
            log.info("Inserted {} products", documents.size());
        } catch (MongoBulkWriteException e) {
            log.info("Some products already exist. Only new products were inserted.");
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "saving products");
        }
    }

    public List<String> getBrands() {
        try {
            List<String> brands = collection().distinct("brand", String.class)
                    .into(new ArrayList<>());
            log.info("Fetched {} brands from database", brands.size());
            return brands;
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "fetching brands");
            return List.of();
        }
    }

    public List<String> getUserTypes() {
        try {
            List<String> userTypes = collection().distinct("userType", String.class)
                    .into(new ArrayList<>());
            log.info("Fetched {} user types from database", userTypes.size());
            return userTypes;
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "fetching user types");
            return List.of();
        }
    }

    public List<String> getCategories() {
        try {
            List<String> categories = collection().distinct("category", String.class)
                    .into(new ArrayList<>());
            log.info("Fetched {} categories from database", categories.size());
            return categories;
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "fetching categories");
            return List.of();
        }
    }

    public ProductInfo findById(int id) {
        try {
            Document doc = collection().find(new Document("id", id)).first();

            if (doc == null) {
                return null;
            }

            return ProductInfo.builder()
                    .id(doc.getInteger("id"))
                    .name(doc.getString("name"))
                    .price(Integer.parseInt(doc.getString("price")))
                    .brand(doc.getString("brand"))
                    .userType(doc.getString("userType"))
                    .category(doc.getString("category"))
                    .build();
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "finding product " + id);
            return null;
        }
    }

    public void updateProduct(int id, ProductInfo product) {
        try {
            Document update = new Document("$set",
                    new Document()
                            .append("id", product.getId())
                            .append("name", product.getName())
                            .append("price", product.getPrice())
                            .append("brand", product.getBrand())
                            .append("userType", product.getUserType())
                            .append("category", product.getCategory()));

            collection().updateOne(new Document("id", id), update);

            log.info("Updated product {}", id);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "updating product " + id);
        }
    }

    public void deleteProduct(int id) {
        try {
            collection().deleteOne(new Document("id", id));
            log.info("Deleted product {}", id);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "deleting product " + id);
        }
    }
}
