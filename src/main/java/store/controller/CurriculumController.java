package store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import store.service.CurriculumService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CurriculumController {

    @Autowired
    private CurriculumService curriculumService;

    @PostMapping("/import-curriculum")
    public ResponseEntity<?> importCurriculum(@RequestParam("file") MultipartFile file) {
        try {
            curriculumService.importCurriculum(file);
            return ResponseEntity.ok().body("Curriculum imported successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }
}
