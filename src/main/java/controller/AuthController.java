package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> authenticateUser(@RequestParam String username,
                                       @RequestParam String password) {

        AuthService authService = new AuthService();
        Map<String, String> userMap = authService.authenticateUser(username, password);
        System.out.println(userMap);
        System.out.println("QWEQWEQWEQWE");
        return userMap;

    }
}

