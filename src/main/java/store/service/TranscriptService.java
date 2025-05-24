package store.service;
import store.entity.Transcriptt;
import store.model.BatchTranscriptResult;
import store.model.BatchTranscriptResult.TranscriptProcessResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
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

    public BatchTranscriptResult importTranscriptsFromZip(MultipartFile file) throws Exception {
        // Validate ZIP file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > 100 * 1024 * 1024) { // 100MB limit for ZIP files
            throw new IllegalArgumentException("File size exceeds 100MB limit");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        if (contentType == null || (!contentType.equals("application/zip") && !contentType.equals("application/x-zip-compressed"))) {
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".zip")) {
                throw new IllegalArgumentException("Only ZIP files are allowed. Received file type: " + contentType);
            }
        }

        BatchTranscriptResult result = new BatchTranscriptResult();
        List<TranscriptFileData> transcriptFiles = extractTranscriptsFromZip(file);
        
        result.setTotalFiles(transcriptFiles.size());
        
        if (transcriptFiles.isEmpty()) {
            throw new IllegalArgumentException("No valid transcript PDF files found in the ZIP archive");
        }

        int successCount = 0;
        int failureCount = 0;

        // Create transcripts directory if it doesn't exist
        File transcriptsDir = new File("src/main/resources/transcripts");
        if (!transcriptsDir.exists()) {
            transcriptsDir.mkdirs();
        }

        for (TranscriptFileData transcriptFile : transcriptFiles) {
            try {
                processIndividualTranscript(transcriptFile);
                result.addResult(new TranscriptProcessResult(
                    transcriptFile.getFilename(), 
                    true, 
                    "Successfully imported transcript", 
                    transcriptFile.getStudentId()
                ));
                successCount++;
            } catch (Exception e) {
                result.addResult(new TranscriptProcessResult(
                    transcriptFile.getFilename(), 
                    false, 
                    e.getMessage()
                ));
                failureCount++;
            }
        }

        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);

        return result;
    }

    private List<TranscriptFileData> extractTranscriptsFromZip(MultipartFile zipFile) throws IOException {
        List<TranscriptFileData> transcriptFiles = new ArrayList<>();
        
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFile.getBytes()))) {
            ZipEntry entry;
            
            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                
                // Skip directories and non-PDF files
                if (entry.isDirectory() || !fileName.toLowerCase().endsWith(".pdf")) {
                    continue;
                }
                
                // Extract just the filename without directory path
                fileName = new File(fileName).getName();
                
                // Validate filename format
                if (!fileName.matches("^\\d+_transcript\\.pdf$")) {
                    continue; // Skip files that don't match the pattern
                }
                
                // Read file content
                byte[] content = zis.readAllBytes();
                
                if (content.length == 0) {
                    continue; // Skip empty files
                }
                
                // Extract student ID from filename
                int studentId = Integer.parseInt(fileName.split("_")[0]);
                
                transcriptFiles.add(new TranscriptFileData(fileName, content, studentId));
            }
        }
        
        return transcriptFiles;
    }

    private void processIndividualTranscript(TranscriptFileData transcriptFile) throws Exception {
        // Check if transcript already exists
        if (transcriptRepo.transcriptAlreadyExists(transcriptFile.getStudentId())) {
            throw new IllegalArgumentException("Transcript for student " + transcriptFile.getStudentId() + " already exists");
        }

        // Save file to disk
        File savedFile = new File("src/main/resources/transcripts/" + transcriptFile.getFilename());
        try (FileOutputStream fos = new FileOutputStream(savedFile)) {
            fos.write(transcriptFile.getContent());
        }

        // Extract text from PDF
        String text;
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(transcriptFile.getContent()))) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read PDF file: " + e.getMessage());
        }

        // Parse transcript
        ParsedTranscript parsed;
        try {
            parsed = TranscriptParser.parseTranscript(text);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to parse transcript: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while parsing transcript: " + e.getMessage());
        }

        // Validate student ID matches filename
        if (parsed.getStudentId() != transcriptFile.getStudentId()) {
            throw new IllegalArgumentException("Student ID in transcript (" + parsed.getStudentId() + 
                ") does not match filename (" + transcriptFile.getStudentId() + ")");
        }

        // Save to database
        try {
            transcriptRepo.saveTranscript(
                    transcriptFile.getStudentId(),
                    parsed.getTotalCredits(),
                    parsed.getGpa(),
                    String.join(",", parsed.getCourseCodes()),
                    parsed.getSemesterNumber(),
                    java.sql.Date.valueOf(LocalDate.now())
            );

            for (String courseCode : parsed.getCourseCodes()) {
                transcriptRepo.saveCourse(transcriptFile.getStudentId(), courseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save transcript data to database: " + e.getMessage());
        }
    }

    // Helper class to hold transcript file data
    private static class TranscriptFileData {
        private final String filename;
        private final byte[] content;
        private final int studentId;

        public TranscriptFileData(String filename, byte[] content, int studentId) {
            this.filename = filename;
            this.content = content;
            this.studentId = studentId;
        }

        public String getFilename() {
            return filename;
        }

        public byte[] getContent() {
            return content;
        }

        public int getStudentId() {
            return studentId;
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
