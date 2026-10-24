package infrastructure;

public interface NotificationService {
    void sendReminder(String recipientEmail, String message);
}