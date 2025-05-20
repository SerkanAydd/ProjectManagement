package store.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TranscriptRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveTranscript(int studentId, int totalCredits, double gpa, String courses, int semesterNumber, java.sql.Date date) {
        String sql = "INSERT INTO transcript (studentid, totalcredits, gpa, courses, semesternum, date) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, studentId, totalCredits, gpa, courses, semesterNumber, date);
    }

    public void saveCourse(int studentId, String courseCode) {
        String sql = "INSERT INTO course_transcript (studentid, coursecode) VALUES (?, ?)";
        jdbcTemplate.update(sql, studentId, courseCode);
    }

    public boolean transcriptAlreadyExists(int studentId) {
        String sql = "SELECT COUNT(*) FROM transcript WHERE studentid = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
        return count != null && count > 0;
    }
}
