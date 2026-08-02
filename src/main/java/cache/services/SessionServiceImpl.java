// SessionServiceImpl.java
package cache.services;

import cache.context.UserContext;
import cache.models.ProductCache;
import cache.models.SessionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cache.repository.CacheRepository;
import cache.services.interfaces.SessionService;

import java.util.HashSet;
import java.util.Optional;

public class SessionServiceImpl implements SessionService {
    private static final String PREFIX_SESSION = "session-";
    private static final String ANONYMOUS_SESSION_KEY = PREFIX_SESSION + "anonymous";
    private static final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);

    private Optional<String> getCurrentSessionKey() {
        return Optional.of(buildSessionKey(UserContext.current_user()));
    }

    private String buildSessionKey(String email) {
        return PREFIX_SESSION + email;
    }

    @Override
    public SessionCache currentSession() {
        Optional<String> sessionKey = getCurrentSessionKey();
        return sessionKey.map(string -> (SessionCache) CacheRepository.view_session(string).orElse(null)).orElse(null);
    }

    @Override
    public void createSession(SessionCache value) {
        String key = buildSessionKey(value.getEmail());
        CacheRepository.save_session(key, value);
        log.debug("Created new session for {}", value.getEmail());
    }

    @Override
    public void updateSession(String email, SessionCache value) {
        String key = buildSessionKey(email);
        CacheRepository.save_session(key, value);
        log.debug("Updated session for {}", email);
    }

    @Override
    public void closeSession() {
        getCurrentSessionKey().ifPresent(key -> {
            CacheRepository.remove_session(key);
            log.debug("Closed session {}", key);
        });
        UserContext.clear();
    }

    public SessionCache getAnonymousSession() {
        return (SessionCache) CacheRepository.view_session(ANONYMOUS_SESSION_KEY).orElse(null);
    }

    public void createAnonymousSession(SessionCache session) {
        CacheRepository.save_session(ANONYMOUS_SESSION_KEY, session);
        log.debug("Created anonymous session");
    }

    public void mergeAnonymousSessionToUser(String email) {
        SessionCache anonymousSession = getAnonymousSession();
        if (anonymousSession != null && !anonymousSession.getCartItems().isEmpty()) {
            SessionCache userSession = currentSession();
            if (userSession == null) {
                userSession = SessionCache.builder()
                        .email(email)
                        .cartItems(new HashSet<>())
                        .build();
            }

            SessionCache finalUserSession = userSession;
            anonymousSession.getCartItems().forEach(item -> {
                if (finalUserSession.getCartItems().contains(item)) {
                    ProductCache existingItem = finalUserSession.getCartItems().stream()
                            .filter(i -> i.equals(item))
                            .findFirst()
                            .get();
                    existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                } else {
                    finalUserSession.getCartItems().add(item);
                }
            });

            updateSession(email, userSession);
            removeAnonymousSession();
            log.info("Merged anonymous cart to user {}", email);
        }
    }

    public void removeAnonymousSession() {
        CacheRepository.remove_session(ANONYMOUS_SESSION_KEY);
        log.debug("Removed anonymous session");
    }
}