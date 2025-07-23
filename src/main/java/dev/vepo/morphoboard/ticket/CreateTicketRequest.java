package dev.vepo.morphoboard.ticket;

public record CreateTicketRequest(String title,
                                         String description,
                                         Long categoryId,
                                         Long authorId,
                                         Long assigneeId,
                                         Long projectId) {
}