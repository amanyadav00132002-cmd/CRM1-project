package in.sp.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sp.main.entity.LeadStatusHistory;

public interface LeadStatusHistoryRepository
        extends JpaRepository<LeadStatusHistory, Long> {

    List<LeadStatusHistory>
    findByLeadIdOrderByChangedAtDesc(Long leadId);

}