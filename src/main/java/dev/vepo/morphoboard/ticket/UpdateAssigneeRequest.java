package dev.vepo.morphoboard.ticket;

import jakarta.validation.constraints.NotNull;

public record UpdateAssigneeRequest(@NotNull Long assigneeId) {}