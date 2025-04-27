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

    public List<Map<String, Object>> getAllStudents() {
        String sql = "SELECT * FROM student";
        return jdbcTemplate.queryForList(sql);
    }

    public String getPassword(String username) {
        int studentid = Integer.parseInt(username);

        String sql = "SELECT password FROM student WHERE studentid = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);

        } catch (Exception e) {
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

