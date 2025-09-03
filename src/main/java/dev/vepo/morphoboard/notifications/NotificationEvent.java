package dev.vepo.morphoboard.notifications;

public record NotificationEvent(long ticketId, String type, String content) {

}
