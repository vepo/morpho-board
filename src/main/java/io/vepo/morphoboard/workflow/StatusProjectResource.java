package io.vepo.morphoboard.workflow;

import java.util.List;

import io.vepo.morphoboard.project.Project;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("projects/{projectId}/status")
public class StatusProjectResource {
    public static record ProjectStatusResponse(long id, String name, boolean start, List<Long> moveable, List<Long> accepts) {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectStatusResponse> getStatus(@PathParam("projectId") long projectId) {
        return Project.<Project>findByIdOptional(projectId)
                      .map(project -> project.workflow.statuses.stream()
                                                               .map(status -> new ProjectStatusResponse(status.id,
                                                                                                        status.name,
                                                                                                        project.workflow.start.id == status.id,
                                                                                                        project.workflow.transitions.stream()
                                                                                                                                    .filter(t -> t.from.id == status.id)
                                                                                                                                    .map(s -> s.to.id)
                                                                                                                                    .toList(),
                                                                                                        project.workflow.transitions.stream()
                                                                                                                                    .filter(t -> t.to.id == status.id)
                                                                                                                                    .map(s -> s.from.id)
                                                                                                                                    .toList()))
                                                               .toList())
                      .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));
    }
}
