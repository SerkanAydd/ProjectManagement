package store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.repository.UserRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepo userInfo;

    public Map<String, String> authenticateUser(String username, String password) {

        List<Map<String, Object>> students = userInfo.getAllStudents();
        for (Map<String, Object> student : students) {
            System.out.println(student);
        }

        String actual_password = userInfo.getPassword(username);
        String role = userInfo.getRole(username);
        String id = userInfo.getId(username);
        String token = userInfo.getToken(username);

        System.out.println(actual_password);
        System.out.println(password);

        if (password.equals(actual_password)){
            Map<String, String> userMap = new HashMap<>();
            userMap.put("username", username);
            userMap.put("password", actual_password);
            userMap.put("role", role);
            userMap.put("id", id);
            userMap.put("token", token);

            return userMap;
        } else {
            return null;
        }


    }
}

