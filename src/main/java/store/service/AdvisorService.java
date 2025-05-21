package store.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.repository.StudentRepository;
import store.repository.AdvisorRepository;
import java.util.List;
import store.entity.*;;

@Service
public class AdvisorService
{
    @Autowired
    private StudentRepository stdInf;

    @Autowired
    private AdvisorRepository advInf;

    public List<Student> getAssignedStudents(String advMail)
    {
        int advId = advInf.getAdvisorId(advMail);
        List<Student> students = advInf.findStudentsByAdvisor(advId);
        if (students == null || students.isEmpty()) 
        {
            throw new RuntimeException("No students found for advisor: " + advId);
        }
        return students;
    }

    public List<Student> getApprovedStudents(String advMail)
    {
        int advId = advInf.getAdvisorId(advMail);
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
