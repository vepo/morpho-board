package dev.vepo.morphoboard.user;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(@NotNull(message = "Seu nome não pode ser nulo, seu idiota!!!") @NotBlank String name,
                                @NotNull @NotBlank @Email String email,
                                @NotNull @NotEmpty List<String> roles) {}