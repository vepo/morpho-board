package com.seuprojeto.controller;

import com.seuprojeto.model.Change;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/changes")
@Produces("application/json")
@Consumes("application/json")
public class ChangeController {

    @GET
    public List<Change> getAll() {
        return Change.listAll();
    }

    @POST
    public Response create(Change change) {
        change.persist();
        return Response.ok(change).status(201).build();
    }
}