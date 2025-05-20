package store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.entity.Studentt;
import store.entity.Transcript;
import store.repository.StudentAffairRepo;

import java.util.ArrayList;
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

    public boolean downloadAllHonorCertificates() {
        List<Transcript> transcriptList = studentAffairRepo.getAllTranscripts();
        List<Studentt> studentList = new ArrayList<>();

        for (Transcript transcript : transcriptList) {
            if (transcript.getGpa() >= 3.00 && transcript.getGpa() < 3.50) {
                Studentt student = studentAffairRepo.getStudentById(transcript.getStudentId());
                studentList.add(student);
            }
        }

        boolean success = HonorGenerator.createHonorCertificates(studentList);
        
        if (success) {
            System.out.println("SUCCESSFULL");
            return true;
        } else {
            System.out.println("UNSUCCESSFULL");
            return false;
        }
    }

    public boolean downloadAllHighHonorCertificates() {
        List<Transcript> transcriptList = studentAffairRepo.getAllTranscripts();
        List<Studentt> studentList = new ArrayList<>();

        for (Transcript transcript : transcriptList) {
            if (transcript.getGpa() >= 3.50) {
                Studentt student = studentAffairRepo.getStudentById(transcript.getStudentId());
                studentList.add(student);
            }
        }

        boolean success = HighHonorGenerator.createHighHonorCertificates(studentList);
        
        if (success) {
            System.out.println("SUCCESSFULL");
            return true;
        } else {
            System.out.println("UNSUCCESSFULL");
            return false;
        }
    }
}
