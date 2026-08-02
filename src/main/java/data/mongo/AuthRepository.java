package data.mongo;

import core.db.COLLECTIONS;
import core.db.MongoDBClient;
import data.DTO.Login;
import data.DTO.PersonalInfo;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AuthRepository {
    private static final Logger log = LoggerFactory.getLogger(AuthRepository.class);
    private static final String COLLECTION = COLLECTIONS.ACCOUNTS.toString();
    private static final MongoCollection<Document> collection = Objects.requireNonNull(MongoDBClient.getDatabase()).getCollection(COLLECTION);

    public List<Login> getAllUsers() {

        List<Login> users = new ArrayList<>();

        for (Document doc : collection.find()) {
            users.add(Login.builder()
                        .email(doc.getString("email"))
                        .password(doc.getString("password"))
                        .personalInfo(PersonalInfo.builder()
                            .name(doc.getString("name"))
                            .title(doc.getString("title"))
                            .firstname(doc.getString("first_name"))
                            .lastname(doc.getString("last_name"))
                            .company(doc.getString("company"))
                            .address(doc.getString("address1"))
                            .address2(doc.getString("address2"))
                            .country(doc.getString("country"))
                            .city(doc.getString("city"))
                            .state(doc.getString("state"))
                            .zipcode(doc.getString("zipcode"))
                            .phone(doc.getString("phone"))
                            .build())
                    .build());
        }

        log.info("Fetched {} users from database", users.size());
        return users;
    }

    public boolean userExists(String email) {
        Document filter = new Document("email", email);
        return collection.find(filter).first() != null;
    }
    public Document getUserByEmail(String email) {
        Document filter = new Document("email", email);
        return collection.find(filter).first();
    }
}
