package store.repository;
import store.entity.Transcriptt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
@Repository
public class TranscriptRepo {

    private static final Logger logger = LoggerFactory.getLogger(TranscriptRepo.class);

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

        try {
            logger.debug("Executing SQL query: {}", sql);
            
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
                    logger.error("Error processing row for studentid {}: {}", 
                        rs.getInt("studentid"), e.getMessage());
                    // Continue processing other rows instead of failing completely
                }
            });
            
            logger.debug("Successfully processed {} transcript records", transcripts.size());
            return transcripts;
            
        } catch (DataAccessException e) {
            logger.error("Database access error while fetching transcripts: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while fetching transcripts: {}", e.getMessage(), e);
            throw new DataAccessException("Failed to fetch transcripts", e) {};
        }
    }

}
