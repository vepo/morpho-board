package dev.vepo.morphoboard.notifications;

public record UserNotificationEvent(long id, String type, boolean read, String content, long timestamp) {

    public static UserNotificationEvent load(Notification notification) {
        return new UserNotificationEvent(notification.getId(),
                                         notification.getType(),
                                         notification.isRead(),
                                         notification.getContent(),
                                         notification.getCreatedAt()
                                                     .toEpochMilli());
    }

}
