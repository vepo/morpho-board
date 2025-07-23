package dev.vepo.morphoboard.ticket;

import dev.vepo.morphoboard.project.Project;

public record ProjectResponse(long id,
                              String name) {

    public static ProjectResponse load(Project project) {
        return new ProjectResponse(project.id, project.name);
    }
}