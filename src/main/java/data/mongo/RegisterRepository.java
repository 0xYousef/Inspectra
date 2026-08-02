package data.mongo;

import com.mongodb.client.MongoCollection;
import core.db.COLLECTIONS;
import core.db.MongoDBClient;
import data.DTO.Register;
import data.exceptions.ExceptionHandler;
import data.mapper.RegisterMapper;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RegisterRepository {
    private static final Logger log = LoggerFactory.getLogger(RegisterRepository.class);
    private static final String COLLECTION = COLLECTIONS.ACCOUNTS.toString();

    private static MongoCollection<Document> collection() {
        return Objects.requireNonNull(MongoDBClient.getDatabase()).getCollection(COLLECTION);
    }

    public void insertUser(Register user) {
        try {
            collection().insertOne(RegisterMapper.toDocument(user));
            log.info("Inserted user name: {} email: {}", user.getName(), user.getEmail());
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "inserting user " + user.getEmail());
        }
    }

    public void deleteUser(String email) {
        try {
            Document filter = new Document("email", email);
            long deletedCount = collection().deleteOne(filter).getDeletedCount();

            if (deletedCount > 0) {
                log.info("Deleted user email: {}", email);
            } else {
                log.info("Not Found User with email {}", email);
            }
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "deleting user " + email);
        }
    }

    public void updateUserByEmail(String email, Register updatedUser) {
        try {
            Document filter = new Document("email", email);
            Document update = new Document("$set", RegisterMapper.toDocument(updatedUser));

            long modifiedCount = collection().updateOne(filter, update).getModifiedCount();

            if (modifiedCount > 0) {
                log.info("Updated user with email: {}", email);
            } else {
                log.warn("No user found with email: {} to update", email);
            }
        } catch (RuntimeException e) {
            ExceptionHandler.handleSilently(e, "updating user " + email);
        }
    }
}
