package store.repository;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CurriculumRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int saveCurriculum(String faculty, String department, int techElectiveCount, int socialElectiveCount, Date date) {
        String sql = "INSERT INTO curriculum (faculty, department, tech_elective_count, social_elective_count, date) VALUES (?, ?, ?, ?, ?) RETURNING curriculumid";
        return jdbcTemplate.queryForObject(sql, Integer.class, faculty, department, techElectiveCount, socialElectiveCount, date);
    }

    public void saveCourse(int curriculumId, String courseCode, String category) {
        String sql = "INSERT INTO course_curriculum (curriculumid, coursecode, category) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, curriculumId, courseCode, category);
    }

    public boolean curriculumAlreadyExists(String faculty, String department) {
        String sql = "SELECT COUNT(*) FROM curriculum WHERE faculty = ? AND department = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, faculty, department);
        return count != null && count > 0;
    }
}
