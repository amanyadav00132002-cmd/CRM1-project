package in.sp.main.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import in.sp.main.entity.Notification;
import in.sp.main.entity.User;
import in.sp.main.repository.NotificationRepository;

@Service
public class NotificationService {

	private final SimpMessagingTemplate messagingTemplate;
	
    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate) {

        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Create notification
    public void createNotification(User user, String message) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
        
        messagingTemplate.convertAndSend("/topic/notifications/" + user.getId(), message);
    }

    // Get user's notifications
    public List<Notification> getUserNotifications(User user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    // Count unread notifications
    public long getUnreadCount(User user) {

        return notificationRepository
                .countByUserAndIsReadFalse(user);
    }
}