package dev.vepo.morphoboard.ticket;

import java.util.List;

public record TicketExpandedResponse(long id,
                                     String title,
                                     String description,
                                     String category,
                                     TicketUserResponse author,
                                     TicketUserResponse assignee,
                                     ProjectResponse project,
                                     String status,
                                     List<TicketHistoryResponse> history) {

    public static TicketExpandedResponse load(Ticket ticket) {
        return new TicketExpandedResponse(ticket.id,
                                          ticket.title,
                                          ticket.description,
                                          ticket.category.name,
                                          TicketUserResponse.load(ticket.author),
                                          TicketUserResponse.load(ticket.assignee),
                                          ProjectResponse.load(ticket.project),
                                          ticket.status.name,
                                          ticket.history.stream()
                                                        .map(TicketHistoryResponse::load)
                                                        .toList());
    }

}