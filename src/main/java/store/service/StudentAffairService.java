package store.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.*;

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
        return success;
    }

    public boolean download_honor_and_high_honor_certificates(String string) {
        List<Transcript> transcriptList = studentAffairRepo.getAllTranscripts();
        List<Studentt> studentList_honor = new ArrayList<>();
        List<Studentt> studentList_high_honor = new ArrayList<>();

        for (Transcript transcript : transcriptList) {
            if (transcript.getGpa() >= 3.50) {
                Studentt student = studentAffairRepo.getStudentById(transcript.getStudentId());
                if (student != null) {
                    studentList_high_honor.add(student);
                }
            } else if (transcript.getGpa() >= 3.00) {
                Studentt student = studentAffairRepo.getStudentById(transcript.getStudentId());
                if (student != null) {
                    studentList_honor.add(student);
                }
            }
        }

        if (string.equals("High Honor")) {
            return HighHonorGenerator.createHighHonorCertificates(studentList_high_honor);
        } else if (string.equals("Honor")) {
            return HonorGenerator.createHonorCertificates(studentList_honor);
        } else {
            return false;
        }
    }

    public boolean downloadAllBeratCertificates() {
        List<Boolean> bool_array = new ArrayList<>();
        List<Boolean> faculty_bool_array = createFacultyBerat();
        List<Boolean> department_bool_array = createDepartmentBerat();
        List<String> distinctFaculties = studentAffairRepo.getDistinctFaculties();
        List<String> distinctDepartments = studentAffairRepo.getDistinctDepartments();

        bool_array.add(createInstitutionBerat());

        for (boolean bool_value : faculty_bool_array) {
            bool_array.add(bool_value);
        }

        for (boolean bool_value : department_bool_array) {
            bool_array.add(bool_value);
        }
        
        List<String> final_zip = new ArrayList<>();
        final_zip.add("instutition_berat_certificates.zip");

        for (String distinctFaculty : distinctFaculties) {
            final_zip.add(distinctFaculty + "_berat_certificates.zip");
        }

        for (String distinctDepartment : distinctDepartments ) {
            final_zip.add(distinctDepartment + "_berat_certificates.zip");
        }

        createFolder("Berat_Certificates");
        Path targetPath = Paths.get("Berat_Certificates");
        
        try {

            for (String fileName : final_zip) {
                Path sourcePath = Paths.get(fileName);
                Path destinationPath = targetPath.resolve(fileName);

                Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved: " + fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean success = zipFolder("Berat_Certificates");
        bool_array.add(success);

        boolean flag = true;
        for (boolean bool_value : bool_array) {
            if (!bool_value) {
                flag = false;
            }
        }

        return flag;
    }
    
    private boolean createInstitutionBerat() {
        List<Transcript> transcriptList = studentAffairRepo.getAllTranscripts();
        List<Double> gpas = new ArrayList<>();
        
        for (Transcript transcript : transcriptList) {
            gpas.add(transcript.getGpa());
        }

        Collections.sort(gpas, Collections.reverseOrder());

        List<Studentt> institution_berat = get_first_three(gpas);

        return InstitutionBeratGenerator.createBeratCertificates(institution_berat);
    }

    private List<Boolean> createFacultyBerat() {
        List<String> distinctFaculties = studentAffairRepo.getDistinctFaculties();
        List<Boolean> bool_array = new ArrayList<>();

        for (String distinctFaculty : distinctFaculties) {

            List<Studentt> studentList_from_same_faculty = studentAffairRepo.getAllStudentsByFaculty(distinctFaculty);
            List<Double> gpas = new ArrayList<>();

            for (Studentt student : studentList_from_same_faculty) {
                int studentid = student.getStudentid();
                double gpa = studentAffairRepo.getGpaById(studentid);
                if (gpa >= 0.0) {
                    gpas.add(gpa);
                }
            }
            
            Collections.sort(gpas, Collections.reverseOrder());

            List<Studentt> top_three_faculty = get_first_three(gpas);
            
            boolean success = FacultyBeratGenerator.createBeratCertificates(distinctFaculty, top_three_faculty);
            bool_array.add(success);
        }

        return bool_array;
    }

    private List<Boolean> createDepartmentBerat() {
        List<String> distinctDepartments = studentAffairRepo.getDistinctDepartments();
        List<Boolean> bool_array = new ArrayList<>();

        for (String distinctDepartment : distinctDepartments) {
            List<Studentt> studentList_from_same_department = studentAffairRepo.getAllStudentsByDepartment(distinctDepartment);
            List<Double> gpas = new ArrayList<>();

            for (Studentt student : studentList_from_same_department) {
                int studentid = student.getStudentid();
                double gpa = studentAffairRepo.getGpaById(studentid);
                if (gpa >= 0) {
                    gpas.add(gpa);
                }
            }
            
            Collections.sort(gpas, Collections.reverseOrder());

            List<Studentt> top_three_department = get_first_three(gpas);
            
            boolean success = DepartmentBeratGenerator.createBeratCertificates(distinctDepartment, top_three_department);
            bool_array.add(success);
        }

        return bool_array;
    }

    private List<Studentt> get_first_three(List<Double> gpas) {
        List<Studentt> list_to_return = new ArrayList<>();

        int gpas_size = gpas.size();
        int threshold = 0;
        if (gpas_size > 3) {
            threshold = 3;
        } else {
            threshold = gpas_size;
        }

        for (int i = 0; i < threshold; i++) {
            List<Integer> studentids = studentAffairRepo.getStudentIdsByGpa(gpas.get(i));
            int size = studentids.size();
            if (size == 0) {
                continue;
            } else if (size == 1) {
                int studentid = studentids.get(0);
                Studentt student = studentAffairRepo.getStudentById(studentid);
                list_to_return.add(student);
            } else if (size > 1) {
                for (int studentid : studentids) {
                    Studentt student = studentAffairRepo.getStudentById(studentid);
                    list_to_return.add(student);
                }
            }
        }

        return list_to_return;
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

    private boolean zipFolder(String folderName) {
        Path sourceDir = Paths.get(folderName);
        String zipFileName = folderName + ".zip";

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFileName))) {
            Files.walk(sourceDir)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                    try {
                        zipOut.putNextEntry(zipEntry);
                        Files.copy(path, zipOut);
                        zipOut.closeEntry();
                    } catch (IOException e) {
                        System.err.println("Failed to zip file: " + path + " due to " + e);
                    }
                });
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Studentt> getApprovedStudents() {
        try {
            return studentAffairRepo.getApprovedStudents();
        } catch (Exception e) {
            System.err.println("Error while fetching approved students: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

