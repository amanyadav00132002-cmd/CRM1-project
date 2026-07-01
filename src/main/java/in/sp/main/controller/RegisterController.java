package in.sp.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.sp.main.entity.User;
import in.sp.main.repository.UserRepository;

@Controller
public class RegisterController {

    private final UserRepository userRepository;

    public RegisterController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String registerPage() {

        return "register";
    }

    @PostMapping("/registerForm")
    public String register(@ModelAttribute User user,
                           Model model) {

        // Check existing email
        User existingUser =
                userRepository.findByEmail(user.getEmail());

        if(existingUser != null) {

            model.addAttribute("error",
                    "Email already exists");

            return "register";
        }

        // Basic validation
        if(user.getUsername() == null ||
           user.getUsername().trim().isEmpty() ||

           user.getEmail() == null ||
           user.getEmail().trim().isEmpty() ||

           user.getPassword() == null ||
           user.getPassword().trim().isEmpty()) {

            model.addAttribute("error",
                    "All fields are required");

            return "register";
        }

        // Default role
        user.setRole("USER");

        // Save user
        userRepository.save(user);

        model.addAttribute("success",
                "Registration successful");

        return "login";
    }
}