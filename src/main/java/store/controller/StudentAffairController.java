package store.controller;
import store.entity.Studentt;

import java.io.File;
import org.springframework.http.ResponseEntity;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.service.StudentAffairService;


@RestController
@RequestMapping("/api")
public class StudentAffairController {

    @Autowired
    private StudentAffairService studentAffairService;

    @PostMapping("/download_all_diplomas")
    public ResponseEntity<?> downloadAllDiplomas() {
        boolean success = studentAffairService.downloadAllDiplomas();
        return sendFile("diplomas.zip", "attachment; filename=diplomas.zip", "Diplomas ", success);
    }

    @PostMapping("/download_all_honor_certificates")
    public ResponseEntity<?> download_all_honor_certificates() {
        boolean success = studentAffairService.download_honor_and_high_honor_certificates("Honor");
        return sendFile("honor_certificates.zip", "attachment; filename=honor_certificates.zip", "Honor Certificates ", success);
    }

    @PostMapping("/download_all_high_honor_certificates")
    public ResponseEntity<?> download_all_high_honor_certificates() {
        boolean success = studentAffairService.download_honor_and_high_honor_certificates("High Honor");
        return sendFile("high_honor_certificates.zip", "attachment; filename=high_honor_certificates.zip", "High Honor Certificates", success);
    }

    @PostMapping("/download_all_berat_certificate")
    public ResponseEntity<?> download_all_berat_certificates() {
        boolean success = studentAffairService.downloadAllBeratCertificates();
        return sendFile("Berat_Certificates.zip", "attachment; filename=Berat_Certificates.zip", "Berat Certificates", success);
    }

    private ResponseEntity<?> sendFile(String string1, String string2, String string3, boolean success) {
        if (success) {
            File zipFile = new File(string1);

            if (!zipFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Generated zip file not found.");
            }

            FileSystemResource resource = new FileSystemResource(zipFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, string2);
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipFile.length())
                .body(resource);

        } else {
            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(string3 + " generation failed. Please check the logs or try again.");
        }
    }

    @PostMapping("/approved-students")
    public ResponseEntity<List<Studentt>> getApprovedStudents() {
        try {
            List<Studentt> approvedStudents = studentAffairService.getApprovedStudents();

            if (approvedStudents.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }

            return ResponseEntity.ok(approvedStudents); // 200 OK + data
        } catch (Exception e) {
            System.err.println("Controller error: " + e.getMessage());
            return ResponseEntity.internalServerError().build(); // 500 Internal Server Error
        }
    }
}

