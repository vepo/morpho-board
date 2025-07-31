package dev.vepo.morphoboard.workflow;

import java.util.List;

public record WorkflowResponse(long id,
                               String name,
                               List<String> statuses,
                               String start,
                               List<TransitionResponse> transitions) {}