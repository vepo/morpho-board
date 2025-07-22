package dev.vepo.morphoboard.project;

import java.util.List;

import org.jboss.resteasy.reactive.ResponseStatus;

import dev.vepo.morphoboard.workflow.Workflow;
import dev.vepo.morphoboard.workflow.WorkflowResource;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {
    public static record CreateProjectRequest(String name, String description, long workflowId) {
    }

    public static record ProjectResponse(long id, String name, String description, WorkflowResponse workflow) {

    }

    public static record WorkflowResponse(long id, String name) {
    }

    private static ProjectResponse toResponse(Project project) {
        return new ProjectResponse(project.id, project.name, project.description, new WorkflowResponse(project.workflow.id, project.workflow.name));
    }

    @GET
    public List<ProjectResponse> listAll() {
        return Project.streamAll()
                      .map(project -> toResponse((Project) project))
                      .toList();
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    public ProjectResponse create(CreateProjectRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BadRequestException("Project name cannot be empty");
        }
        var workflow = Workflow.<Workflow>findById(request.workflowId());
        if (workflow == null) {
            throw new BadRequestException("Workflow with ID " + request.workflowId() + " does not exist");
        }
        
        Project project = new Project();
        project.name = request.name();
        project.description = request.description();
        project.workflow = workflow;
        project.persist();
        return toResponse(project);
    }

    @GET
    @Path("{id}")
    public ProjectResponse get(long id) {
        Project project = Project.findById(id);
        if (project == null) {
            throw new NotFoundException("Project with ID " + id + " does not exist"); 
        }
        return toResponse(project);
    }

    @GET
    @Path("{id}/workflow")
    public WorkflowResource.WorkflowResponse getWorkflow(long id) {
        Project project = Project.findById(id);
        if (project == null) {
            throw new NotFoundException("Project with ID " + id + " does not exist");
        }
        return WorkflowResource.toResponse(project.workflow);
    }
}