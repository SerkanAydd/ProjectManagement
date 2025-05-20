package store.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.sql.Date;
import java.util.List;
import store.entity.Staff;
import store.entity.Student;
import store.repository.StudentRepository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.math.BigDecimal;

@Repository
public class AdvisorRepository 
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentRepository stdRepo;

    private String getSqlStr(int aid, String sql) 
    {
        try {
            return jdbcTemplate.queryForObject(sql, String.class, aid);
        }
        catch (Exception e) {
            System.out.println("Advisor can not found: " + aid);
            return null;
        }
    }

    public Staff findById(int aid)
    {
        String name = getSqlStr(aid, "SELECT name FROM staff WHERE title = 'Advisor' AND id = ?");
        String password = getSqlStr(aid, "SELECT password FROM staff WHERE title = 'Advisor' AND id = ?");
        String faculty = getSqlStr(aid, "SELECT faculty FROM staff WHERE title = 'Advisor' AND id = ?");
        String department = getSqlStr(aid, "SELECT department FROM staff WHERE title = 'Advisor' AND id = ?");
        String mail = getSqlStr(aid, "SELECT mail FROM staff WHERE title = 'Advisor' AND id = ?");

        if (name == null || password == null || faculty == null || department == null || mail == null)
        {
            System.out.println("Advisor not exist or there was an error while findById(aid) Method, given argument was: " + aid);
            return null;
        }
        Staff advisor = new Staff(aid, password, name, faculty, department, "Advisor", mail);
        return advisor;
    }

    public int getAdvisorId(String mail) 
    {
        String sql = "SELECT id FROM staff WHERE mail = ?";
        try
        {
            return jdbcTemplate.queryForObject(sql, Integer.class, mail);
        } 
        catch (EmptyResultDataAccessException e) 
        {
            System.out.println("No advisor found with mail: " + mail);
            return -1;
        }
        catch (IncorrectResultSizeDataAccessException e) 
        {
            System.out.println("Multiple advisors found with same mail: " + mail);
            return -1;
        }
        catch (Exception e) 
        {
            System.out.println("Unexpected error: " + e.getMessage());
            return -1;
        }
    }

    public List<Student> findStudentsByAdvisor(int aid)
    {
        String sql = "SELECT * FROM student WHERE staff_id = ?";
        List<Student> students = jdbcTemplate.query(sql, new Object[]{aid}, new BeanPropertyRowMapper<>(Student.class));
        students = stdRepo.getGPAandAdvNameforEach(students);
        if (students.isEmpty()) {
            System.out.println("No Student Found for given advisor id: " + aid);
            return null;
        }
        return students;
    }

    public List<Student> findApprovedStudentsByAdvisor(int aid)
    {
        String sql = "SELECT * FROM student WHERE staff_id = ? AND approval = 'Approved'";
        List<Student> students = jdbcTemplate.query(sql, new Object[]{aid}, new BeanPropertyRowMapper<>(Student.class));
        students = stdRepo.getGPAandAdvNameforEach(students);
        if (students.isEmpty()) {
            System.out.println("No Student Found for given advisor id: " + aid + "or there is no approved student for this advisor");
            return null;
        }
        return students;
    }

    public boolean changeGraduationStatus(int aid, int sid, String graduationStatus) 
    {
        String sql = "UPDATE student SET graduation_status = ? WHERE staff_id = ? AND student_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, graduationStatus, aid, sid);
        return (rowsAffected == 1);
    }

}
