package store.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import store.service.AuthService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Map<String, String> authenticateUser(@RequestParam String mail,
            @RequestParam String password) {

        Map<String, String> userMap = authService.authenticateUser(mail, password);

        return userMap;
    }

    @PostMapping("/validate-token")
    public boolean validateToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return authService.isTokenValid(token);
    }

    @PostMapping("/register_student")
    public Map<String, String> register(@RequestParam String mail, @RequestParam String name, @RequestParam String faculty, @RequestParam String department, @RequestParam String password) {
        Map<String, String> accomplish = authService.register_student(mail, name, faculty, department, password);
        return accomplish;
    }

    @PostMapping("/register_staff")
    public Map<String, String> register_staff(@RequestParam String mail, @RequestParam String name, @RequestParam String title, @RequestParam String faculty, @RequestParam String department, @RequestParam String password) {
        Map<String, String> accomplish = authService.register_staff(mail, name, title, faculty, department, password);
        return accomplish;
    }

}
