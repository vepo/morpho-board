package dev.vepo.morphoboard.workflow;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.ResponseStatus;

import dev.vepo.morphoboard.user.Role;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/workflows")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@DenyAll
public class WorkflowEndpoint {

    public static record TransitionResponse(String from, String to) {}

    public static record TransitionRequest(String from, String to) {}

    @ValidTransitions
    public static record CreateWorkflowRequest(@NotBlank(message = "Workflow name cannot be empty!") @Size(min = 5, max = 64, message = "Workflow name should have at least 5 caracters and at most 64!") String name,
                                               @NotEmpty(message = "No status defined!") @Size(min = 2, message = "At least 2 statuses must be defined!") List<String> statuses,
                                               @NotNull(message = "No start status is defined!") String start,
                                               @NotEmpty List<TransitionRequest> transitions) {}

    public static record WorkflowResponse(long id,
                                          String name,
                                          List<String> statuses,
                                          String start,
                                          List<TransitionResponse> transitions) {}

    public static WorkflowResponse toResponse(Workflow workflow) {
        return new WorkflowResponse(workflow.getId(),
                                    workflow.getName(),
                                    workflow.getStatuses()
                                            .stream()
                                            .sorted(Comparator.comparing(WorkflowStatus::getId))
                                            .map(status -> status.getName())
                                            .collect(Collectors.toList()),
                                    workflow.getStart()
                                            .getName(),
                                    workflow.getTransitions()
                                            .stream()
                                            .map(transition -> new TransitionResponse(transition.getFrom().getName(),
                                                                                      transition.getTo().getName()))
                                            .collect(Collectors.toList()));
    }

    private final WorkflowRepository repository;

    @Inject
    public WorkflowEndpoint(WorkflowRepository repository) {
        this.repository = repository;
    }

    @GET
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public List<WorkflowResponse> listWorkflows() {
        return repository.findAll()
                         .map(workflow -> toResponse((Workflow) workflow))
                         .toList();
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @RolesAllowed({ Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public WorkflowResponse create(@Valid CreateWorkflowRequest request) {
        var statuses = request.statuses()
                              .stream()
                              .map(status -> repository.findStatusByName(status)
                                                       .orElseGet(() -> {
                                                           var dbStatus = new WorkflowStatus(status);
                                                           repository.save(dbStatus);
                                                           return dbStatus;
                                                       }))
                              .collect(Collectors.toMap(w -> w.getName(), Function.identity()));

        Workflow workflow = new Workflow(request.name(),
                                         statuses.values()
                                                 .stream()
                                                 .toList(),
                                         statuses.get(request.start),
                                         request.transitions().stream()
                                                .map(transition -> new WorkflowTransition(statuses.get(transition.from),
                                                                                          statuses.get(transition.to)))
                                                .collect(Collectors.toList()));
        repository.save(workflow);
        return toResponse(workflow);
    }
}
