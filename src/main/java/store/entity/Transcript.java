package store.entity;

public class Transcript {
    private int studentid;
    private double gpa;
    
    public Transcript(int studentid, double gpa) {
        this.studentid = studentid;
        this.gpa = gpa;
    }

    public int getStudentId() {
        return studentid;
    }

    public double getGpa() {
        return gpa;
    }
}
