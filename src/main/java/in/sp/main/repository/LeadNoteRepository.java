package in.sp.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sp.main.entity.LeadNote;

import java.util.List;

public interface LeadNoteRepository
        extends JpaRepository<LeadNote, Long> {

    // Get notes by lead
    List<LeadNote> findByLeadId(Long leadId);

    // Latest notes first
    List<LeadNote> findByLeadIdOrderByCreatedAtDesc(Long leadId);

}