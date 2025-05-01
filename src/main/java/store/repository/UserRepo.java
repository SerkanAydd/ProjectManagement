package store.repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getPassword(String mail) {

        String sql = "SELECT password FROM student WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);

        } catch (Exception e) {
            System.out.println("Error here");
            // If not found or error, you can log or throw custom exception
            return null;
        }
    }

    public String getToken(String username) {
        return username;
    }

    public String getRole(String username) {
        return username;
    }

    public String getId(String username) {
        return username;
    }
}

