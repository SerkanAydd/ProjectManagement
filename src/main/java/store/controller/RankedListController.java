package store.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import store.entity.Student;
import store.service.StudentService;

@RestController
@RequestMapping("/api")
public class RankedListController
{
    @Autowired
    private StudentService studentService;

    @PostMapping("/viewRankedListUniverstySA")
    public ResponseEntity<?> viewUnversityRankedList()
    {
        try
        {
            List<Student> stds = studentService.getRankedStudentList();
            return ResponseEntity.ok(stds);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected server error", "details", e.getMessage()));
        }
    }

    @PostMapping("/viewRankedListFacultySA")
    public ResponseEntity<?> viewFacultyRankedList(String facultyName)
    {
        try
        {
            List<Student> stds = studentService.getRankedStudentListByFaculty(facultyName);
            return ResponseEntity.ok(stds);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected server error", "details", e.getMessage()));
        }
    }

    @PostMapping("/viewRankedListDepartmentSA")
    public ResponseEntity<?> viewDepartmentRankedList(String departmentName)
    {
        try
        {
            List<Student> stds = studentService.getRankedStudentListByDepartment(departmentName);
            return ResponseEntity.ok(stds);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected server error", "details", e.getMessage()));
        }
    }         
}