package selenium.components;

import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cache.models.SessionCache;
import cache.services.SessionServiceImpl;

public class Header {

    public static final Logger log = LoggerFactory.getLogger(Header.class);
    private final SessionServiceImpl sessionService = new SessionServiceImpl();

    @Step("Get Logged username")
    public String getLoggedInUsername(){
        String currentUser = getUserInfo();
        if (currentUser == null) {
            log.info("No logged user");
            return null;
        }
        log.info("logged user: {}",currentUser.lines().findFirst().get());
        return currentUser.lines().findFirst().get();
    }
    private  String getUserInfo() {
        SessionCache session = sessionService.currentSession();
        if (session == null) {
            return null;
        }
        return session.getAccountInfo();
    }

}
