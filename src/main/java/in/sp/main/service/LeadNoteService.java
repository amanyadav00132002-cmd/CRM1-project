package in.sp.main.service;

import in.sp.main.entity.Lead;
import in.sp.main.entity.LeadNote;
import in.sp.main.repository.LeadNoteRepository;
import in.sp.main.repository.LeadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeadNoteService {

    @Autowired
    private LeadNoteRepository noteRepo;

    @Autowired
    private LeadRepository leadRepo;

    // Add note to lead
    public void addNote(Long leadId, String comment) {

        Lead lead = leadRepo.findById(leadId)
                .orElseThrow(() ->
                        new RuntimeException("Lead not found"));

        LeadNote note = new LeadNote();

        note.setComment(comment);

        note.setCreatedAt(LocalDateTime.now());

        note.setLead(lead);

        noteRepo.save(note);
    }

    // Get notes of lead
    public List<LeadNote> getNotes(Long leadId) {

        return noteRepo
                .findByLeadIdOrderByCreatedAtDesc(leadId);
    }
}