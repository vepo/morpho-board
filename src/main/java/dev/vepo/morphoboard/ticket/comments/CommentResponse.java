package dev.vepo.morphoboard.ticket.comments;

import dev.vepo.morphoboard.ticket.TicketUserResponse;

public record CommentResponse(long id,
                              TicketUserResponse author,
                              String content,
                              long createdAt) {
    public static CommentResponse load(Comment comment) {
        return new CommentResponse(comment.getId(),
                                   TicketUserResponse.load(comment.getAuthor()),
                                   comment.getContent(),
                                   comment.getCreatedAt().toEpochMilli());
    }
}