package store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.entity.Studentt;
import store.repository.StudentAffairRepo;

import java.util.List;

@Service
public class StudentAffairService {

    @Autowired
    private StudentAffairRepo studentAffairRepo;

    public boolean downloadAllDiplomas() {
        List<Studentt> studentList = studentAffairRepo.getAllStudents();
        boolean success = PdfGenerator.createStudentPdf(studentList);
        if (success) {
            System.out.println("SUCCESSFULL");
            return true;
        } else {
            System.out.println("UNSUCCESSFULL");
            return false;
        }
    }
}
