package store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import store.entity.Student;
import store.repository.StudentRepository;


@Service
public class StudentService 
{
    @Autowired
    private StudentRepository stdRepo;

    public List<Student> getRankedStudentListByDepartment(String departmentName)
    {
        List<Student> rankStudents = stdRepo.getRankedStudentListByDepartment(departmentName);

        if (rankStudents == null || rankStudents.isEmpty())
        {
            throw new RuntimeException("No students found for ranking in this department, department name: >" + departmentName);
        }
        return (rankStudents);
    }

    public List<Student> getRankedStudentListByFaculty(String facultyName)
    {
        List<Student> rankStudents = stdRepo.getRankedStudentListByFaculty(facultyName);

        if (rankStudents == null || rankStudents.isEmpty())
        {
            throw new RuntimeException("No students found for ranking in this faculty, faculty name: >" + facultyName);
        }
        return (rankStudents);
    }

    public List<Student> getRankedStudentList() 
    {
        List<Student> rankStudents = stdRepo.getRankedStudentList();

        if (rankStudents == null || rankStudents.isEmpty())
        {
            throw new RuntimeException("No students found for ranking");
        }
        return (rankStudents);
    }

    public List<Student> getStudentRankingListByDepartment(String departmentName)
    {
        List<Student> students = stdRepo.getStudentListByDeparment(departmentName);
    
        if (students == null || students.isEmpty()) 
        {
            throw new RuntimeException("No students found for department: >" + departmentName);
        }
        return (students);
    }

    public List<Student> StudentsEligibleforGraduation()
    {
        List<Student> students = stdRepo.getStudentListEligable();
    
        if (students == null || students.isEmpty()) 
        {
            throw new RuntimeException("No students found for Eligible to graduate");
        }
        return (students);
    }
}
