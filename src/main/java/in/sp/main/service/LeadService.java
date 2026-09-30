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

    // =====================================================
    // Save Lead
    // =====================================================

    public void saveLead(Lead lead) {

        leadRepository.save(lead);
    }

    // =====================================================
    // Check if client already exists by phone
    // =====================================================

    public Lead getLeadByPhone(String phone) {

        return leadRepository.findByPhone(phone);
    }

    // =====================================================
    // Get All Leads
    // =====================================================

    public List<Lead> getAll() {

        return leadRepository.findAll();
    }

    // =====================================================
    // Get Lead By ID
    // =====================================================

    public Lead getLeadById(Long id) {

        return leadRepository.findById(id).orElse(null);
    }

    // =====================================================
    // Delete Lead
    // =====================================================

    public void deleteLead(Long id) {

        if (leadRepository.existsById(id)) {

            leadRepository.deleteById(id);
        }
    }

    // =====================================================
    // Today's Follow-ups - ADMIN
    // =====================================================

    public List<Lead> getTodayFollowUps() {

        return leadRepository.findByFollowUpDate(
                LocalDate.now());
    }

    // =====================================================
    // Overdue Follow-ups - ADMIN
    // =====================================================

    public List<Lead> getOverdueFollowUps() {

        return leadRepository.findByFollowUpDateBefore(
                LocalDate.now());
    }

    // =====================================================
    // Overdue Follow-ups - USER
    // =====================================================

    public List<Lead> getOverdueFollowUpsForUser(User user) {

        return leadRepository.findByFollowUpDateBeforeAndAssignedUser(
                LocalDate.now(),
                user);
    }

    // =====================================================
    // Search All Leads - ADMIN
    // =====================================================

    public List<Lead> searchAllLeads(String keyword) {

        return leadRepository
                .findByNameContainingIgnoreCaseOrPhoneContaining(
                        keyword,
                        keyword);
    }

    // =====================================================
    // Search Leads By User
    // =====================================================

    public List<Lead> searchLeadsByUser(
            String keyword,
            User user) {

        return leadRepository
                .findByAssignedUserAndNameContainingIgnoreCaseOrAssignedUserAndPhoneContaining(
                        user,
                        keyword,
                        user,
                        keyword);
    }

    // =====================================================
    // Leads By Status
    // =====================================================

    public List<Lead> getLeadByStatus(String status) {

        return leadRepository.findByStatus(status);
    }

    // =====================================================
    // Leads By User
    // =====================================================

    public List<Lead> getLeadsByUser(User user) {

        return leadRepository.findByUser(user);
    }

    // =====================================================
    // ADMIN - Total Leads
    // =====================================================

    public long countTotalLeads() {

        return leadRepository.count();
    }

    // =====================================================
    // ADMIN - Active Leads
    // New + hot + warm + cold
    // =====================================================

    public long countActiveLeads() {

        return leadRepository.countByStatusIn(
                List.of(
                		"New",
                        "new",
                        "Hot",
                        "Warm",
                        "Cold"
                )
        );
    }

    // =====================================================
    // ADMIN - Lost Leads
    // =====================================================

    public long countLostLeads() {

        return leadRepository.countByStatus("Lost");
    }

    // =====================================================
    // USER - Total Leads
    // =====================================================

    public long countTotalLeadsForUser(User user) {

        return leadRepository.countByAssignedUser(user);
    }

    // =====================================================
    // USER - Active Leads
    // =====================================================

    public long countActiveLeadsForUser(User user) {

        return leadRepository.countByAssignedUserAndStatusIn(
                user,
                List.of(
                		"New",
                        "new",
                        "Hot",
                        "Warm",
                        "Cold"
                )
        );
    }

    // =====================================================
    // USER - Lost Leads
    // =====================================================

    public long countLostLeadsForUser(User user) {

        return leadRepository.countByAssignedUserAndStatus(
                user,
                "Lost");
    }

    // =====================================================
    // Assigned Leads
    // =====================================================

    public List<Lead> getAssignedLeads(User user) {

        return leadRepository.findByAssignedUserAndStatusNot(
                user,
                "NOT_INTERESTED");
    }

    // =====================================================
    // Today's Follow-ups - USER
    // =====================================================

    public List<Lead> getTodayFollowUpsForUser(User user) {

        return leadRepository.findByFollowUpDateAndAssignedUser(
                LocalDate.now(),
                user);
    }

    // =====================================================
    // Leads By Status List - ADMIN
    // =====================================================

    public List<Lead> getLeadsByStatusList(
            List<String> statuses) {

        return leadRepository.findByStatusIn(statuses);
    }

    // =====================================================
    // Leads By Status List - USER
    // =====================================================

    public List<Lead> getLeadsByUserAndStatusList(
            User user,
            List<String> statuses) {

        return leadRepository.findByAssignedUserAndStatusIn(
                user,
                statuses);
    }
}