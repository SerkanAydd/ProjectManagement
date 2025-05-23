package store.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;




@Repository
public class OzturkRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer takeCurriculumid(String department){
        String sql = "SELECT curriculumid FROM curriculum WHERE department = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, department);        
    }
    
    public List<Map<String, Object>> viewCurriculum(int curriculumId) {
    String sql = "SELECT coursecode, category FROM course_curriculum WHERE curriculumid = ?";
    return jdbcTemplate.queryForList(sql, curriculumId);
}
    public List<String> viewCurriculumByCategory(int curriculumId, String category) {
    String sql = "SELECT coursecode FROM course_curriculum WHERE curriculumid = ? AND category = ?";
    return jdbcTemplate.queryForList(sql, String.class, curriculumId, category);
}




    public Integer findStaffIdByEmail(String email) {
        String sql = "SELECT id FROM staff WHERE mail = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, email);
    }

    public List<Map<String, String>> findStudentNamesAndApprovalsByAdvisorMail(String advisorMail) {
    
        int staff_id = findStaffIdByEmail(advisorMail);
        String sql = "SELECT name, approval FROM student WHERE staff_id = ?";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        Map<String, String> row = new HashMap<>();
        row.put("name", rs.getString("name"));
        row.put("approval", rs.getString("approval"));
        return row;
    }, staff_id);
    }

    public int updateGraduationStatusPairs(String staffMail, List<Map<String, String>> updates) {
        
        long staffId = findStaffIdByEmail(staffMail);
        String sql = "UPDATE student SET approval = ? WHERE staff_id = ? AND name = ?";
        int totalUpdated = 0;

        for (Map<String, String> update : updates) {
            String name = update.get("name");
            String status = update.get("status");
            int updated = jdbcTemplate.update(sql, status, staffId, name);
            totalUpdated += updated;
        }

        return totalUpdated;
    }   

public List<String> findCourseCodesByStudentId(Long studentId) {
    String sql = "SELECT courses FROM transcript WHERE studentid = ?";
    String raw = jdbcTemplate.queryForObject(sql, String.class, studentId);

    if (raw != null && !raw.isBlank()) {
        return Arrays.stream(raw.split(","))
            .map(s -> s.trim().toUpperCase())  // boşlukları temizle, büyük harfe çevir
            .collect(Collectors.toList());
    } else {
        return Collections.emptyList();
    }
}




    public String findDepartmentByStudentId(Long studentId) {
        String sql = "SELECT department FROM student WHERE studentid = ?";
        return jdbcTemplate.queryForObject(sql, String.class, studentId);
    }

    public Long findStudentIdByName(String studentName, String mail) {
    long staffid =  findStaffIdByEmail(mail);
    String sql = "SELECT studentid FROM student WHERE name = ? AND staff_id = ?";
    return jdbcTemplate.queryForObject(sql, Long.class, studentName, staffid);
    }

    public Map<String, Integer> takeTechElectiveAndSocialElective(String department) {
        String sql = "SELECT tech_elective_count, social_elective_count FROM curriculum WHERE department = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Map<String, Integer> result = new HashMap<>();
            result.put("tech_elective_count", rs.getInt("tech_elective_count"));
            result.put("social_elective_count", rs.getInt("social_elective_count"));
            return result;
        }, department);
    }





}
