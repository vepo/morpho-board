package dev.vepo.morphoboard.project;

import java.util.List;

import dev.vepo.morphoboard.workflow.Workflow;
import dev.vepo.morphoboard.workflow.WorkflowStatus;
import dev.vepo.morphoboard.workflow.WorkflowTransition;

public record ProjectStatusResponse(long id,
                                    String name,
                                    boolean start,
                                    List<Long> moveable) {

    public static ProjectStatusResponse load(WorkflowStatus status, Workflow workflow) {
        return new ProjectStatusResponse(status.getId(),
                                         status.getName(),
                                         workflow.getStart().equals(status),
                                         workflow.getTransitions().stream()
                                                 .filter(t -> t.getFrom().equals(status))
                                                 .map(WorkflowTransition::getTo)
                                                 .map(WorkflowStatus::getId)
                                                 .toList());
    }
}
