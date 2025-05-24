package store.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class Transcriptt {

    @JsonProperty("studentId")
    private int studentId;
    
    @JsonProperty("courses")
    private String courses;
    
    @JsonProperty("totalCredits")
    private int totalCredits;
    
    @JsonProperty("gpa")
    private double gpa;
    
    @JsonProperty("date")
    private LocalDate date;
    
    @JsonProperty("semesterNum")
    private int semesterNum;

    // Default constructor for JSON serialization
    public Transcriptt() {
    }

    // Parameterized constructor
    public Transcriptt(int studentId, String courses, int totalCredits, double gpa, LocalDate date, int semesterNum) {
        this.studentId = studentId;
        this.courses = courses;
        this.totalCredits = totalCredits;
        this.gpa = gpa;
        this.date = date;
        this.semesterNum = semesterNum;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getSemesterNum() {
        return semesterNum;
    }

    public void setSemesterNum(int semesterNum) {
        this.semesterNum = semesterNum;
    }
    @Override
    public String toString() {
        return "Transcript{" +
                "studentId=" + studentId +
                ", courses='" + courses + '\'' +
                ", totalCredits=" + totalCredits +
                ", gpa=" + gpa +
                ", date=" + date +
                ", semesterNum=" + semesterNum +
                
                '}';
    }
}
