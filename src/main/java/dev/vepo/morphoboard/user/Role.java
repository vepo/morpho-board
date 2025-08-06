package dev.vepo.morphoboard.user;

@SuppressWarnings("java:S1192")
public enum Role {
    USER("user"),
    ADMIN("admin"),
    PROJECT_MANAGER("project-manager");

    public static final String PROJECT_MANAGER_ROLE = "project-manager";
    public static final String USER_ROLE = "user";
    public static final String ADMIN_ROLE = "admin";

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String role() {
        return role;
    }
}
