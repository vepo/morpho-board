package dev.vepo.morphoboard.project;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.ResponseStatus;

import dev.vepo.morphoboard.user.Role;
import dev.vepo.morphoboard.workflow.Workflow;
import dev.vepo.morphoboard.workflow.WorkflowResource;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
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
    @GET
    @Transactional
    @RolesAllowed({
        Role.PROJECT_MANAGER_ROLE,
        Role.ADMIN_ROLE,
        Role.USER_ROLE })
    public List<ProjectResponse> listAllProjects() {
        return Project.<Project>streamAll()
                      .map(ProjectResponse::load)
                      .toList();
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @RolesAllowed(Role.PROJECT_MANAGER_ROLE)
    public ProjectResponse createProject(@Valid @Parameter(name = "request") CreateProjectRequest request) {
        var workflow = Workflow.<Workflow>findById(request.workflowId());
        if (workflow == null) {
            throw new BadRequestException("Workflow with ID " + request.workflowId() + " does not exist");
        }

        Project project = new Project(request.name(), request.description(), workflow);
        project.persist();
        return ProjectResponse.load(project);
    }

    @GET
    @Path("{projectId}")
    @RolesAllowed({ Role.PROJECT_MANAGER_ROLE, Role.ADMIN_ROLE, Role.USER_ROLE })
    public ProjectResponse getProjectById(@PathParam("projectId") long projectId) {
        Project project = Project.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project with ID " + projectId + " does not exist");
        }
        return ProjectResponse.load(project);
    }

    @GET
    @Path("{projectId}/workflow")
    @RolesAllowed({ Role.PROJECT_MANAGER_ROLE, Role.ADMIN_ROLE, Role.USER_ROLE })
    public WorkflowResource.WorkflowResponse getProjectWorkflow(@PathParam("projectId") long projectId) {
        Project project = Project.findById(projectId);
        if (project == null) {
            throw new NotFoundException("Project with ID " + projectId + " does not exist");
        }
        return WorkflowResource.toResponse(project.workflow);
    }

    @GET
    @Path("{projectId}/status")
    @RolesAllowed({ Role.PROJECT_MANAGER_ROLE, Role.ADMIN_ROLE, Role.USER_ROLE })
    public List<ProjectStatusResponse> getAllTicketStatusByProject(@PathParam("projectId") long projectId) {
        return Project.<Project>findByIdOptional(projectId)
                      .map(project -> project.workflow.statuses.stream()
                                                               .map(status -> new ProjectStatusResponse(status.id,
                                                                                                        status.name,
                                                                                                        project.workflow.start.id == status.id,
                                                                                                        project.workflow.transitions.stream()
                                                                                                                                    .filter(t -> t.from.id == status.id)
                                                                                                                                    .map(s -> s.to.id)
                                                                                                                                    .toList()))
                                                               .toList())
                      .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));
    }
}