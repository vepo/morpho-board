package dev.vepo.morphoboard.workflow;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record WorkflowResponse(long id,
                               String name,
                               List<String> statuses,
                               String start,
                               List<TransitionResponse> transitions) {
    public static WorkflowResponse load(Workflow workflow) {
        return new WorkflowResponse(workflow.getId(),
                                    workflow.getName(),
                                    workflow.getStatuses()
                                            .stream()
                                            .sorted(Comparator.comparing(WorkflowStatus::getId))
                                            .map(WorkflowStatus::getName)
                                            .collect(Collectors.toList()),
                                    workflow.getStart()
                                            .getName(),
                                    workflow.getTransitions()
                                            .stream()
                                            .map(transition -> new TransitionResponse(transition.getFrom().getName(),
                                                                                      transition.getTo().getName()))
                                            .collect(Collectors.toList()));
    }
}