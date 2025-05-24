package store.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import store.repository.OzturkRepo;
import store.util.JwtUtil;

@Service
public class OzturkService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private OzturkRepo ozturkRepo;

    @Autowired
    private JwtUtil jwtUtil;

    public List<Map<String, Object>>vievCurriculum(String department) {
        Integer curriculumId = ozturkRepo.takeCurriculumid(department);
        if (curriculumId == null) {
            return Collections.singletonList(Map.of("error", "Curriculum does not exist"));

        }
        List<Map<String, Object>> curriculum =ozturkRepo.viewCurriculum(curriculumId);
        return curriculum;
        }

    public List<Map<String, String>> getStudentsByAdvisor(String advisorMail) {
        return ozturkRepo.findStudentNamesAndApprovalsByAdvisorMail(advisorMail);
    }
    public int updateMultipleStudentStatuses(String staffMail, List<Map<String, String>> updates) {
        for (Map<String, String> update : updates) {
            String status = update.get("status");
            if (!status.equals("Approved") && !status.equals("Rejected")) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }

        return ozturkRepo.updateGraduationStatusPairs(staffMail, updates);
    }

public Map<String, Object> findCompletedCurriculumCourses(Long studentId) {
    System.out.println("Student ID: " + studentId);

    String department = ozturkRepo.findDepartmentByStudentId(studentId);
    System.out.println("Department: " + department);

    Integer curriculumId = ozturkRepo.takeCurriculumid(department);
    System.out.println("Curriculum ID: " + curriculumId);

    Map<String, Integer> elective = ozturkRepo.takeTechElectiveAndSocialElective(department);
    int tech_elective_count = (int) elective.get("tech_elective_count");
    int social_elective_count = (int) elective.get("social_elective_count");

    System.out.println("Expected Technical Electives: " + tech_elective_count);
    System.out.println("Expected Social Electives: " + social_elective_count);

    List<String> social_elective = ozturkRepo.viewCurriculumByCategory(0, "social_elective");
    List<String> technical_elective = ozturkRepo.viewCurriculumByCategory(curriculumId, "technical_elective");

    System.out.println("Social Elective Courses in Curriculum: " + social_elective);
    System.out.println("Technical Elective Courses in Curriculum: " + technical_elective);

    List<String> curriculumCourses = ozturkRepo.viewCurriculumByCategory(curriculumId, "mandatory");
    System.out.println("Mandatory Curriculum Courses: " + curriculumCourses);

    List<String> studentCourses = ozturkRepo.findCourseCodesByStudentId(studentId);
    System.out.println("Courses Taken by Student: " + studentCourses);

    // Ana zorunlu dersler tamam mı?
    boolean completed = studentCourses.containsAll(curriculumCourses);
    System.out.println("Mandatory courses completed: " + completed);

    Set<String> normalizedSocial = social_elective.stream()
        .map(s -> s.trim().toUpperCase())
        .collect(Collectors.toSet());

    Set<String> normalizedTech = technical_elective.stream()
        .map(s -> s.trim().toUpperCase())
        .collect(Collectors.toSet());

    List<String> normalizedStudentCourses = studentCourses.stream()
        .map(s -> s.trim().toUpperCase())
        .collect(Collectors.toList());

    System.out.println("Normalized Social Elective Codes: " + normalizedSocial);
    System.out.println("Normalized Technical Elective Codes: " + normalizedTech);
    System.out.println("Normalized Student Courses: " + normalizedStudentCourses);

    long socialTaken = normalizedStudentCourses.stream()
        .filter(normalizedSocial::contains)
        .count();

    long techTaken = normalizedStudentCourses.stream()
        .filter(normalizedTech::contains)
        .count();

    boolean socialMatch = socialTaken >= social_elective_count;
    boolean techMatch = techTaken >= tech_elective_count;

    System.out.println("Social Electives Taken: " + socialTaken + " → Match: " + socialMatch);
    System.out.println("Technical Electives Taken: " + techTaken + " → Match: " + techMatch);

    completed = completed && socialMatch && techMatch;
    System.out.println("Curriculum Completed: " + completed);


    return Map.of(
        "studentId", studentId,
        "completed", completed
    );
}





}
