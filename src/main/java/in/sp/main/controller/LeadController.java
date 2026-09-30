package in.sp.main.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import in.sp.main.entity.Lead;
import in.sp.main.entity.User;
import in.sp.main.repository.UserRepository;
import in.sp.main.service.CallLogService;
import in.sp.main.service.LeadNoteService;
import in.sp.main.service.LeadService;
import in.sp.main.service.LeadStatusHistoryService;
import in.sp.main.service.NotificationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LeadController {

    @Autowired
    private LeadService leadService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeadNoteService leadNoteService;
    @Autowired
    private LeadStatusHistoryService historyService;
    
    private final NotificationService notificationService;
    
    private final CallLogService callLogService;
    
    
    public LeadController(NotificationService notificationService,
    		CallLogService callLogService) {
    	
    	this.notificationService = notificationService;
    	this.callLogService = callLogService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model,
            HttpSession session,
            HttpServletResponse response) {

        response.setHeader("Cache-Control",
                "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/";
        }

        model.addAttribute("username", username);

        String role = (String) session.getAttribute("role");

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/";
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return "redirect:/";
        }


        // =========================
        // LEAD LIST
        // =========================

        if (status != null && !status.trim().isEmpty()) {

            if ("active".equalsIgnoreCase(status)) {

                if ("ADMIN".equals(role)) {

                    model.addAttribute("leads",
                            leadService.getLeadsByStatusList(
                                    java.util.List.of(
                                            "New",
                                            "Hot",
                                            "Warm",
                                            "Cold"
                                    )
                            ));

                } else {

                    model.addAttribute("leads",
                            leadService.getLeadsByUserAndStatusList(
                                    user,
                                    java.util.List.of(
                                            "New",
                                            "Hot",
                                            "Warm",
                                            "Cold"
                                    )
                            ));
                }

            } else if ("lost".equalsIgnoreCase(status)) {

                if ("ADMIN".equals(role)) {

                    model.addAttribute("leads",
                            leadService.getLeadsByStatusList(
                                    java.util.List.of("Lost")
                            ));

                } else {

                    model.addAttribute("leads",
                            leadService.getLeadsByUserAndStatusList(
                                    user,
                                    java.util.List.of("Lost")
                            ));
                }

            } else {

                if ("ADMIN".equals(role)) {
                    model.addAttribute("leads",
                            leadService.getAll());
                } else {
                    model.addAttribute("leads",
                            leadService.getAssignedLeads(user));
                }
            }

        } else if (keyword != null && !keyword.trim().isEmpty()) {

            // =========================
            // SEARCH LOGIC
            // =========================

            if ("ADMIN".equals(role)) {

                model.addAttribute("users",
                        userRepository.findByRole("USER"));

                model.addAttribute("leads",
                        leadService.searchAllLeads(keyword));

            } else {

                model.addAttribute("leads",
                        leadService.searchLeadsByUser(
                                keyword,
                                user));
            }

        } else {

            // =========================
            // NORMAL DASHBOARD LEADS
            // =========================

            if ("ADMIN".equals(role)) {

                model.addAttribute("users",
                        userRepository.findByRole("USER"));

                model.addAttribute("leads",
                        leadService.getAll());

            } else {

                model.addAttribute("leads",
                        leadService.getAssignedLeads(user));
            }
        }


        // =========================
        // TODAY FOLLOW-UPS
        // =========================

        if ("ADMIN".equals(role)) {

            model.addAttribute("todayLeads",
                    leadService.getTodayFollowUps());

        } else {

            model.addAttribute("todayLeads",
                    leadService.getTodayFollowUpsForUser(user));
        }


        // =========================
        // OVERDUE FOLLOW-UPS
        // =========================

        if ("ADMIN".equals(role)) {

            model.addAttribute("overdueLeads",
                    leadService.getOverdueFollowUps());

        } else {

            model.addAttribute("overdueLeads",
                    leadService.getOverdueFollowUpsForUser(user));
        }


        // =========================
        // DASHBOARD COUNTS
        // =========================

        if ("ADMIN".equals(role)) {

            model.addAttribute("totalLeads",
                    leadService.getAll().size());

            model.addAttribute("activeLeads",
                    leadService.countActiveLeads());

            model.addAttribute("lostLeads",
                    leadService.countLostLeads());

        } else {

            model.addAttribute("totalLeads",
                    leadService.countTotalLeadsForUser(user));

            model.addAttribute("activeLeads",
                    leadService.countActiveLeadsForUser(user));

            model.addAttribute("lostLeads",
                    leadService.countLostLeadsForUser(user));
        }

      
        if (userId != null) {

            User currentUser = userRepository
                    .findById(userId)
                    .orElse(null);

            if (currentUser != null) {

                model.addAttribute(
                        "unreadCount",
                        notificationService.getUnreadCount(currentUser)
                );
            }
        }

        return "dashboard";
    }
    
    @GetMapping("/addLead")
    public String addLeadPage(HttpSession session) {

        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        return "addLead";
    }

    @PostMapping("/saveLead")
    public String saveLead(@ModelAttribute Lead lead, HttpSession session, Model model) {

        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

//      Check duplicate client by phone no.
        Lead existingLead = leadService.getLeadByPhone(lead.getPhone());

        if (existingLead != null) {

            User existingUser = existingLead.getAssignedUser();

            if (existingUser != null) {
            	
//            	Current user who is trying to add the client
            	String currentUsername = (String) session.getAttribute("username");
            	
//            	Notification for existing owner
            	String  notificationMessage = "Another user (" + currentUsername
            			+") is trying to add your client: "
            			+ existingLead.getName()
            			+ ". Client is already assigned to you.";
            	
            	notificationService.createNotification(
            			existingUser,
            			notificationMessage);
            
//            	Message for current user 
            	String ownerPhone = existingUser.getPhone();

                model.addAttribute(
                        "error",
                        "This client already exists in CRM. "
                        + "Assigned to: " + existingUser.getUsername()
                        + "\n contact Number: "
                        +  (ownerPhone != null ? ownerPhone : "Not available")
                        + ". Please contact the assigned user."
                );

            } else {

                model.addAttribute(
                        "error",
                        "Client already exists in CRM, "
                        + "but no user is currently assigned."
                );
            }

            return "addLead";
        }
        
        lead.setCreatedDate(LocalDate.now());

        Long userId = (Long) session.getAttribute("userId");

        User user = userRepository.findById(userId).orElse(null);

        if(user !=null) {
        
        lead.setUser(user);
        	
        lead.setAssignedUser(user);
        }

        leadService.saveLead(lead);


        return "redirect:/dashboard";
    }

    @PostMapping("/saveCall")
    public String saveCall(
    		@RequestParam Long leadId,
    		@RequestParam String callType,
    		@RequestParam String callStatus,
    		@RequestParam(required = false) String notes,
    		HttpSession session) {
    	
    	if (session.getAttribute("username") == null) {
    		return "redirect:/";
    	}
    	
    	Long userId = (Long) session.getAttribute("userId");
    	
    	User user = userRepository.findById(userId).orElse(null);
    	
    	Lead lead = leadService.getLeadById(leadId);
    	
    	if (user == null | lead == null) {
    		return "redirect:/dashboard";
    	}
    	
    	callLogService.saveCall(lead,user,callType,callStatus,notes);
    	
    	return "redirect:/lead-details/" + leadId;
    }
    
    
    @GetMapping("/deleteLead/{id}")
    public String deleteLead(@PathVariable Long id,
                             HttpSession session) {

        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        String role = (String) session.getAttribute("role");

        Long userId = (Long) session.getAttribute("userId");

        Lead lead = leadService.getLeadById(id);

        if(lead == null) {
            return "redirect:/dashboard";
        }

        // ADMIN can delete all leads
        if("ADMIN".equals(role)) {

            leadService.deleteLead(id);

        } else {

            // USER can delete only own leads
            if(lead.getUser().getId().equals(userId)) {

                leadService.deleteLead(id);

            } else {

                return "redirect:/dashboard";
            }
        }

        return "redirect:/dashboard";
    }
    
    @GetMapping("/editLead/{id}")
    public String editLead(@PathVariable Long id,
                           Model model,
                           HttpSession session) {

        // Check login
        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        String role = (String) session.getAttribute("role");

        Long userId = (Long) session.getAttribute("userId");

        // Fetch lead
        Lead lead = leadService.getLeadById(id);

        if(lead == null) {
            return "redirect:/dashboard";
        }

        // USER can edit only own lead
        if(!"ADMIN".equals(role)) {

            if(!lead.getUser().getId().equals(userId)) {
                return "redirect:/dashboard";
            }
        }

        model.addAttribute("lead", lead);

        return "editLead";
    }

    @PostMapping("/updateLead")
    public String updateLead(@ModelAttribute Lead lead,
                             HttpSession session) {

        // Check login
        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        String role = (String) session.getAttribute("role");

        Long userId = (Long) session.getAttribute("userId");

        // Fetch existing lead
        Lead existingLead = leadService.getLeadById(lead.getId());

        if(existingLead == null) {
            return "redirect:/dashboard";
        }

        // USER can update only own lead
        if(!"ADMIN".equals(role)) {

            if(!existingLead.getUser().getId().equals(userId)) {
                return "redirect:/dashboard";
            }
        }

        // SAVE STATUS HISTORY
        if(!java.util.Objects.equals(
                existingLead.getStatus(),
                lead.getStatus())) {

            historyService.saveHistory(
                    existingLead,
                    existingLead.getStatus(),
                    lead.getStatus()
            );
        }

        // Update lead details
        existingLead.setName(lead.getName());
        existingLead.setEmail(lead.getEmail());
        existingLead.setPhone(lead.getPhone());
        existingLead.setSource(lead.getSource());
        existingLead.setStatus(lead.getStatus());
        existingLead.setFollowUpDate(lead.getFollowUpDate());

        // Save updated lead
        leadService.saveLead(existingLead);

        return "redirect:/dashboard";
    }
    
    @GetMapping("/customers")
    public String customerPage(
            @RequestParam(required = false) String keyword,
            HttpSession session,
            Model model) {

        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        model.addAttribute("username",
                session.getAttribute("username"));

        String role = (String) session.getAttribute("role");

        Long userId = (Long) session.getAttribute("userId");

        User user = userRepository.findById(userId).orElse(null);

        if(keyword != null && !keyword.trim().isEmpty()) {

            if("ADMIN".equals(role)) {

                model.addAttribute("customers",
                        leadService.searchAllLeads(keyword));

            } else {

                model.addAttribute("customers",
                        leadService.searchLeadsByUser(keyword, user));
            }

        } else {

            if("ADMIN".equals(role)) {

                model.addAttribute("customers",
                        leadService.getAll());

            } else {

                model.addAttribute("customers",
                        leadService.getAssignedLeads(user));
            }
        }

        return "customers";
    }
    
    @GetMapping("/reports")
    public String reportPage(HttpSession session, Model model) {
    	
    	if(session.getAttribute("username") == null) {
    		return "redirect:/";
    	}
    	
    	  model.addAttribute("username",
    	            session.getAttribute("username"));

    	  String role = (String) session.getAttribute("role");
    	  
    	  Long userId = (Long) session.getAttribute("userId");
    	  
    	  if (userId == null) {
    		  return "redirect:/";
    	  }
    	  
    	  User user = userRepository.findById(userId).orElse(null);
    	  
    	  if (user == null) {
    		  return "redirect:/";
    	  }
    	  
    	  // =========================
    	    // ADMIN REPORTS
    	    // =========================

    	    if ("ADMIN".equals(role)) {

    	        model.addAttribute(
    	                "totalLeads",
    	                leadService.getAll().size()
    	        );

    	        model.addAttribute(
    	                "activeLeads",
    	                leadService.countActiveLeads()
    	        );

    	        model.addAttribute(
    	                "lostLeads",
    	                leadService.countLostLeads()
    	        );

    	        model.addAttribute(
    	                "todayLeads",
    	                leadService.getTodayFollowUps().size()
    	        );

    	        model.addAttribute(
    	                "overdueLeads",
    	                leadService.getOverdueFollowUps().size()
    	        );

    	    }

    	    // =========================
    	    // USER REPORTS
    	    // =========================

    	    else {

    	        model.addAttribute(
    	                "totalLeads",
    	                leadService.countTotalLeadsForUser(user)
    	        );

    	        model.addAttribute(
    	                "activeLeads",
    	                leadService.countActiveLeadsForUser(user)
    	        );

    	        model.addAttribute(
    	                "lostLeads",
    	                leadService.countLostLeadsForUser(user)
    	        );

    	        model.addAttribute(
    	                "todayLeads",
    	                leadService.getTodayFollowUpsForUser(user).size()
    	        );

    	        model.addAttribute(
    	                "overdueLeads",
    	                leadService.getOverdueFollowUpsForUser(user).size()
    	        );
    	    }
    	    return "reports";
    }
    
    @GetMapping("/user-leads/{id}")
    public String userLeads(@PathVariable Long id,
                            HttpSession session,
                            Model model) {

        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        String role = (String) session.getAttribute("role");

        // ONLY ADMIN
        if(!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }

        User user = userRepository.findById(id).orElse(null);

        if(user == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("username",
                session.getAttribute("username"));

        model.addAttribute("leads",
                leadService.getAssignedLeads(user));

        return "user-leads";
    }
    
    @GetMapping("/lead-details/{id}")
    public String leadDetails(@PathVariable Long id,
                              HttpSession session,
                              Model model) {

        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        String role = (String) session.getAttribute("role");
        Long userId = (Long) session.getAttribute("userId");
        
        Lead lead = leadService.getLeadById(id);

        if(lead == null) {
            return "redirect:/dashboard";
        }

//        user can seee their own lead
        if(!"ADMIN".equals(role)) {
        	
        	if(lead.getAssignedUser() == null ||
        		!lead.getAssignedUser().getId().equals(userId)) {
        		return "redirect:/dashboard";
        	}
        }
        
        model.addAttribute("lead", lead);

        model.addAttribute("notes",
                leadNoteService.getNotes(id));

        model.addAttribute("history",
                historyService.getHistory(id));
        
        model.addAttribute("callHistory",
        		callLogService.getCallHistory(lead));
        
        return "lead-details";
    }
    	
    @PostMapping("/lead-details/{id}/status")
    public String UpdateLeadStatus(@PathVariable Long id, @RequestParam("status") String status, HttpSession session) {
    	
    	if(session.getAttribute("username") == null) {
    		return "redirect:/";
    	}
    	
    	String role = (String) session.getAttribute("role");
    	Long userId = (Long) session.getAttribute("userId");
    	
    	Lead lead = leadService.getLeadById(id);
    	
    	if(lead == null) {
    		return "redirect:/dashboard";
    	}
    	
    	// USER can update only their assigned lead
    	if(!"ADMIN".equals(role)) {
    		
    		if(lead.getAssignedUser() == null || 
    				!lead.getAssignedUser().getId().equals(userId)) {
    			
    			return "redirect:/dashboard";
    		}
    	}
    	
    	if("NOT INTERESTED".equals(status)) {
    		lead.setStatus("NOT_INTERESTED");
    	}
    	else if("INTERESTED".equals(status)) {
    		lead.setStatus("INTERESTED");
    	}
    	
    	leadService.saveLead(lead);
    	
    	return "redirect:/lead-details/" + id;
    }
    
    @GetMapping("/notifications")
    public String notifications(
            HttpSession session,
            Model model) {

        if (session.getAttribute("username") == null) {
            return "redirect:/";
        }

        Long userId =
                (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/";
        }

        User user = userRepository
                .findById(userId)
                .orElse(null);

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute(
                "username",
                user.getUsername()
        );

        model.addAttribute(
                "notifications",
                notificationService.getUserNotifications(user)
        );

        return "notifications";
    }
    
    @GetMapping("/assignLead/{id}")
    public String assignLeadPage(@PathVariable Long id,
                                 HttpSession session,
                                 Model model) {

        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        String role = (String) session.getAttribute("role");

        if(!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }

        Lead lead = leadService.getLeadById(id);

        if(lead == null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("lead", lead);
        model.addAttribute("users",
                userRepository.findByRole("USER"));

        return "assignLead";
    }
    
    @PostMapping("/assignLead")
    public String assignLead(@RequestParam Long leadId,
                             @RequestParam Long userId,
                             HttpSession session) {

        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        String role = (String) session.getAttribute("role");

        if(!"ADMIN".equals(role)) {
            return "redirect:/dashboard";
        }

        Lead lead = leadService.getLeadById(leadId);

        User user = userRepository.findById(userId)
                                  .orElse(null);

        if(lead == null || user == null) {
            return "redirect:/dashboard";
        }

        lead.setAssignedUser(user);

        leadService.saveLead(lead);

        return "redirect:/dashboard";
    }
    
    @PostMapping("/updateLeadStatus")
    public String updateLeadStatus(
            @RequestParam Long leadId,
            @RequestParam String status,
            @RequestParam(required = false) LocalDate followUpDate,
            HttpSession session) {

        if(session.getAttribute("username") == null) {
            return "redirect:/";
        }

        String role = (String) session.getAttribute("role");
        Long userId = (Long) session.getAttribute("userId");
        
        
        Lead lead = leadService.getLeadById(leadId);

        if(lead == null) {
            return "redirect:/dashboard";
        }

        // USER can update only their assigned lead
        if (!"ADMIN".equals(role)) {

            if (lead.getAssignedUser() == null ||
                !lead.getAssignedUser().getId().equals(userId)) {

                return "redirect:/dashboard";
            }
        }
        
        if("NOT_INTERESTED".equals(status)) {

            lead.setStatus("NOT_INTERESTED");

         // Keep assigned user
            // Do NOT set assignedUser to null
            

        } else if("INTERESTED".equals(status)) {

            if(followUpDate == null) {
                return "redirect:/lead-details/" + leadId;
            }

            lead.setStatus("INTERESTED");
            lead.setFollowUpDate(followUpDate);
        }

        leadService.saveLead(lead);

        return "redirect:/dashboard";
    }
    
}