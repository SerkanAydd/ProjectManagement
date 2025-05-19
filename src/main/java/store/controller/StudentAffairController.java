package store.controller;

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.noneDSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.service.StudentAffairService;
import java.io.File;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api")
public class StudentAffairController {

    @Autowired
    private StudentAffairService studentAffairService;

    @PostMapping("/download_all_diplomas")
    public ResponseEntity<?> downloadAllDiplomas() {
        boolean success = studentAffairService.downloadAllDiplomas();
        
        if (success) {
            File zipFile = new File("diplomas.zip");

            if (!zipFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Generated zip file not found.");
            }

            FileSystemResource resource = new FileSystemResource(zipFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diplomas.zip");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipFile.length())
                .body(resource);

        } else {
            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Diploma generation failed. Please check the logs or try again.");
        }
    }

    @PostMapping("/download_all_honor_certificates")
    public ResponseEntity<?> download_all_honor_certificates() {
        boolean success = studentAffairService.downloadAllHonorCertificates();
        
        if (success) {
            File zipFile = new File("honor_certificates.zip");

            if (!zipFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Generated zip file not found.");
            }

            FileSystemResource resource = new FileSystemResource(zipFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=honor_certificates.zip");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipFile.length())
                .body(resource);

        } else {
            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Honor certificates generation failed. Please check the logs or try again.");
        }
    }

    @PostMapping("/download_all_high_honor_certificates")
    public ResponseEntity<?> download_all_high_honor_certificates() {
        boolean success = studentAffairService.downloadAllHighHonorCertificates();
        
        if (success) {
            File zipFile = new File("high_honor_certificates.zip");

            if (!zipFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Generated zip file not found.");
            }

            FileSystemResource resource = new FileSystemResource(zipFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=high_honor_certificates.zip");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
            
            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipFile.length())
                .body(resource);

        } else {
            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("High honor certificates generation failed. Please check the logs or try again.");
        }
    }

    @PostMapping("/download_all_berat_certificate")
    public ResponseEntity<?> download_all_berat_certificates() {
        boolean success = studentAffairService.downloadAllBeratCertificates();
        return null;
    }
}

