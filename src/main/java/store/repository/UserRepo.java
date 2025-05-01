package store.repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Random;

@Repository
public class UserRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getPassword(String mail) {
        String password = getPasswordStudent(mail);
        if (password == null){
            password = getPasswordStaff(mail);
        }
        return password;
    }

    private String getPasswordStudent(String mail) {

        String sql = "SELECT password FROM student WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        }
        catch (Exception e) {
            return null;
        }
    }

    private String getPasswordStaff(String mail) {

        String sql = "SELECT password FROM staff WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        }
        catch (Exception e) {
            return null;
        }
    }

    public String getToken(String mail) {
        return mail;
    }

    public String getRole(String mail) {

        String student_password = getPasswordStudent(mail);

        if (student_password != null) {
            return "Student";
        } else {
            String sql = "SELECT title FROM staff WHERE mail = ?";
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        }

    }

    public String getId(String mail) {
        String id = getStudentId(mail);

        if (id == null){
            id = getStaffId(mail);
        }

        return id;
    }

    private String getStudentId(String mail) {
        String sql = "SELECT id FROM student WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        }
        catch (Exception e) {
            return null;
        }
    }

    private String getStaffId(String mail) {
        String sql = "SELECT id FROM staff WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        }
        catch (Exception e) {
            return null;
        }
    }

    public String getName(String mail) {
        String name = getStudentName(mail);

        if (name == null){
            name = getStaffName(mail);
        }

        return name;
    }

    private String getStudentName(String mail) {
        String sql = "SELECT name FROM student WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        }
        catch (Exception e) {
            return null;
        }
    }

    private String getStaffName(String mail) {
        String sql = "SELECT name FROM staff WHERE mail = ?";

        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        }
        catch (Exception e) {
            return null;
        }
    }

    public boolean register_student(String mail, String name, String faculty, String department) {
        String sql = "INSERT INTO student (studentid, password, faculty, department, startDate, mail, name, graduationstatus, staff_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sql_ = "SELECT id FROM staff WHERE title = ?";
        List<String> advisorid_list = null;

        try {
            advisorid_list = jdbcTemplate.query( sql_, (rs, rowNum) -> rs.getString(1),"Advisor");
        } catch (Exception e) {
            return false;
        }

        Random rand = new Random();
        String random_Advisor = advisorid_list.get(rand.nextInt(advisorid_list.size()));
        int randomAdvisor = Integer.parseInt(random_Advisor);

        try {
            int rowsAffected = jdbcTemplate.update(sql, 31, "itispassword", faculty, department, Date.valueOf("2021-05-31"), mail, name, "Active", randomAdvisor);
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace(); // You can log or handle this more appropriately
            return false;
        }

    }
}

