package in.sp.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sp.main.entity.CallLog;
import in.sp.main.entity.Lead;

public interface CallLogRepository
        extends JpaRepository<CallLog, Long> {

    List<CallLog> findByLeadOrderByCallTimeDesc(Lead lead);
}