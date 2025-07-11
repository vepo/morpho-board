package io.vepo.morphoboard.ticket;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/statuses")
@Produces(MediaType.APPLICATION_JSON)
public class StatusResource {
    public static final record StatusResponse(String name) {
    }

    private static StatusResponse toResponse(Status status) {
        return new StatusResponse(status.name);
    }

    @Inject
    StatusRepository repository;

    @GET
    public List<StatusResponse> listAll() {
        return repository.streamAll()
                         .map(StatusResource::toResponse)
                         .toList();
    }
}