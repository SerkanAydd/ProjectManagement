package store.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.entity.Student;
import store.service.AdvisorService;
import store.service.StudentService;

@RestController
@RequestMapping("/api")
public class ApprovedStudentsController
{
    @Autowired
    private AdvisorService advServ;

    @PostMapping("/viewApprovedStudentList")
    public ResponseEntity<?> AdvisorViewApprStdList(@RequestParam int aid)
    {
        try
        {
            List<Student> stds = advServ.getApprovedStudents(aid);
            return ResponseEntity.ok(stds);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected server error", "details", e.getMessage()));
        }
    }
}