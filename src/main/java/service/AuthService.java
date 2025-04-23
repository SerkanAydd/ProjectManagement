package service;

import org.springframework.stereotype.Service;
import repository.UserRepo;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private String username;

    public AuthService(String username) {
        this.username = username;
    }

    public Map<String, String> authenticateUser(String username, String password) {

        UserRepo userRepo = new UserRepo(username);

        String actual_password = userInfo.getPassword();
        String role = userInfo.getRole();
        String id = userInfo.getId();
        String token = userInfo.getToken();

        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("password", actual_password);
        userMap.put("role", role);
        userMap.put("id", id);
        userMap.put("token", token);

        return userMap;
    }
}

