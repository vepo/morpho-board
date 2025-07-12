package io.vepo.morphoboard.workflow;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("status")
@Produces(MediaType.APPLICATION_JSON)
public class StatusResource {

    public static final record StatusResponse(long id, String name) {
    }

    @GET
    public List<StatusResponse> listAll() {
        return WorkflowStatus.<WorkflowStatus>streamAll()
                             .map(StatusResource::toResponse)
                             .toList();
    }

    private static final StatusResponse toResponse(WorkflowStatus status) {
        return new StatusResponse(status.id, status.name);
    }
}
