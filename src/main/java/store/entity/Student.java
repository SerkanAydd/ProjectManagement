package store.entity;

import java.sql.Date;
import java.math.BigDecimal;

public class Student
{
    Integer studentId;
    String name;
    String faculty;
    String department;
    BigDecimal GPA;
    Date startDate;
    String mail;
    String advisorName;

    public Student() {}

    public Student(Integer studentId, String name, String faculty, String department, BigDecimal GPA, Date startDate, String mail, String advisorName) {
        this.studentId = studentId;
        this.name = name;
        this.faculty = faculty;
        this.department = department;
        this.GPA = GPA;
        this.startDate = startDate;
        this.mail = mail;
        this.advisorName = advisorName;
    }

     public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public BigDecimal getGPA() {
        return GPA;
    }

    public void setGPA(BigDecimal GPA) {
        this.GPA = GPA;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }
}
