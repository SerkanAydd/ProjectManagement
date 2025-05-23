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
    private EmailService emailService;
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private UserRepo userInfo;

    @Autowired
    private JwtUtil jwtUtil;
    private final Map<String, PendingRegistration> pendingStudentRegistrations = new HashMap<>();

    // PendingRegistration: mail → bilgiler + hashed password
    static class PendingRegistration {
        String name;
        String faculty;
        String department;
        String hashedPassword;

        public PendingRegistration(String name, String faculty, String department, String hashedPassword) {
            this.name = name;
            this.faculty = faculty;
            this.department = department;
            this.hashedPassword = hashedPassword;
        }
    }

    public Map<String, String> initiateStudentRegistration(String mail, String name, String faculty, String department, String password) {
        Map<String, String> response = new HashMap<>();

        // 1. Mail, tanımlı öğrenci listesinde mi?
        if (!isMailInStudentList(mail)) {
            response.put("Successful", "False");
            response.put("Message", "Email not eligible for registration (not in student list).");
            return response;
        }

        // 2. Zaten kayıtlı mı?
        if (userInfo.getId(mail) != null) {
            response.put("Successful", "False");
            response.put("Message", "User already registered.");
            return response;
        }

        // 3. Hashle ve geçici kayıt için sakla
        String hashedPassword = passwordEncoder.encode(password);
        pendingStudentRegistrations.put(mail, new PendingRegistration(name, faculty, department, hashedPassword));

        // 4. Kod üret ve mail gönder
        String code = verificationTokenService.generateCode(mail);
        emailService.sendVerificationCode(mail, code);

        response.put("Successful", "Pending");
        response.put("Message", "Verification code sent to email.");
        return response;
    }


    public Map<String, String> completeStudentRegistration(String mail, String code) {
        Map<String, String> response = new HashMap<>();

        if (!verificationTokenService.verifyCode(mail, code)) {
            response.put("Successful", "False");
            response.put("Message", "Invalid or expired verification code.");
            return response;
        }

        PendingRegistration pending = pendingStudentRegistrations.remove(mail);
        if (pending == null) {
            response.put("Successful", "False");
            response.put("Message", "No pending registration found.");
            return response;
        }

        int id = userInfo.findMaxStudentId() + 1;
        boolean success = userInfo.register_student(id, mail, pending.name, pending.faculty, pending.department, pending.hashedPassword);

        if (success) {
            response.put("Successful", "True");
            response.put("Message", "User successfully registered.");
        } else {
            response.put("Successful", "False");
            response.put("Message", "Database error during registration.");
        }

        return response;
    }

    private boolean isMailInStaffList(String mail) {
        Resource resource = new ClassPathResource("StaffMails.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(mail.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private final Map<String, PendingStaffRegistration> pendingStaffRegistrations = new HashMap<>();

    static class PendingStaffRegistration {
        String name;
        String title;
        String faculty;
        String department;
        String hashedPassword;

        public PendingStaffRegistration(String name, String title, String faculty, String department, String hashedPassword) {
            this.name = name;
            this.title = title;
            this.faculty = faculty;
            this.department = department;
            this.hashedPassword = hashedPassword;
        }
    }

    public Map<String, String> initiateStaffRegistration(String mail, String name, String title, String faculty, String department, String password) {
        Map<String, String> response = new HashMap<>();

        if (userInfo.getId(mail) != null) {
            response.put("Successful", "False");
            response.put("Message", "User already registered.");
            return response;
        }

        String hashedPassword = passwordEncoder.encode(password);
        pendingStaffRegistrations.put(mail, new PendingStaffRegistration(name, title, faculty, department, hashedPassword));

        String code = verificationTokenService.generateCode(mail);
        emailService.sendVerificationCode(mail, code);

        response.put("Successful", "Pending");
        response.put("Message", "Verification code sent to email.");
        return response;
    }

    public Map<String, String> completeStaffRegistration(String mail, String code) {
        Map<String, String> response = new HashMap<>();

        if (!verificationTokenService.verifyCode(mail, code)) {
            response.put("Successful", "False");
            response.put("Message", "Invalid or expired verification code.");
            return response;
        }

        PendingStaffRegistration pending = pendingStaffRegistrations.remove(mail);
        if (pending == null) {
            response.put("Successful", "False");
            response.put("Message", "No pending registration found.");
            return response;
        }

        int id = userInfo.findMaxStaffId() + 1;
        boolean success = userInfo.register_staff(id, mail, pending.name, pending.title, pending.faculty, pending.department, pending.hashedPassword);

        if (success) {
            response.put("Successful", "True");
            response.put("Message", "Staff successfully registered.");
        } else {
            response.put("Successful", "False");
            response.put("Message", "Database error during registration.");
        }

        return response;
    }


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
        String faculty = userInfo.getFaculty(mail);
        String department = userInfo.getDepartment(mail);

        Map<String, String> userMap = new HashMap<>();
        userMap.put("role", role);
        userMap.put("mail", mail);
        userMap.put("id", id);
        userMap.put("faculty", faculty);
        userMap.put("department", department);
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
    private boolean isMailInStudentList(String mail) {
        Resource resource = new ClassPathResource("student_emails.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(mail.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, String> sendVerificationCode(String mail) {
        Map<String, String> response = new HashMap<>();

        String code = verificationTokenService.generateCode(mail);
        try {
            emailService.sendVerificationCode(mail, code);
            response.put("status", "success");
            response.put("message", "Verification code sent to email.");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send verification email.");
        }

        return response;
    }

    public Map<String, String> confirmVerificationCode(String mail, String code) {
        Map<String, String> result = new HashMap<>();
        if (verificationTokenService.verifyCode(mail, code)) {
            result.put("status", "verified");
        } else {
            result.put("status", "failed");
            result.put("message", "Invalid or expired code.");
        }
        return result;
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


}}
