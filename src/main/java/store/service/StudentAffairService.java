package store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.entity.Student;
import store.repository.StudentAffairRepo;

import java.util.List;

@Service
public class StudentAffairService {

    @Autowired
    private StudentAffairRepo studentAffairRepo;

    public int downloadAllDiplomas() {
        List<Student> studentList = studentAffairRepo.getAllStudents();

        for (Student student : studentList) {
            System.out.println("ID: " + student.getStudentid());
            System.out.println("Name: " + student.getName());
            System.out.println("Faculty: " + student.getFaculty());
            System.out.println("Department: " + student.getDepartment());
            System.out.println("----------");
        }

        return 0;
    }
}
