package com.codewithmosh.store;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String index() {
        return "home.html";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password) {

        System.out.println(username);
        System.out.println(password);
        System.out.println();

        if (username.equals("serkanınarkakapı") && password.equals("gıcırdıyor")) {
            return "redirect:/dashboard"; // or return an error page if login fails
        } else {
            return "redirect:/?error=true";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard.html";
    }
}

