package dev.vepo.morphoboard.ticket;

public record TicketResponse(long id,
                             String title,
                             String description,
                             Long category,
                             Long author,
                             Long assignee,
                             Long project,
                             Long status) {
    public static TicketResponse load(Ticket ticket) {
        return new TicketResponse(ticket.id,
                                  ticket.title,
                                  ticket.description,
                                  ticket.category.id,
                                  ticket.author.id,
                                  ticket.assignee != null ? ticket.assignee.id : null,
                                  ticket.project.id,
                                  ticket.status.id);
    }
}