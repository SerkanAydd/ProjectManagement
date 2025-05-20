package store.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tomcat.util.descriptor.tagplugin.TagPluginParser;
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
        
        List<Boolean> bool_array = new ArrayList<>();
        
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
        bool_array.add(success1);
         
        List<String> distinctFaculties = studentAffairRepo.getDistinctFaculties();
        
        for (String distinctFaculty : distinctFaculties) {
            List<Studentt> studentList_from_same_faculty = studentAffairRepo.getAllStudentsByFaculty(distinctFaculty);
            List<Double> new_gpas = new ArrayList<>();

            for (Studentt student : studentList_from_same_faculty) {
                int studentid = student.getStudentid();
                new_gpas.add(studentAffairRepo.getGpaById(studentid));
            }
            
            Collections.sort(new_gpas, Collections.reverseOrder());

            List<Studentt> top_three_faculty = new ArrayList<>();
                        
            for (int i = 0; i < 3; i++) {
                double a_gpa = new_gpas.get(i);
                int student_id = studentAffairRepo.getStudentIdByGpa(a_gpa);
                Studentt studentt = studentAffairRepo.getStudentById(student_id);
                top_three_faculty.add(studentt);
            }
            
            boolean successfull = FacultyBeratGenerator.createBeratCertificates(distinctFaculty, top_three_faculty);
            bool_array.add(successfull);
        }

        List<String> distinctDepartments = studentAffairRepo.getDistinctDepartments();
        
        for (String distinctDepartment : distinctDepartments) {
            List<Studentt> studentList_from_same_department = studentAffairRepo.getAllStudentsByDepartment(distinctDepartment);
            List<Double> new_gpas2 = new ArrayList<>();

            for (Studentt student : studentList_from_same_department) {
                int studentid2 = student.getStudentid();
                new_gpas2.add(studentAffairRepo.getGpaById(studentid2));
            }
            
            Collections.sort(new_gpas2, Collections.reverseOrder());

            List<Studentt> top_three_department = new ArrayList<>();
                        
            for (int i = 0; i < 3; i++) {
                double a_gpa2 = new_gpas2.get(i);
                int student_id2 = studentAffairRepo.getStudentIdByGpa(a_gpa2);
                Studentt studentt2 = studentAffairRepo.getStudentById(student_id2);
                top_three_department.add(studentt2);
            }
            
            boolean successfull2 = DepartmentBeratGenerator.createBeratCertificates(distinctDepartment, top_three_department);
            bool_array.add(successfull2);
        }

        List<String> final_zip = new ArrayList<>();
        final_zip.add("instutition_berat_certificates.zip");

        for (String distinctFaculty : distinctFaculties) {
            final_zip.add(distinctFaculty + "_berat_certificates.zip");
        }

        for (String distinctDepartment : distinctDepartments ) {
            final_zip.add(distinctDepartment + "_berat_certificates.zip");
        }

        for (String file_name : final_zip) {
            System.out.println(file_name);
        }

        createFolder("Berat_Certificates");
        
        return true;
    }

    
    private boolean createFolder(String fileName) {
        String folderName = fileName;
        File folder = new File(folderName);
        
        if (folder.exists() && folder.isDirectory()) {
            deleteFolderRecursively(folder);
            System.out.println("Existing folder deleted.");
        } else {
            System.out.println("No existing folder found.");
        }

        String folderNameWithZip = "diplomas.zip";
        File folderWithZip = new File(folderNameWithZip);

        if (folderWithZip.exists()) {
            folderWithZip.delete();
            System.out.println("Existing ZIP file deleted.");
        }


        boolean success = folder.mkdir();   
        if (!success) {
            System.out.println("Does here return ?");
            return false;
        } else {
            return true;
        }
    }

    public static void deleteFolderRecursively(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolderRecursively(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    } 

}

