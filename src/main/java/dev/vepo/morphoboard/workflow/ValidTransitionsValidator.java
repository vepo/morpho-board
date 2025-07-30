package dev.vepo.morphoboard.workflow;

import java.util.Objects;

import dev.vepo.morphoboard.workflow.WorkflowEndpoint.CreateWorkflowRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTransitionsValidator implements ConstraintValidator<ValidTransitions, CreateWorkflowRequest> {

    @Override
    public boolean isValid(CreateWorkflowRequest workflow, ConstraintValidatorContext context) {
        if (Objects.isNull(workflow.statuses())
                || Objects.isNull(workflow.transitions())
                || workflow.transitions().stream().anyMatch(t -> Objects.isNull(t.from()) || Objects.isNull(t.to()))) {
            // not validated by this validator
            return true;
        }
        // Check start
        if (!workflow.statuses().contains(workflow.start())) {
            return false;
        }

        // Check transitions
        for (var transition : workflow.transitions()) {
            if (!workflow.statuses().contains(transition.from()) || !workflow.statuses().contains(transition.to())) {
                return false;
            }
        }

        return true;
    }
}
