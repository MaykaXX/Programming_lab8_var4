package infrastructure;

public class EmailNotificationService implements NotificationService {
    @Override
    public void sendReminder(String recipientEmail, String message) {
        System.out.println("[Email] Нагадування надіслано на " + recipientEmail +
                ": " + message);
    }
}