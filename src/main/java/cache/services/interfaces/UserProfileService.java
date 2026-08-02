package cache.services.interfaces;



public interface UserProfileService {
    void login(String email, String userInfo);
    void logout();
}
