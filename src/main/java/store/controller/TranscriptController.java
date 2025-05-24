package store.controller;
import store.entity.Transcriptt;
import store.model.BatchTranscriptResult;
import java.util.List;
import java.util.ArrayList;
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

    @PostMapping("/import-transcripts-zip")
    public ResponseEntity<?> importTranscriptsFromZip(@RequestParam("file") MultipartFile file) {
        try {
            BatchTranscriptResult result = transcriptService.importTranscriptsFromZip(file);
            
            if (result.getSuccessCount() == 0) {
                return ResponseEntity.badRequest().body(result);
            } else if (result.getFailureCount() > 0) {
                // Partial success - some files processed successfully, some failed
                return ResponseEntity.ok().body(result);
            } else {
                // All files processed successfully
                return ResponseEntity.ok().body(result);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/view_transcript")
    public ResponseEntity<List<Transcriptt>> viewTranscripts() {
        List<Transcriptt> transcripts = transcriptService.getAllTranscripts();

        if (transcripts.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(transcripts); // 200 OK + data
        }
    }
}
