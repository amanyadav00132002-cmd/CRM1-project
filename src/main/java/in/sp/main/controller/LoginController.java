package in.sp.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.sp.main.entity.User;
import in.sp.main.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String loginPage(HttpSession session,
                            HttpServletResponse response) {

        // Prevent browser cache
        response.setHeader("Cache-Control",
                "no-cache, no-store, must-revalidate");

        response.setHeader("Pragma", "no-cache");

        response.setDateHeader("Expires", 0);

        // Already logged in
        if(session.getAttribute("username") != null) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @PostMapping("/loginForm")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        // Empty validation
        if(email == null || email.trim().isEmpty() ||
           password == null || password.trim().isEmpty()) {

            model.addAttribute("error",
                    "Email and Password required");

            return "login";
        }

        // Find user
        User user = userRepository
                .findByEmail(email.trim());

        // Validate login
        if(user != null &&
           password.equals(user.getPassword())) {

            session.setAttribute("username",
                    user.getUsername());

            session.setAttribute("role",
                    user.getRole());

            session.setAttribute("userId",
                    user.getId());

            return "redirect:/dashboard";
        }

        // Invalid login
        model.addAttribute("error",
                "Invalid Email or Password");

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        // Destroy session
        session.invalidate();

        return "redirect:/";
    }
}