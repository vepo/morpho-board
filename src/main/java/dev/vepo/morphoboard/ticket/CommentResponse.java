package dev.vepo.morphoboard.ticket;

public record CommentResponse(long id,
                              TicketUserResponse author,
                              String content,
                              long createdAt) {
    public static CommentResponse load(Comment comment) {
        return new CommentResponse(comment.id,
                                   TicketUserResponse.load(comment.author),
                                   comment.content,
                                   comment.createdAt.toEpochMilli());
    }
}