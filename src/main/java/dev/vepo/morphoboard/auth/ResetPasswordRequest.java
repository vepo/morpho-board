package dev.vepo.morphoboard.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest(@NotBlank @NotNull String credential) {}
