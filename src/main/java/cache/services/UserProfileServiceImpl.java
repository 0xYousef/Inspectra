package cache.services;

import cache.context.UserContext;
import cache.models.SessionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cache.services.interfaces.UserProfileService;

import java.util.HashSet;

public class UserProfileServiceImpl implements UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);
    private final SessionServiceImpl sessionService = new SessionServiceImpl();

    @Override
    public void login(String email, String userInfo) {
        UserContext.CurrentUser(email);

        sessionService.mergeAnonymousSessionToUser(email);
        String fullName = extractFullName(userInfo);
        SessionCache session = sessionService.currentSession();
        if (session == null) {
            session = SessionCache.builder()
                    .email(email)
                    .fullName(fullName)
                    .accountInfo(userInfo)
                    .cartItems(new HashSet<>())
                    .build();
            sessionService.createSession(session);
        }

        logger.info("User {} logged in successfully", email);
    }

    @Override
    public void logout() {
        String email = UserContext.current_user();
        // Keep the session in cache, just clear the context
        UserContext.clear();
        logger.info("User {} logged out", email);
    }

    private String extractFullName(String userInfo) {
        if (userInfo == null || userInfo.isBlank()) {
            return "Unknown User";
        }

        String firstLine = userInfo.split("\\R")[0].trim();
        String[] words = firstLine.split("\\s+");

        if (words[0].equalsIgnoreCase("Mr.") || words[0].equalsIgnoreCase("Mr")
                || words[0].equalsIgnoreCase("Mrs.") || words[0].equalsIgnoreCase("Mrs")
                || words[0].equalsIgnoreCase("Ms.") || words[0].equalsIgnoreCase("Ms")) {

            if (words.length >= 3) {
                return words[1] + " " + words[2];
            }
        }

        return words.length >= 2 ? words[0] + " " + words[1] : words[0];
    }

}