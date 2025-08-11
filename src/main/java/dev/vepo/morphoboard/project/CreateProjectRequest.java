package dev.vepo.morphoboard.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProjectRequest(@NotBlank(message = "Project prefix cannot be empty") String prefix,
                                   @NotBlank(message = "Project name cannot be empty") String name,
                                   String description,
                                   @NotNull(message = "Workflow ID must be provided") Long workflowId) {}