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

        String outputPath = "students.pdf";
        PdfGenerator.createStudentPdf(studentList, outputPath);

        return 0;
    }
}
