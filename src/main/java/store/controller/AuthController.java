package store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import store.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Map<String, String> authenticateUser(@RequestParam String mail,
                                       @RequestParam String password) {

        Map<String, String> userMap = authService.authenticateUser(mail, password);

        return userMap;
    }

    @PostMapping("/register_student")
    public boolean register(@RequestParam String mail, String name, String faculty, String department) {
        return authService.register_student(mail, name, faculty, department);
    }
}

