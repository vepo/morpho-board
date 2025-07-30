package dev.vepo.morphoboard.ticket.comments;

public record CommentRequest(String content,
                             Long authorId) {}