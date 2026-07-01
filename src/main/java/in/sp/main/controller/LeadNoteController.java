package in.sp.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import in.sp.main.entity.Lead;
import in.sp.main.service.LeadNoteService;
import in.sp.main.service.LeadService;
import jakarta.servlet.http.HttpSession;

@Controller
public class LeadNoteController {

    @Autowired
    private LeadNoteService noteService;

    @Autowired
    private LeadService leadService;

    @PostMapping("/leads/{id}/notes")
    public String addNote(@PathVariable Long id,
                          @RequestParam String comment,
                          HttpSession session) {

        // Login check
        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        String role = (String) session.getAttribute("role");

        Long userId = (Long) session.getAttribute("userId");

        Lead lead = leadService.getLeadById(id);

        if(lead == null) {
            return "redirect:/dashboard";
        }

        // USER can add note only to own lead
        if(!"ADMIN".equals(role)) {

            if(lead.getUser() == null ||
               !lead.getUser().getId().equals(userId)) {

                return "redirect:/dashboard";
            }
        }

        noteService.addNote(id, comment);

        return "redirect:/lead-details/" + id;
    }
}