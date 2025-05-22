package store.repository;
import store.entity.Transcriptt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.ArrayList;
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
    public List<Transcriptt> getAllTranscripts() {
        String sql = "SELECT * FROM transcript";


        List<Transcriptt> transcripts = new ArrayList<>();

        jdbcTemplate.query(sql, rs -> {
            try {
                Transcriptt t = new Transcriptt(
                        rs.getInt("studentid"),
                        rs.getString("courses"),
                        rs.getInt("totalcredits"),
                        rs.getDouble("gpa"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("semesternum")
                );
                transcripts.add(t);
            } catch (Exception e) {
                // Log and skip this row
                System.err.println("Skipping row due to error: " + e.getMessage());
            }
        });

        return transcripts;


        }

}
