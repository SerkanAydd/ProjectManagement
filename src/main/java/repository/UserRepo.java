package repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepo {
    public UserRepo() {

    }

    public Map<String, String> fetchUser(String username, String password) {

        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("username", "serkan");
        userMap.put("password", "aydogdu");
        userMap.put("id", "1");
        userMap.put("role", "student");
        userMap.put("token", "ıotyecbeo");

        return userMap;
    }
}
