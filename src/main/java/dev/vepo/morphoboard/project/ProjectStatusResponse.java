package dev.vepo.morphoboard.project;

import java.util.List;

import dev.vepo.morphoboard.workflow.Workflow;
import dev.vepo.morphoboard.workflow.WorkflowStatus;

public record ProjectStatusResponse(long id,
                                    String name,
                                    boolean start,
                                    List<Long> moveable) {

    public static ProjectStatusResponse load(WorkflowStatus status, Workflow workflow) {
        return new ProjectStatusResponse(status.getId(),
                                         status.getName(),
                                         workflow.getStart().getId() == status.getId(),
                                         workflow.getTransitions().stream()
                                                 .filter(t -> t.getFrom().getId() == status.getId())
                                                 .map(s -> s.getTo().getId())
                                                 .toList());
    }
}
