package store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import store.service.TranscriptService;

@RestController
@RequestMapping("/api")
public class TranscriptController {

    @Autowired
    private TranscriptService transcriptService;

    @PostMapping("/import-transcript")
    public ResponseEntity<?> importTranscript(@RequestParam("file") MultipartFile file) {
        try {
            transcriptService.importTranscript(file);
            return ResponseEntity.ok().body("Transcript successfully imported.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }
}
