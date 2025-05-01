package store.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import store.repository.UserRepo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepo userInfo;

    public Map<String, String> authenticateUser(String mail, String password) {

        String storedHash = userInfo.getPassword(mail);
        if (storedHash == null){
            return null;
        }

        if (passwordEncoder.matches(password, storedHash)) {
            String role = userInfo.getRole(mail);
            String id = userInfo.getId(mail);
            String token = userInfo.getToken(mail);
            String name = userInfo.getName(mail);

            Map<String, String> userMap = new HashMap<>();
            userMap.put("role", role);
            userMap.put("mail", mail);
            userMap.put("id", id);
            userMap.put("token", token);
            userMap.put("name", name);

            return userMap;

        } else {
            System.out.println("Password Incorrect");

            return null;
        }
    }

    public boolean register_student(String mail, String name, String faculty, String department) {
        Resource resource = new ClassPathResource("student_emails.txt");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if (line.equals(mail)) {
                    return userInfo.register_student(mail, name, faculty, department);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Map<String, String> register_staff(String mail, String name,String title, String faculty, String department, String password){
        Resource resource = new ClassPathResource("StaffMails.txt");
        boolean flag = false;
        Map<String, String> registerMessage = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(mail)) {
                    if(userInfo.getStaffId(mail) != null){
                        registerMessage.put("Successful","False");
                        registerMessage.put("Message", "User already registered.");
                        return registerMessage;
                    }else{
                        int id = userInfo.findMaxStaffId()+1;
                        String hashedPassword = passwordEncoder.encode(password);
                        userInfo.register_staff(id,mail, name, title,faculty, department, hashedPassword);
                        registerMessage.put("Successful", "True");
                        registerMessage.put("Message", "User successfully registered.");
                        return registerMessage;
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        registerMessage.put("Successful", "False");
        registerMessage.put("Message","User mail is not in database" );
        return registerMessage;

    }
}

