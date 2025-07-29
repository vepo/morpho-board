package dev.vepo.morphoboard.project;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.ResponseStatus;

import dev.vepo.morphoboard.user.Role;
import dev.vepo.morphoboard.workflow.WorkflowRepository;
import dev.vepo.morphoboard.workflow.WorkflowResource;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@DenyAll
public class ProjectEndpoint {

    @Inject
    private ProjectRepository repository;

    @Inject
    private WorkflowRepository workflowRepository;

    @GET
    @Transactional
    @RolesAllowed({ Role.PROJECT_MANAGER_ROLE, Role.ADMIN_ROLE, Role.USER_ROLE })
    public List<ProjectResponse> listAllProjects() {
        return repository.findAll()
                         .map(ProjectResponse::load)
                         .toList();
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @RolesAllowed(Role.PROJECT_MANAGER_ROLE)
    public ProjectResponse createProject(@Valid @Parameter(name = "request") CreateProjectRequest request) {
        var workflow = workflowRepository.findById(request.workflowId())
                                         .orElseThrow(() -> new BadRequestException("Workflow with ID " + request.workflowId() + " does not exist"));

        Project project = new Project(request.name(), request.description(), workflow);
        repository.save(project);
        return ProjectResponse.load(project);
    }

    @GET
    @Path("{projectId}")
    @RolesAllowed({ Role.PROJECT_MANAGER_ROLE, Role.ADMIN_ROLE, Role.USER_ROLE })
    public ProjectResponse getProjectById(@PathParam("projectId") long projectId) {
        return ProjectResponse.load(repository.findById(projectId)
                                              .orElseThrow(() -> new NotFoundException("Project with ID " + projectId + " does not exist")));
    }

    @GET
    @Path("{projectId}/workflow")
    @RolesAllowed({ Role.PROJECT_MANAGER_ROLE, Role.ADMIN_ROLE, Role.USER_ROLE })
    public WorkflowResource.WorkflowResponse getProjectWorkflow(@PathParam("projectId") long projectId) {
        return WorkflowResource.toResponse(repository.findById(projectId)
                                                     .orElseThrow(() -> new NotFoundException("Project with ID " + projectId + " does not exist"))
                                                     .getWorkflow());
    }

    @GET
    @Path("{projectId}/status")
    @RolesAllowed({ Role.PROJECT_MANAGER_ROLE, Role.ADMIN_ROLE, Role.USER_ROLE })
    public List<ProjectStatusResponse> getAllTicketStatusByProject(@PathParam("projectId") long projectId) {
        return repository.findById(projectId)
                         .map(project -> project.getWorkflow()
                                                .getStatuses()
                                                .stream()
                                                .map(status -> ProjectStatusResponse.load(status, project.getWorkflow()))
                                                .toList())
                         .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));
    }
}