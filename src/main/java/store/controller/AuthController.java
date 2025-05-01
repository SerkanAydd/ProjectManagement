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
    public Map<String, String> register(@RequestParam String mail, @RequestParam String name, @RequestParam String faculty, @RequestParam String department, @RequestParam String password) {
        Map<String, String> accomplish = authService.register_student(mail, name, faculty, department, password);
        return accomplish;
    }

    @PostMapping("/register_staff")
    public Map<String, String> register_staff(@RequestParam String mail, @RequestParam String name,@RequestParam String title, @RequestParam String faculty, @RequestParam String department, @RequestParam String password ) {
        Map<String, String> accomplish = authService.register_staff(mail, name, title, faculty, department, password);
        return accomplish;
    }

}

