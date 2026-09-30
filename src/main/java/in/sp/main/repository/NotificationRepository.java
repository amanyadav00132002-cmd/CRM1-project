package in.sp.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sp.main.entity.Notification;
import in.sp.main.entity.User;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // User ki notifications
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Unread notifications
    long countByUserAndIsReadFalse(User user);
}