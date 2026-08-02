package data.mongo;

import com.mongodb.client.MongoCollection;
import core.db.COLLECTIONS;
import core.db.MongoDBClient;
import data.DTO.ExecutionRecord;
import data.exceptions.ExceptionHandler;
import data.mapper.ExecutionMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ExecutionRepository {
    private static final Logger log = LoggerFactory.getLogger(ExecutionRepository.class);

    public void insertExecutions(List<ExecutionRecord> records) {
        if (records == null || records.isEmpty()) {
            return;
        }

        try {
            MongoCollection<Document> collection = MongoDBClient.getDatabase()
                    .getCollection(COLLECTIONS.EXECUTIONS.toString());
            List<Document> documents = records.stream()
                    .map(ExecutionMapper::toDocument)
                    .toList();
            collection.insertMany(documents);
            log.info("Bulk inserted {} execution records into {} collection",
                    documents.size(), COLLECTIONS.EXECUTIONS);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "bulk inserting execution records");
        }
    }
}
