package io.vepo.morphoboard.ticket;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {
    @GET
    public List<Project> listAll() {
        return Project.listAll();
    }

    public static record CreateProjectRequest(String name, String description) {}

    @POST
    @Transactional
    public Response create(CreateProjectRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nome do projeto é obrigatório").build();
        }
        Project project = new Project();
        project.name = request.name();
        project.description = request.description();
        project.persist();
        return Response.status(Response.Status.CREATED).entity(project).build();
    }
} 