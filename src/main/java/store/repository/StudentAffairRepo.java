package store.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public int getStudentIdByGpa(double gpa) {
        String sql = "SELECT studentid FROM transcript WHERE gpa = ?";
        System.out.println(gpa);
        return jdbcTemplate.queryForObject(sql, new Object[]{gpa}, Integer.class);
    }

    public List<String> getDistinctFaculties() {
        String sql = "SELECT DISTINCT faculty FROM student";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> getDistinctDepartments() {
        String sql = "SELECT DISTINCT department FROM student";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<Studentt> getAllStudentsByFaculty(String faculty) {
    String sql = "SELECT * FROM student WHERE faculty = ?";
    return jdbcTemplate.query(sql, new Object[]{faculty}, (rs, rowNum) -> new Studentt(
        rs.getInt("studentid"),
        rs.getString("name"),
        rs.getString("faculty"),
        rs.getString("department")
    ));
    }

    public List<Studentt> getAllStudentsByDepartment(String department) {
    String sql = "SELECT * FROM student WHERE department = ?";
    return jdbcTemplate.query(sql, new Object[]{department}, (rs, rowNum) -> new Studentt(
        rs.getInt("studentid"),
        rs.getString("name"),
        rs.getString("faculty"),
        rs.getString("department")
    ));
    }

    public double getGpaById(int studentid) {
        String sql = "SELECT gpa FROM transcript WHERE studentid = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{studentid}, Double.class);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("No GPA found for student ID: " + studentid);
            return -1.00; // or return a default value like 0.0
        }
    }


}

