package dev.vepo.morphoboard.ticket;

import dev.vepo.morphoboard.user.User;

public record TicketUserResponse(long id, String name, String email) {
    public static TicketUserResponse load(User user) {
        if (user == null) {
            return null;
        }
        return new TicketUserResponse(user.getId(), user.getName(), user.getEmail());
    }
}