package store.entity;

public class Staff 
{
    private int id;
    private String password;
    private String name;
    private String faculty;
    private String department;
    private String title;
    private String mail;

    public Staff() {}

    public Staff(int id, String password, String name, String faculty, String deparment, String title, String mail)
    {
        this.id = id;
        this.password = password;
        this.name = name;
        this.faculty = faculty;
        this.department = deparment;
        this.title = title;
        this.mail = mail;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}

