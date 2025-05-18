package store.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import store.entity.Studentt;

import java.util.List;

@Repository
public class StudentAffairRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Studentt> getAllStudents() {
        String sql = "SELECT * FROM student";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Studentt(
                rs.getInt("studentid"),
                rs.getString("name"),
                rs.getString("faculty"),
                rs.getString("department")
        ));
    }
}
