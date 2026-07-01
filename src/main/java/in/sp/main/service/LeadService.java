package in.sp.main.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import in.sp.main.entity.Lead;
import in.sp.main.entity.User;
import in.sp.main.repository.LeadRepository;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    // Save Lead
    public void saveLead(Lead lead) {

        leadRepository.save(lead);
    }

    // Get All Leads
    public List<Lead> getAll() {

        return leadRepository.findAll();
    }

    // Get Lead By ID
    public Lead getLeadById(Long id) {

        return leadRepository.findById(id).orElse(null);
    }

    // Delete Lead
    public void deleteLead(Long id) {

        if(leadRepository.existsById(id)) {

            leadRepository.deleteById(id);
        }
    }

    // Today's Follow-ups
    public List<Lead> getTodayFollowUps() {

        return leadRepository
                .findByFollowUpDate(LocalDate.now());
    }

    // Overdue Follow-ups
    public List<Lead> getOverdueFollowUps() {

        return leadRepository
                .findByFollowUpDateBefore(LocalDate.now());
    }

    public List<Lead> searchAllLeads(String keyword) {

        return leadRepository
                .findByNameContainingIgnoreCaseOrPhoneContaining(
                        keyword,
                        keyword
                );
    }

    // Search Leads By User
    public List<Lead> searchLeadsByUser(String keyword,
            User user) {

        return leadRepository
        .findByAssignedUserAndNameContainingIgnoreCaseOrAssignedUserAndPhoneContaining(
        user,
        keyword,
        user,
        keyword
        );
    }

    // Leads By Status
    public List<Lead> getLeadByStatus(String status) {

        return leadRepository.findByStatus(status);
    }

    // Leads By User
    public List<Lead> getLeadsByUser(User user) {

        return leadRepository.findByUser(user);
    }

    // Active Leads Count
    public long countActiveLeads() {

        return leadRepository.countByStatus("Active");
    }

    // Lost Leads Count
    public long countLostLeads() {

        return leadRepository.countByStatus("Lost");
    }
    
    public List<Lead> getAssignedLeads(User user) {
        return leadRepository.findByAssignedUserAndStatusNot(
                user,
                "NOT_INTERESTED");
    }
}