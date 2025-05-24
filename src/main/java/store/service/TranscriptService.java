package store.service;
import store.entity.Transcriptt;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import store.model.ParsedTranscript;
import store.repository.TranscriptRepo;
import store.util.TranscriptParser;

@Service
public class TranscriptService {

    @Autowired
    private TranscriptRepo transcriptRepo;

    public void importTranscript(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 50MB limit");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed. Received file type: " + contentType);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches("^\\d+_transcript\\.pdf$")) {
            throw new IllegalArgumentException("Filename must be in format: studentId_transcript.pdf");
        }

        int studentId = Integer.parseInt(originalFilename.split("_")[0]);

        if (transcriptRepo.transcriptAlreadyExists(studentId)) {
            throw new IllegalArgumentException("Transcript for this student already exists.");
        }

        File savedFile = new File("src/main/resources/transcripts/" + originalFilename);
        try (FileOutputStream fos = new FileOutputStream(savedFile)) {
            fos.write(file.getBytes());
        }

        String text;
        try (PDDocument document = PDDocument.load(savedFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read PDF file: " + e.getMessage());
        }

        ParsedTranscript parsed;
        try {
            parsed = TranscriptParser.parseTranscript(text);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to parse transcript: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while parsing transcript: " + e.getMessage());
        }

        try {
            transcriptRepo.saveTranscript(
                    studentId,
                    parsed.getTotalCredits(),
                    parsed.getGpa(),
                    String.join(",", parsed.getCourseCodes()),
                    parsed.getSemesterNumber(),
                    java.sql.Date.valueOf(LocalDate.now())
            );

            for (String courseCode : parsed.getCourseCodes()) {
                transcriptRepo.saveCourse(studentId, courseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save transcript data to database: " + e.getMessage());
        }
    }
    public List<Transcriptt> getAllTranscripts() {
        try {
            List<Transcriptt> transcripts = transcriptRepo.getAllTranscripts();

            // İlk 3 transcript'i yazdır
            for (int i = 0; i < Math.min(3, transcripts.size()); i++) {
                System.out.println("Transcript " + (i + 1) + ": " + transcripts.get(i));
            }
            return transcripts;
        } catch (Exception e) {
            // Hata durumunda loglama yapılabilir
            System.err.println("Error while fetching transcripts: " + e.getMessage());
            return new ArrayList<>(); // Boş liste dönerek uygulamanın çökmesini engeller
        }
    }
}
