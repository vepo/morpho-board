package io.vepo.morphoboard.ticket;

import java.util.List;

import io.vepo.morphoboard.ticket.TicketResource.TicketResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/projects/{projectId}/tickets")
public class FindTicketByProjectEndpoint {

    @GET
    public List<TicketResponse> findByProjectId(@PathParam("projectId") long projectId) {
        return Ticket.findByProject(projectId)
                     .map(TicketResource::toResponse)
                     .toList();
    }
}
