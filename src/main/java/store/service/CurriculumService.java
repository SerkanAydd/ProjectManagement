package store.service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import store.model.ParsedCurriculum;
import store.repository.CurriculumRepo;
import store.util.CurriculumParser;

@Service
public class CurriculumService {

    @Autowired
    private CurriculumRepo curriculumRepo;

    public void importCurriculum(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 50MB limit");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.contains("text") && !file.getOriginalFilename().endsWith(".csv") && !file.getOriginalFilename().endsWith(".txt")) {
            throw new IllegalArgumentException("Only .txt or .csv files are allowed. Detected file type: " + contentType);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches("^[A-Za-z]+_[A-Za-z]+_curriculum\\.(txt|csv)$")) {
            throw new IllegalArgumentException("Filename must be in format: faculty_department_curriculum.txt or faculty_department_curriculum.csv");
        }

        // Save the file locally
        File dir = new File("src/main/resources/curriculums");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File savedFile = new File(dir, originalFilename);
        try (FileOutputStream fos = new FileOutputStream(savedFile)) {
            fos.write(file.getBytes());
        }

        // Extract and parse
        ParsedCurriculum parsed = CurriculumParser.parse(file);

        // Check if curriculum already exists
        if (curriculumRepo.curriculumAlreadyExists(parsed.getFaculty(), parsed.getDepartment())) {
            throw new IllegalArgumentException("Curriculum for this faculty and department already exists.");
        }

        // Save curriculum
        int curriculumId = curriculumRepo.saveCurriculum(
                parsed.getFaculty(),
                parsed.getDepartment(),
                parsed.getTechElectiveCount(),
                parsed.getSocialElectiveCount(),
                java.sql.Date.valueOf(LocalDate.now())
        );

        // Save course rows
        List<String[]> courses = parsed.getCourses();
        for (String[] entry : courses) {
            String courseCode = entry[0];
            String category = entry[1];
            curriculumRepo.saveCourse(curriculumId, courseCode, category);
        }
    }
}
