package dev.vepo.morphoboard.user;

import java.util.List;

public record UserResponse(long id, String name, String email, List<String> roles) {

    public static UserResponse load(User user) {
        return new UserResponse(user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRoles()
                                    .stream()
                                    .map(Role::role)
                                    .toList());
    }
}
