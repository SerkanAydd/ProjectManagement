package store.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import store.model.ParsedCurriculum;

public class CurriculumParser {

    public static ParsedCurriculum parse(MultipartFile file) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;

        Map<String, String> meta = new HashMap<>();
        List<String[]> courses = new ArrayList<>();

        boolean courseSectionStarted = false;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (!courseSectionStarted) {
                if (line.toLowerCase().startsWith("courses")) {
                    courseSectionStarted = true;
                    continue;
                }

                String[] parts = line.split("[:;,]", 2);
                if (parts.length == 2) {
                    meta.put(parts[0].trim().toLowerCase(), parts[1].trim());
                }

            } else {
                // Skip header row like "Code,Category" or "Code;Category"
                if (line.toLowerCase().contains("code") && line.toLowerCase().contains("category")) {
                    continue;
                }

                String[] parts = line.split("[;,]", 2);
                if (parts.length == 2) {
                    courses.add(new String[]{parts[0].trim(), parts[1].trim()});
                }
            }
        }

        return new ParsedCurriculum(
                meta.get("faculty"),
                meta.get("department"),
                Integer.parseInt(meta.get("tech_elective_count")),
                Integer.parseInt(meta.get("social_elective_count")),
                courses
        );
    }
}
