package store.service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;

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

            // ⬇️ Debug çıktısı buraya
            System.out.println("Transcript Parsed:");
            System.out.println("Student ID: " + parsed.getStudentId());
            System.out.println("Courses: " + parsed.getCourseCodes());
            System.out.println("Total Credits: " + parsed.getTotalCredits());
            System.out.println("GPA: " + parsed.getGpa());
            System.out.println("Semester Count: " + parsed.getSemesterNumber());

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
}
