package core.db;

import lombok.Getter;

@Getter
public enum COLLECTIONS {
    ACCOUNTS("accounts"),
    PRODUCTS("products"),
    AUTH_HISTORY("auth-logs");

    private final String collection;

    COLLECTIONS(String collection) {
        this.collection = collection;
    }

    @Override
    public String toString() {
        return collection;
    }
}
