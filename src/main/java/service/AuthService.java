package service;

import org.springframework.stereotype.Service;
import repository.UserRepo;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    public AuthService() {

    }

    public Map<String, String> authenticateUser(String username, String password) {

        UserRepo userRepo = new UserRepo();
        Map<String, String> userMap = userRepo.fetchUser(username, password);

        return userMap;
    }

    public boolean fetchUser() {
        return true;
    }
}
