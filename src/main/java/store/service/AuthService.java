package store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.repository.UserRepo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepo userInfo;

    public Map<String, String> authenticateUser(String mail, String password) {

        String storedHash = userInfo.getPassword(mail);
        if (storedHash == null){
            return null;
        }

        if (passwordEncoder.matches(password, storedHash)) {
            String role = userInfo.getRole(mail);
            String id = userInfo.getId(mail);
            String token = userInfo.getToken(mail);

            Map<String, String> userMap = new HashMap<>();
            userMap.put("role", role);
            userMap.put("id", id);
            userMap.put("token", token);

            return userMap;

        } else {
            System.out.println("Password Incorrect");

            return null;
        }
    }
}

