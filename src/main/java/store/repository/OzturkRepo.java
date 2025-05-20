package store.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<String> viewCurriculum(int curriculumId){
        String sql = "SELECT coursecode FROM course_curriculum WHERE curriculumid = ?";
        return jdbcTemplate.queryForList(sql, String.class, curriculumId);        
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
        String sql = "SELECT coursecode FROM course_transcript WHERE studentid = ?";
        return jdbcTemplate.queryForList(sql, String.class, studentId);
    }

    public String findDepartmentByStudentId(Long studentId) {
        String sql = "SELECT department FROM student WHERE studentid = ?";
        return jdbcTemplate.queryForObject(sql, String.class, studentId);
    }

    public Long findStudentIdByName(String studentName, String mail) {
    long staffid =  findStaffIdByEmail(mail);
    System.out.println("Staffid" +staffid);
    String sql = "SELECT studentid FROM student WHERE name = ? AND staff_id = ?";
    return jdbcTemplate.queryForObject(sql, Long.class, studentName, staffid);
    }




}
