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

import store.repository.UserRepo;
import store.util.JwtUtil;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepo userInfo;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, String> authenticateUser(String mail, String password) {
        System.out.println(new BCryptPasswordEncoder().encode("itispassword"));
        Map<String, String> response = new HashMap<>();
        String storedHash = userInfo.getPassword(mail);

        System.out.println("DEBUG: getPassword returned => " + storedHash);

        if (storedHash == null) {
            response.put("error", "User not found: No user registered with this email");
            return response;
        }

        System.out.println("Password entered   : [" + password + "]");
        System.out.println("Password from DB   : [" + storedHash + "]");
        System.out.println("Match result       : " + passwordEncoder.matches(password, storedHash));

        if (!passwordEncoder.matches(password, storedHash)) {
            response.put("error", "Incorrect password");
            return response;
        }

        String role = userInfo.getRole(mail);
        String id = userInfo.getId(mail);
        String name = userInfo.getName(mail);
        String token = jwtUtil.generateToken(mail, role);

        Map<String, String> userMap = new HashMap<>();
        userMap.put("role", role);
        userMap.put("mail", mail);
        userMap.put("id", id);
        userMap.put("token", token);
        userMap.put("name", name);

        return userMap;
    }

    public boolean isTokenValid(String token) {
        String extractedMail = jwtUtil.extractMail(token);
        return jwtUtil.isTokenValid(token, extractedMail);
    }

    public Map<String, String> register_student(String mail, String name, String faculty, String department, String password) {
        Resource resource = new ClassPathResource("student_emails.txt");
        Map<String, String> registerMessage = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(mail)) {
                    if (userInfo.getId(mail) != null) {
                        registerMessage.put("Successful", "False");
                        registerMessage.put("Message", "User already registered.");
                        return registerMessage;
                    } else {
                        int id = userInfo.findMaxStudentId() + 1;
                        String hashedPassword = passwordEncoder.encode(password);
                        userInfo.register_student(id, mail, name, faculty, department, hashedPassword);
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
        registerMessage.put("Message", "User mail is not in database");
        return registerMessage;
    }

    public Map<String, String> register_staff(String mail, String name, String title, String faculty, String department, String password) {
        Resource resource = new ClassPathResource("StaffMails.txt");
        Map<String, String> registerMessage = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(mail)) {
                    if (userInfo.getId(mail) != null) {
                        registerMessage.put("Successful", "False");
                        registerMessage.put("Message", "User already registered.");
                        return registerMessage;
                    } else {
                        int id = userInfo.findMaxStaffId() + 1;
                        String hashedPassword = passwordEncoder.encode(password);
                        userInfo.register_staff(id, mail, name, title, faculty, department, hashedPassword);
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
        registerMessage.put("Message", "User mail is not in database");
        return registerMessage;
    }
}
