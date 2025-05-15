package store.model;

import java.util.List;

public class ParsedCurriculum {

    private String faculty;
    private String department;
    private int techElectiveCount;
    private int socialElectiveCount;
    private List<String[]> courses; // each course: [code, category]

    public ParsedCurriculum(String faculty, String department, int techElectiveCount, int socialElectiveCount, List<String[]> courses) {
        this.faculty = faculty;
        this.department = department;
        this.techElectiveCount = techElectiveCount;
        this.socialElectiveCount = socialElectiveCount;
        this.courses = courses;
    }

    // Getters
    public String getFaculty() {
        return faculty;
    }

    public String getDepartment() {
        return department;
    }

    public int getTechElectiveCount() {
        return techElectiveCount;
    }

    public int getSocialElectiveCount() {
        return socialElectiveCount;
    }

    public List<String[]> getCourses() {
        return courses;
    }
}
