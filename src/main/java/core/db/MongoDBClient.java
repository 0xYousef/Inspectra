package core.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import core.utils.Configuration;
import data.exceptions.DatabaseException;
import data.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoDBClient {
    private static final String CONNECTION_STRING = Configuration.get("mongodb.connection.string");
    private static final String DATABASE_NAME = Configuration.get("mongodb.database.name");
    private static final Logger log = LoggerFactory.getLogger(MongoDBClient.class);

    private static MongoClient mongoClient;

    public static MongoDatabase getDatabase() {
        try {
            if (mongoClient == null) {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                log.info("Connected to MongoDB at {}", CONNECTION_STRING);
            }
            return mongoClient.getDatabase(DATABASE_NAME);
        } catch (RuntimeException e) {
            return ExceptionHandler.handle(e, "connecting to MongoDB", new DatabaseException("Failed to connect to MongoDB", e));
        }
    }


    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
