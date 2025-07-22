package dev.vepo.morphoboard.workflow;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.ResponseStatus;

import jakarta.transaction.Transactional;
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
public class WorkflowResource {

    public static record TransitionResponse(String from, String to) {
    }

    public static record TransitionRequest(String from, String to) {
    }

    public static record CreateWorkflowRequest(String name,
                                               List<String> statuses,
                                               String start,
                                               List<TransitionRequest> transitions) {
    }

    public static record WorkflowResponse(long id,
                                          String name,
                                          List<String> statuses,
                                          String start,
                                          List<TransitionResponse> transitions) {
    }

    public static WorkflowResponse toResponse(Workflow workflow) {
        return new WorkflowResponse(workflow.id,
                                    workflow.name,
                                    workflow.statuses.stream()
                                                     .sorted(Comparator.comparing(status -> status.id))
                                                     .map(status -> status.name)
                                                     .collect(Collectors.toList()),
                                    workflow.start.name,
                                    workflow.transitions.stream()
                                                        .map(transition -> new TransitionResponse(transition.from.name,
                                                                                                  transition.to.name))
                                                        .collect(Collectors.toList()));
    }

    @GET
    public List<WorkflowResponse> listWorkflows() {
        return Workflow.streamAll()
                       .map(workflow -> toResponse((Workflow) workflow))
                       .toList();
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    public WorkflowResponse createWorkflow(CreateWorkflowRequest request) {
        if (request.name == null || request.name.isBlank()) {
            throw new BadRequestException("Workflow name cannot be empty");
        }
        if (request.statuses == null || request.statuses.isEmpty()) {
            throw new BadRequestException("Workflow must have at least one status");
        }
        if (request.start == null || request.start == null || request.start.isBlank()) {
            throw new BadRequestException("Workflow must have a valid start status");
        }
        if (request.transitions == null || request.transitions.isEmpty()) {
            throw new BadRequestException("Workflow must have at least one transition");
        }
        if (request.transitions.stream().anyMatch(transition -> transition.from == null || transition.to == null)) {
            throw new BadRequestException("All transitions must have valid 'from' and 'to' status");
        }
        if (request.statuses.stream().anyMatch(status -> status == null || status.isBlank())) {
            throw new BadRequestException("All statuses must have valid names");
        }
        if (request.transitions.stream().anyMatch(transition -> transition.from.equals(transition.to))) {
            throw new BadRequestException("Transitions cannot loop back to the same status");
        }
        if (request.statuses.stream().noneMatch(status -> status.equals(request.start))) {
            throw new BadRequestException("Start status must be one of the defined statuses");
        }

        var statuses = request.statuses.stream()
                                       .map(status -> {
                                           var statusQuery = WorkflowStatus.find("name", status);
                                           if (statusQuery.count() == 0) {
                                               var dbStatus = new WorkflowStatus(status);
                                               dbStatus.persist();
                                               return dbStatus;
                                           } else {
                                               return statusQuery.firstResult();
                                           }
                                       })
                                       .collect(Collectors.toMap(w -> w.name, Function.identity()));

        Workflow workflow = new Workflow();
        workflow.name = request.name;
        workflow.statuses = statuses.values().stream().toList();
        workflow.start = statuses.get(request.start);
        workflow.transitions = request.transitions.stream()
                                                  .map(transition -> new WorkflowTransition(statuses.get(transition.from),
                                                                                            statuses.get(transition.to)))
                                                  .collect(Collectors.toList());
        workflow.persist();
        return toResponse(workflow);
    }
}
