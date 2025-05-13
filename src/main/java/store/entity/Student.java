package store.entity;

public class Student {
    private int studentid;
    private String name;
    private String faculty;
    private String department;

    public Student(int studentid, String name, String faculty, String department) {
        this.studentid = studentid;
        this.name = name;
        this.faculty = faculty;
        this.department = department;
    }

    public int getStudentid() {
        return studentid;
    }

    public String getName() {
        return name;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getDepartment() {
        return department;
    }
}
