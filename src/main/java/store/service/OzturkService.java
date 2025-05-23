package store.service;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
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


public Map<String, Object> findCompletedCurriculumCourses(String studentName, String mail) {
    Long studentId = ozturkRepo.findStudentIdByName(studentName, mail);
    String department = ozturkRepo.findDepartmentByStudentId(studentId);
    Integer curriculumId = ozturkRepo.takeCurriculumid(department);

    List<String> curriculumCourses = ozturkRepo.viewCurriculumByCategory(curriculumId, "mandatory");
    List<String> studentCourses = ozturkRepo.findCourseCodesByStudentId(studentId);

    boolean completed = curriculumCourses.containsAll(studentCourses);
    return Map.of(
        "studentId", studentId,
        "completed", completed
    );
}





}
