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
    public Map<String, String> authenticateUser(@RequestParam String username,
                                       @RequestParam String password) {

        System.out.println("Eşşek serhat");

        Map<String, String> userMap = authService.authenticateUser(username, password);


        return userMap;
    }
}

