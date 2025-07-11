package io.vepo.morphoboard.workflow;

import java.util.List;

import io.vepo.morphoboard.project.Project;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("projects/{projectId}/stages")
public class StageProjectResource {
    public static record ProjectStageResponse(long id, String name, boolean start, List<Long> moveable) {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectStageResponse> getStages(@PathParam("projectId") long projectId) {
        return Project.<Project>findByIdOptional(projectId)
                      .map(project -> project.workflow.stages.stream()
                                                             .map(stage -> new ProjectStageResponse(stage.id,
                                                                                                    stage.name,
                                                                                                    project.workflow.start.id == stage.id,
                                                                                                    project.workflow.transitions.stream()
                                                                                                                                .filter(t -> t.from.id == stage.id)
                                                                                                                                .map(s -> s.to.id)
                                                                                                                                .toList()))
                                                             .toList())
                      .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));
    }
}
