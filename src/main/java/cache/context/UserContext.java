package cache.context;



public class UserContext {
    private static String CURRENT_USER = null;
    private static final String DEFAULT_ANONYMOUS = "anonymous";

    public static void CurrentUser(String email) {
        CURRENT_USER = email;
    }

    public static String current_user() {
        return (CURRENT_USER != null) ? CURRENT_USER : DEFAULT_ANONYMOUS;

    }

    public static void clear() {
        if ( CURRENT_USER != null)
               CURRENT_USER=null;
    }
}
