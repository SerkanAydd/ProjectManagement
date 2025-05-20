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

    public List<String> vievCurriculum(String department) {
        Integer curriculumId = ozturkRepo.takeCurriculumid(department);
        if (curriculumId == null) {
            return Collections.singletonList("Curriculum does not exist");
        }
        List<String> curriculum =ozturkRepo.viewCurriculum(curriculumId);
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

    public boolean findCompletedCurriculumCourses(Long studentId) {
    String department = ozturkRepo.findDepartmentByStudentId(studentId);

    Integer curriculumId = ozturkRepo.takeCurriculumid(department);

    List<String> curriculumCourses = ozturkRepo.viewCurriculum(curriculumId);

    List<String> studentCourses = ozturkRepo.findCourseCodesByStudentId(studentId);

    boolean completed =  studentCourses.containsAll(curriculumCourses);

    return completed;
}


}
