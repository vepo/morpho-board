package dev.vepo.morphoboard.ticket;

import dev.vepo.morphoboard.project.Project;

public record TicketProjectResponse(long id,
                                    String name) {

    public static TicketProjectResponse load(Project project) {
        return new TicketProjectResponse(project.getId(), project.getName());
    }
}