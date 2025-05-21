package store.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import store.entity.Studentt;
import store.entity.Transcript;

import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentAffairRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Studentt> getAllStudents() {
        String sql = "SELECT * FROM student";
        List<Studentt> students = new ArrayList<>();

        jdbcTemplate.query(sql, rs -> {
            try {
                Studentt s = new Studentt(
                    rs.getInt("studentid"),
                    rs.getString("name"),
                    rs.getString("faculty"),
                    rs.getString("department")
                );
                students.add(s);
            } catch (Exception e) {
                // Log and skip this row
                System.err.println("Skipping row due to error: " + e.getMessage());
            }
        });

        return students;
    }

    public List<Transcript> getAllTranscripts() {
        String sql = "SELECT * FROM transcript";
        List<Transcript> transcripts = new ArrayList<>();

        jdbcTemplate.query(sql, rs -> {
            try {
                Transcript t = new Transcript(
                    rs.getInt("studentid"),
                    rs.getDouble("gpa")
                );
                transcripts.add(t);
            } catch (Exception e) {
                System.err.println("Skipping row due to error: " + e.getMessage());
            }
        });
        return transcripts;
    }

    public Studentt getStudentById(int studentid) {
        String sql = "SELECT * FROM student WHERE studentid = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{studentid}, (rs, rowNum) -> new Studentt(
                rs.getInt("studentid"),
                rs.getString("name"),
                rs.getString("faculty"),
                rs.getString("department")
            ));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Studentt> getApprovedStudents() {
        String sql = "SELECT * FROM student WHERE approval = 'Approved'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Studentt(
                rs.getInt("studentid"),
                rs.getString("name"),
                rs.getString("faculty"),
                rs.getString("department")
        ));
    }

    public List<Integer> getStudentIdsByGpa(double gpa) {
        String sql = "SELECT studentid FROM transcript WHERE gpa = ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{gpa}, (rs, rowNum) -> rs.getInt("studentid"));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<String> getDistinctFaculties() {
        String sql = "SELECT DISTINCT faculty FROM student";
        try {
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (DataAccessException e) {
            System.err.println("Error fetching faculties: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> getDistinctDepartments() {
        String sql = "SELECT DISTINCT department FROM student";
        try {
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (DataAccessException e) {
            // Log the error and return an empty list instead of crashing
            System.err.println("Error fetching departments: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Studentt> getAllStudentsByFaculty(String faculty) {
        String sql = "SELECT * FROM student WHERE faculty = ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{faculty}, (rs, rowNum) -> new Studentt(
                rs.getInt("studentid"),
                rs.getString("name"),
                rs.getString("faculty"),
                rs.getString("department")
            ));
        } catch (DataAccessException e) {
            System.err.println("Error fetching students by faculty: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Studentt> getAllStudentsByDepartment(String department) {
        String sql = "SELECT * FROM student WHERE department = ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{department}, (rs, rowNum) -> new Studentt(
                rs.getInt("studentid"),
                rs.getString("name"),
                rs.getString("faculty"),
                rs.getString("department")
            ));
        } catch (DataAccessException e) {
            System.err.println("Error fetching students by department: " + e.getMessage());
            return new ArrayList<>();
        }
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

