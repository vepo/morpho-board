package io.vepo.morphoboard.workflow;

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

@Path("/api/workflows")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkflowResource {

    public static record TransitionResponse(String from, String to) {
    }

    public static record TransitionRequest(String from, String to) {
    }

    public static record CreateWorkflowRequest(String name,
                                               List<String> stages,
                                               String start,
                                               List<TransitionRequest> transitions) {
    }

    public static record WorkflowResponse(long id,
                                          String name,
                                          List<String> stages,
                                          String start,
                                          List<TransitionResponse> transitions) {
    }

    private static WorkflowResponse toResponse(Workflow workflow) {
        return new WorkflowResponse(workflow.id,
                                    workflow.name,
                                    workflow.stages.stream()
                                                   .map(stage -> stage.name)
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
        if (request.stages == null || request.stages.isEmpty()) {
            throw new BadRequestException("Workflow must have at least one stage");
        }
        if (request.start == null || request.start == null || request.start.isBlank()) {
            throw new BadRequestException("Workflow must have a valid start stage");
        }
        if (request.transitions == null || request.transitions.isEmpty()) {
            throw new BadRequestException("Workflow must have at least one transition");
        }
        if (request.transitions.stream().anyMatch(transition -> transition.from == null || transition.to == null)) {
            throw new BadRequestException("All transitions must have valid 'from' and 'to' stages");
        }
        if (request.stages.stream().anyMatch(stage -> stage == null || stage.isBlank())) {
            throw new BadRequestException("All stages must have valid names");
        }
        if (request.transitions.stream().anyMatch(transition -> transition.from.equals(transition.to))) {
            throw new BadRequestException("Transitions cannot loop back to the same stage");
        }
        if (request.stages.stream().noneMatch(stage -> stage.equals(request.start))) {
            throw new BadRequestException("Start stage must be one of the defined stages");
        }

        var stages = request.stages.stream()
                                   .map(stage -> {
                                       var stageQuery = WorkflowStage.find("name", stage);
                                       if (stageQuery.count() == 0) {
                                           var dbStage = new WorkflowStage(stage);
                                           dbStage.persist();
                                           return dbStage;
                                       } else {
                                           return stageQuery.firstResult();
                                       }
                                   })
                                   .collect(Collectors.toMap(w -> w.name, Function.identity()));

        Workflow workflow = new Workflow();
        workflow.name = request.name;
        workflow.stages = stages.values().stream().toList();
        workflow.start = stages.get(request.start);
        workflow.transitions = request.transitions.stream()
                                                  .map(transition -> new WorkflowTransition(stages.get(transition.from),
                                                                                            stages.get(transition.to)))
                                                  .collect(Collectors.toList());
        workflow.persist();
        return toResponse(workflow);
    }
}
