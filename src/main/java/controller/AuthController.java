package controller;

import org.springframework.web.bind.annotation.*;
import service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> authenticateUser(@RequestParam String username,
                                       @RequestParam String password) {

        AuthService authService = new AuthService(username);
        Map<String, String> userMap = authService.authenticateUser(username, password);

        return userMap;
    }
}

