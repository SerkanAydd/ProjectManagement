package store.repository;

import java.sql.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getPassword(String mail) {
        String password = getPasswordStudent(mail);

        if (password == null) {
            password = getPasswordStaff(mail);
        }

        return password;
    }

    private String getPasswordStudent(String mail) {

        String sql = "SELECT password FROM student WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            return null;
        }
    }

    private String getPasswordStaff(String mail) {
        String sql = "SELECT password FROM staff WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            System.err.println("Error in getPasswordStaff: " + e.getMessage());
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

        if (id == null) {
            id = getStaffId(mail);
        }

        return id;
    }

    private String getStudentId(String mail) {
        String sql = "SELECT studentid FROM student WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            return null;
        }
    }

    private String getStaffId(String mail) {
        String sql = "SELECT id FROM staff WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            return null;
        }
    }

    public String getName(String mail) {
        String name = getStudentName(mail);

        if (name == null) {
            name = getStaffName(mail);
        }

        return name;
    }

    private String getStudentName(String mail) {
        String sql = "SELECT name FROM student WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            return null;
        }
    }

    private String getStaffName(String mail) {
        String sql = "SELECT name FROM staff WHERE mail = ?";

        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            return null;
        }
    }

    private String getStudentFaculty(String mail) {
        String sql = "SELECT faculty FROM student WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            return null;
        }
    }

    private String getStaffFaculty(String mail) {
        String sql = "SELECT faculty FROM staff WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            return null;
        }
    }

    public String getFaculty(String mail) {
        String faculty = getStudentFaculty(mail);

        if (faculty == null) {
            faculty = getStaffFaculty(faculty);
        }

        return faculty;
    }

    private String getStudentDepartment(String mail) {
        String sql = "SELECT department FROM student WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            return null;
        }
    }

    private String getStaffDepartment(String mail) {
        String sql = "SELECT department FROM staff WHERE mail = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, mail);
        } catch (Exception e) {
            return null;
        }
    }

    public String getDepartment(String mail) {
        String department = getStudentDepartment(mail);

        if (department == null) {
            department = getStaffDepartment(mail);
        }

        return department;
    }

    public boolean register_student(String studentno, String mail, String name, String faculty, String department, String password) {
        String sql = "INSERT INTO student (studentid, password, faculty, department, startDate, mail, name, graduationstatus, staff_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sql_ = "SELECT id FROM staff WHERE title = ?";
        List<String> advisorid_list = null;

        try {
            advisorid_list = jdbcTemplate.query(sql_, (rs, rowNum) -> rs.getString(1), "Advisor");
        } catch (Exception e) {
            return false;
        }

        Random rand = new Random();
        String random_Advisor = advisorid_list.get(rand.nextInt(advisorid_list.size()));
        int randomAdvisor = Integer.parseInt(random_Advisor);

        try {
            // Convert string studentno to integer for database insertion
            int studentId = Integer.parseInt(studentno);
            int rowsAffected = jdbcTemplate.update(sql, studentId, password, faculty, department, Date.valueOf("2021-05-31"), mail, name, "Active", randomAdvisor);
            return rowsAffected > 0;
        } catch (NumberFormatException e) {
            System.err.println("Invalid student number format: " + studentno);
            return false;
        } catch (Exception e) {
            e.printStackTrace(); // You can log or handle this more appropriately
            return false;
        }
    }

    public boolean register_staff(int id, String mail, String name, String title, String faculty, String department, String password) {
        // Replace all spaces with underscores in the title
        String normalizedTitle = title.replace(" ", "_");

        String sql = "INSERT INTO staff (id, mail, name, title, faculty, department, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int rows = jdbcTemplate.update(sql,
                id,
                mail,
                name,
                normalizedTitle,
                faculty,
                department,
                password
        );
        return rows > 0;
    }

    public int findMaxStaffId() {
        // COALESCE ensures we get 0 instead of null when the table is empty
        String sql = "SELECT COALESCE(MAX(id), 0) FROM staff";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int findMaxStudentId() {
        // COALESCE ensures we get 0 instead of null when the table is empty
        String sql = "SELECT COALESCE(MAX(studentid), 0) FROM student";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public boolean isStudentNoExists(String studentno) {
        String sql = "SELECT COUNT(*) FROM student WHERE studentid = ?";
        try {
            // Convert string studentno to integer for database query
            int studentId = Integer.parseInt(studentno);
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
            return count != null && count > 0;
        } catch (NumberFormatException e) {
            System.err.println("Invalid student number format: " + studentno);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
