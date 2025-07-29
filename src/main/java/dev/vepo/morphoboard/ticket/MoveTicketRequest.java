package dev.vepo.morphoboard.ticket;

import io.smallrye.common.constraint.NotNull;

public record MoveTicketRequest(@NotNull Long to) {}