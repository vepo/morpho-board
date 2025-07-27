package dev.vepo.morphoboard.ticket;

public record CreateTicketRequest(String title,
                                  String description,
                                  Long categoryId,
                                  Long projectId) {
}