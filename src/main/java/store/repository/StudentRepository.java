package store.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.sql.Date;
import java.util.List;
import store.entity.Student;
import java.math.BigDecimal;


@Repository
public class StudentRepository 
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String getSqlStr(int sid, String sql) 
    {
        try {
            return jdbcTemplate.queryForObject(sql, String.class, sid);
        }
        catch (Exception e) {
            System.out.println("Student can not found: " + sid);
            return null;
        }
    }

    private int getSqlint(int sid, String sql)
    {
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, sid);
        }
        catch (Exception e) {
            System.out.println("Student can not found: " + sid);
            return -1;
        }
    }

    private Date getSqlStartDate(int sid)
    {
        String sql = "SELECT startdate FROM student WHERE studentid = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Date.class, sid);
        }
        catch (Exception e) {
            System.out.println("Student can not found: " + sid);
            return Date.valueOf("1900-01-01");
        }
    }

    public Student findById(int sid)
    {
        String name = getSqlStr(sid, "SELECT name FROM student WHERE studentid = ?"); //
        String mail = getSqlStr(sid, "SELECT mail FROM student WHERE studentid = ?"); //
        String department = getSqlStr(sid, "SELECT depatment FROM student WHERE studentid = ?"); //
        String faculty = getSqlStr(sid, "SELECT faculty FROM student WHERE studentid = ?"); //
        Date startDate = getSqlStartDate(sid); //

        if (name == null || mail == null || department == null || faculty == null || startDate.equals(Date.valueOf("1900-01-01")))
        {
            System.out.println("Student not exist or there was an error while findById(sid) Method, given argument was: " + sid);
            return null;
        }

        Student student = new Student(sid, name, faculty, department, null, startDate, mail, null);
        student.setGPA(getGpaOfStudent(student));
        student.setAdvisorName(getAdvisorNameOfStudent(student));
        return student;
    }

    public boolean updateGraduationStatus(int sid, String newStatus)
    {
        String sql = "UPDATE student SET graduationstatus = ? WHERE studentid = ?";
        try {
            jdbcTemplate.update(sql, newStatus, sid);
        }
        catch (Exception e) {
            System.out.println("Error occuer while updating graduation status");
            return false;
        }
        return true;
    }

    private int getStaffIdOfStudent(Student stud)
    {
        int sid = stud.getStudentId();
        String sql = "SELECT staff_id FROM student WHERE studentid = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, sid);
        }
        catch (Exception e) {
            System.out.println("Student can not found: " + sid);
            return -1;
        }
    }

    private String getAdvisorNameOfStudent(Student stud)
    {
        int staffid = getStaffIdOfStudent(stud);
        String sql = "SELECT name FROM staff WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, staffid);
        }
        catch (Exception e) {
            System.out.println("Advisor can not found: " + staffid);
            return null;
        }
    }

    private BigDecimal getGpaOfStudent(Student stud)
    {
        int sid = stud.getStudentId();
        String sql = "SELECT gpa FROM transcript WHERE studentid = ?";
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, sid);
        }
        catch (Exception e) {
            System.out.println("Student can not found: " + sid);
            return null;
        }
    }

    private List<Student> rankStudents(List<Student> students)
    {
        int len = students.size();
        int i = 0;
        int j = 0;
        while (i < len)
        {
            j = i + 1;
            while (j < len)
            {
                if (getGpaOfStudent(students.get(i)).compareTo(getGpaOfStudent(students.get(j))) < 0)
                {
                    Student temp = students.get(i);
                    students.set(i, students.get(j));
                    students.set(j, temp);
                }
                j++;
            }
            i++;
        }
        return (students);
    }

    protected List<Student> getGPAandAdvNameforEach(List<Student> students)
    {
        for (Student student : students)
        {
            student.setAdvisorName(getAdvisorNameOfStudent(student));
            student.setGPA(getGpaOfStudent(student));
        }
        return students;
    }

    public List<Student> getRankedStudentListByDepartment(String departmentName)
    {
        String sql = "SELECT * FROM student WHERE approval = 'Approved' AND department = ?";
        List<Student> students = jdbcTemplate.query(sql, new Object[]{departmentName}, new BeanPropertyRowMapper<>(Student.class));
        students = getGPAandAdvNameforEach(students);
        if (students.isEmpty())
        {
            System.out.println("Student are not approved for all department or No Student Found for given department: " + departmentName);
            return null;
        }
        return (rankStudents(students));
    }

    public List<Student> getRankedStudentListByFaculty(String facultyName)
    {
        String sql = "SELECT * FROM student WHERE approval = 'Approved' AND faculty = ?";
        List<Student> students = jdbcTemplate.query(sql, new Object[]{facultyName}, new BeanPropertyRowMapper<>(Student.class));
        students = getGPAandAdvNameforEach(students);
        if (students.isEmpty())
        {
            System.out.println("No Student Found for given faculty: ." + facultyName);
            return null;
        }
        return (rankStudents(students));
    }

    public List<Student> getRankedStudentList() 
    {
        String sql = "SELECT * FROM student WHERE approval = 'Approved'";
        List<Student> students = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Student.class));
        students = getGPAandAdvNameforEach(students);
        if (students.isEmpty())
        {
            System.out.println("No Student Found.");
            return null;
        }
        return (rankStudents(students));
    }

    public List<Student> getStudentListByDeparment(String departmentName)
    {
        String sql = "SELECT * FROM student WHERE department = ? ORDER BY name ASC";
        List<Student> students = jdbcTemplate.query(sql, new Object[]{departmentName},  new BeanPropertyRowMapper<>(Student.class));
        students = getGPAandAdvNameforEach(students);
        if (students.isEmpty())
        {
            System.out.println("No Student Found for given department:" + departmentName);
            return null;
        }
        return (students);
    }

    public List<Student> getAllStudents()
    {
        String sql = "SELECT * FROM student ORDER BY name ASC";
        List<Student> students = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Student.class));
        students = getGPAandAdvNameforEach(students);
        if (students.isEmpty())
        {
            System.out.println("No Student Found.");
            return null;
        }
        return (students);
    }
}