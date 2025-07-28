package dev.vepo.morphoboard.ticket;

public record CommentRequest(String content,
                             Long authorId) {}