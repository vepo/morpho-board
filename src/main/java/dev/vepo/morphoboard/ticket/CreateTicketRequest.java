package dev.vepo.morphoboard.ticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(@Size(min = 5, max = 255) String title,
                                  @Size(min = 5, max = 1200) String description,
                                  @NotNull Long categoryId,
                                  @NotNull Long projectId) {}