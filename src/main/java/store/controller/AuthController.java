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
import store.service.AuthService;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestParam String mail, @RequestParam String password) {
        try {
            Map<String, String> userMap = authService.authenticateUser(mail, password);
            return ResponseEntity.ok(userMap);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected server error",
                    "details", e.getMessage()
            ));
        }
    }
    @PostMapping("/register_student/initiate")
    public ResponseEntity<?> initiateRegisterStudent(
            @RequestParam String mail,
            @RequestParam String name,
            @RequestParam String faculty,
            @RequestParam String department,
            @RequestParam String password) {

        Map<String, String> response = authService.initiateStudentRegistration(mail, name, faculty, department, password);
        return "Pending".equals(response.get("Successful"))
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/register_student/confirm")
    public ResponseEntity<?> confirmStudentRegistration(
            @RequestParam String mail,
            @RequestParam String code) {

        Map<String, String> response = authService.completeStudentRegistration(mail, code);
        return "True".equals(response.get("Successful"))
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/register_staff/initiate")
    public ResponseEntity<?> initiateRegisterStaff(
            @RequestParam String mail,
            @RequestParam String name,
            @RequestParam String title,
            @RequestParam String faculty,
            @RequestParam String department,
            @RequestParam String password) {

        Map<String, String> response = authService.initiateStaffRegistration(mail, name, title, faculty, department, password);
        return "Pending".equals(response.get("Successful"))
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/register_staff/confirm")
    public ResponseEntity<?> confirmStaffRegistration(
            @RequestParam String mail,
            @RequestParam String code) {

        Map<String, String> response = authService.completeStaffRegistration(mail, code);
        return "True".equals(response.get("Successful"))
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }



    @PostMapping("/validate-token")
    public boolean validateToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return authService.isTokenValid(token);
    }
    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String mail) {
        Map<String, String> response = authService.sendVerificationCode(mail);
        if ("success".equals(response.get("status"))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String mail, @RequestParam String code) {
        Map<String, String> response = authService.confirmVerificationCode(mail, code);
        if ("verified".equals(response.get("status"))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
/*
    @PostMapping("/register_student")
    public ResponseEntity<?> registerStudent(
            @RequestParam String mail,
            @RequestParam String name,
            @RequestParam String faculty,
            @RequestParam String department,
            @RequestParam String password) {

        Map<String, String> response = authService.register_student(mail, name, faculty, department, password);

        if ("True".equals(response.get("Successful"))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/register_staff")
    public ResponseEntity<?> registerStaff(
            @RequestParam String mail,
            @RequestParam String name,
            @RequestParam String title,
            @RequestParam String faculty,
            @RequestParam String department,
            @RequestParam String password) {

        Map<String, String> response = authService.register_staff(mail, name, title, faculty, department, password);

        if ("True".equals(response.get("Successful"))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

}
