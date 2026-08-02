// CacheRepository.java
package cache.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CacheRepository {
    private static final Cache<String, Object> sessionCache =
            Caffeine.newBuilder()
                    .expireAfterAccess(30, TimeUnit.MINUTES)
                    .build();

    @Getter
    private static final Set<String> sessionKeys = new HashSet<>();

    @Getter
    private static final Set<String> completedOrders = new HashSet<>();

    public static void save_session(String key, Object session) {
        sessionCache.put(key, session);
        sessionKeys.add(key);
        view_session(key);
    }

    public static Optional<Object> view_session(String key) {
        return Optional.ofNullable(sessionCache.getIfPresent(key));
    }

    public static List<Optional<Object>> view_all_sessions() {
        return List.copyOf(sessionKeys.stream().map(CacheRepository::view_session).toList());
    }

    public static void remove_session(String key) {
        sessionCache.invalidate(key);
        sessionKeys.remove(key);
    }

    public static void clear_all_sessions() {
        sessionKeys.clear();
        sessionCache.invalidateAll();
    }

    public static void markOrderCompleted(String email) {
        completedOrders.add(email);
        remove_session("session-" + email);
    }

    public static boolean hasCompletedOrder(String email) {
        return completedOrders.contains(email);
    }

}