package store.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.entity.Studentt;
import store.entity.Transcript;
import store.repository.StudentAffairRepo;

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

    public boolean downloadAllBeratCertificates() {
        /*
        List<Transcript> transcriptList = studentAffairRepo.getAllTranscripts();
        List<Double> gpas = new ArrayList<>();
        List<Studentt> institution_berat = new ArrayList<>();

        for (Transcript transcript : transcriptList) {
            gpas.add(transcript.getGpa());
        }

        Collections.sort(gpas, Collections.reverseOrder());

        for (int i = 0; i < 3; i++) {
            int studentid = studentAffairRepo.getStudentIdByGpa(gpas.get(i));
            Studentt student = studentAffairRepo.getStudentById(studentid);
            institution_berat.add(student);
        }

        boolean success1 = InstitutionBeratGenerator.createBeratCertificates(institution_berat);
         */

        List<String> distinctFaculties = studentAffairRepo.getDistinctFaculties();
        int count = distinctFaculties.size();
        System.out.println("Number of faculties: " + count);
        System.out.println("Faculties: " + distinctFaculties);

        return true;

    }
}

