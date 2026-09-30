package in.sp.main.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import in.sp.main.entity.CallLog;
import in.sp.main.entity.Lead;
import in.sp.main.entity.User;
import in.sp.main.repository.CallLogRepository;

@Service
public class CallLogService {

    private final CallLogRepository callLogRepository;

    public CallLogService(CallLogRepository callLogRepository) {
        this.callLogRepository = callLogRepository;
    }

    // Save call record
    public void saveCall(
            Lead lead,
            User user,
            String callType,
            String callStatus,
            String notes) {

        CallLog callLog = new CallLog();

        callLog.setLead(lead);
        callLog.setUser(user);
        callLog.setCallType(callType);
        callLog.setCallStatus(callStatus);
        callLog.setCallTime(LocalDateTime.now());
        callLog.setNotes(notes);

        callLogRepository.save(callLog);
    }

    // Get call history of a lead
    public List<CallLog> getCallHistory(Lead lead) {

        return callLogRepository
                .findByLeadOrderByCallTimeDesc(lead);
    }
}