package store.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import store.repository.StudentRepository;
import store.repository.AdvisorRepository;
import store.util.JwtUtil;
import java.util.List;
import store.entity.*;;

@Service
public class AdvisorService
{
    @Autowired
    private StudentRepository stdInf;

    @Autowired
    private AdvisorRepository advInf;

    public List<Student> getAssignedStudents(int advId)
    {
        List<Student> students = advInf.findStudentsByAdvisor(advId);
        if (students == null || students.isEmpty()) 
        {
            throw new RuntimeException("No students found for advisor: " + advId);
        }
        return students;
    }

    public List<Student> getApprovedStudents(int advId)
    {
        List<Student> students = advInf.findApprovedStudentsByAdvisor(advId);
        if (students == null || students.isEmpty()) 
        {
            throw new RuntimeException("No students found for advisor: " + advId);
        }
        return students;                
    }


    public boolean rejectGraduation(int advId, int stdId) 
    {
        return advInf.changeGraduationStatus(advId, stdId, "rejected");
    }

    public boolean approveGraduation(int advId, int stdId)
    {
        return advInf.changeGraduationStatus(advId, stdId, "approved");
    }
}
