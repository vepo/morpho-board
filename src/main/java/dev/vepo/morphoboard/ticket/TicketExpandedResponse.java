package dev.vepo.morphoboard.ticket;

import java.util.List;

public record TicketExpandedResponse(long id,
                                     String title,
                                     String description,
                                     String category,
                                     TicketUserResponse author,
                                     TicketUserResponse assignee,
                                     TicketProjectResponse project,
                                     String status,
                                     List<TicketHistoryResponse> history) {

    public static TicketExpandedResponse load(Ticket ticket) {
        return new TicketExpandedResponse(ticket.getId(),
                                          ticket.getTitle(),
                                          ticket.getDescription(),
                                          ticket.getCategory().getName(),
                                          TicketUserResponse.load(ticket.getAuthor()),
                                          TicketUserResponse.load(ticket.getAssignee()),
                                          TicketProjectResponse.load(ticket.getProject()),
                                          ticket.getStatus().getName(),
                                          ticket.getHistory()
                                                .stream()
                                                .map(TicketHistoryResponse::load)
                                                .toList());
    }

}