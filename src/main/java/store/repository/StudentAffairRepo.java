package store.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import store.entity.Student;

import java.util.List;

@Repository
public class StudentAffairRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Student> getAllStudents() {
        String sql = "SELECT * FROM student";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Student(
                rs.getInt("studentid"),
                rs.getString("faculty"),
                rs.getString("department"),
                rs.getString("name")
        ));
    }
}
