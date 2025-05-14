package store.model;

import java.util.List;

public class ParsedTranscript {

    private int studentId;
    private List<String> courseCodes;
    private int totalCredits;
    private double gpa;
    private int semesterNumber;

    public ParsedTranscript(int studentId, List<String> courseCodes, int totalCredits, double gpa, int semesterNumber) {
        this.studentId = studentId;
        this.courseCodes = courseCodes;
        this.totalCredits = totalCredits;
        this.gpa = gpa;
        this.semesterNumber = semesterNumber;
    }

    public int getStudentId() {
        return studentId;
    }

    public List<String> getCourseCodes() {
        return courseCodes;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public double getGpa() {
        return gpa;
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }
}
