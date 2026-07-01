package in.sp.main.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.sp.main.entity.Lead;
import in.sp.main.entity.LeadStatusHistory;
import in.sp.main.repository.LeadStatusHistoryRepository;

@Service
public class LeadStatusHistoryService {

    @Autowired
    private LeadStatusHistoryRepository historyRepo;

    public void saveHistory(Lead lead,
                            String oldStatus,
                            String newStatus) {

        LeadStatusHistory history =
                new LeadStatusHistory();

        history.setLead(lead);

        history.setOldStatus(oldStatus);

        history.setNewStatus(newStatus);

        history.setChangedAt(LocalDateTime.now());

        historyRepo.save(history);
    }

    public List<LeadStatusHistory>
    getHistory(Long leadId) {

        return historyRepo
                .findByLeadIdOrderByChangedAtDesc(leadId);
    }
}