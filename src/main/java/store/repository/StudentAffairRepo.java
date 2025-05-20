package store.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import store.entity.Studentt;
import store.entity.Transcript;

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

    public List<Transcript> getAllTranscripts() {
        String sql = "SELECT * FROM transcript";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Transcript(
                rs.getInt("studentid"),
                rs.getDouble("gpa")
        ));
    }

    public Studentt getStudentById(int studentid) {
    String sql = "SELECT * FROM student WHERE studentid = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{studentid}, (rs, rowNum) -> new Studentt(
            rs.getInt("studentid"),
            rs.getString("name"),
            rs.getString("faculty"),
            rs.getString("department")
    ));
}

}
