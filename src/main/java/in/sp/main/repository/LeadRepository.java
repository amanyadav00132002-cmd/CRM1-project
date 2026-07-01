package in.sp.main.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sp.main.entity.Lead;
import in.sp.main.entity.User;

public interface LeadRepository
        extends JpaRepository<Lead, Long> {

    // Search leads by name
	List<Lead> findByNameContainingIgnoreCaseOrPhoneContaining(String keyword, String keyword2);

	
    // Today's follow-ups
    List<Lead> findByFollowUpDate(LocalDate date);

    // Overdue follow-ups
    List<Lead> findByFollowUpDateBefore(LocalDate date);

    // Status-based leads
    List<Lead> findByStatus(String status);

    // Count leads by status
    long countByStatus(String status);

    // User-specific leads
    List<Lead> findByUser(User user);

 // Search assigned leads by name OR phone
    List<Lead> findByAssignedUserAndNameContainingIgnoreCaseOrAssignedUserAndPhoneContaining(
            User assignedUser1,
            String name,
            User assignedUser2,
            String phone
    );
    
    List<Lead> findByAssignedUser(User assignedUser);

    // Search by email
    Lead findByEmail(String email);

    // Search by phone
    Lead findByPhone(String phone);
    
    List<Lead> findByAssignedUserAndStatusNot(User assignedUser, String status);
}