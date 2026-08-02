package data.mongo;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import core.db.COLLECTIONS;
import core.db.MongoDBClient;
import data.DTO.ProductInfo;
import data.mapper.ProductsMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ProductsRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductsRepository.class);
    private static final String COLLECTION = COLLECTIONS.PRODUCTS.toString();
    private static final MongoCollection<Document> collection =
            Objects.requireNonNull(MongoDBClient.getDatabase()).getCollection(COLLECTION);

    static {
        // Create only once (MongoDB ignores it if it already exists)
        collection.createIndex(
                new Document("id", 1),
                new IndexOptions().unique(true)
        );
    }

    public void insertProduct(ProductInfo product) {
        collection.insertOne(ProductsMapper.toDocument(
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
    }

    public void saveProducts(List<Map<String, Object>> products) {

        List<Document> documents = products.stream()
                .map(ProductsMapper::toDocument)
                .toList();

        try {
            collection.insertMany(documents, new InsertManyOptions().ordered(false));
            log.info("Inserted {} products", documents.size());
        } catch (MongoBulkWriteException e) {
            log.info("Some products already exist. Only new products were inserted.");
        }
    }

    public ProductInfo findById(int id) {
        Document doc = collection.find(new Document("id", id)).first();

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
    }

    public void updateProduct(int id, ProductInfo product) {

        Document update = new Document("$set",
                new Document()
                        .append("id", product.getId())
                        .append("name", product.getName())
                        .append("price", product.getPrice())
                        .append("brand", product.getBrand())
                        .append("userType", product.getUserType())
                        .append("category", product.getCategory()));

        collection.updateOne(new Document("id", id), update);

        log.info("Updated product {}", id);
    }

    public void deleteProduct(int id) {
        collection.deleteOne(new Document("id", id));
        log.info("Deleted product {}", id);
    }
}