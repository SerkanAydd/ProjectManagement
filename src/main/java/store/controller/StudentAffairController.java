package store.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
        studentAffairService.downloadAllDiplomas();
        return null;
    }
}
