package dev.vepo.morphoboard.ticket;

public record UpdateTicketRequest(String title,
                                         String description,
                                         Long categoryId,
                                         Long assigneeId) {
}