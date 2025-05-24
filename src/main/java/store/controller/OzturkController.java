package store.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.service.OzturkService;

@RestController
@RequestMapping("/api")
public class OzturkController {

    @Autowired
    private OzturkService ozturkService;

    @PostMapping("/viewCurriculum")
    public ResponseEntity<?> vievCurriculum(@RequestParam String department) {
        try {
            List<Map<String, Object>> userMap = ozturkService.vievCurriculum(department);
            return ResponseEntity.ok(userMap);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to load \r\n" + //
                                                "curriculum. \r\n" + //
                                                "Please try again \r\n" + //
                                                "later.",
                    "details", e.getMessage()
            ));
        }
    }
        @GetMapping("/students/by-advisor/{advisorMail}")
        public ResponseEntity<List<Map<String, String>>> getStudentsByAdvisor(@PathVariable String advisorMail) {
            List<Map<String, String>> students = ozturkService.getStudentsByAdvisor(advisorMail);
            return ResponseEntity.ok(students);
        }

   @PostMapping("/update-approvals")
public ResponseEntity<?> updateApprovalsBulk(
        @RequestParam String staffMail,
        @RequestBody List<Map<String, String>> updates) {

    try {
        int updatedCount = ozturkService.updateMultipleStudentStatuses(staffMail, updates);

        return ResponseEntity.ok(Map.of(
            "updated", updatedCount,
            "message", "Approval statuses updated successfully"
        ));

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", "Unexpected error",
            "details", e.getMessage()
        ));
    }
}

@GetMapping("/check-transcript/{studentNo}")
public ResponseEntity<Map<String, Object>> checkTranscriptCurriculumMatches(
        @PathVariable Long studentNo) {
    try {
        Map<String, Object> result = ozturkService.findCompletedCurriculumCourses(studentNo);

        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", "Something went wrong",
            "details", e.getMessage()
        ));
    }
}

    

}
