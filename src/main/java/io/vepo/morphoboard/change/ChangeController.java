package io.vepo.morphoboard.change;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/changes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChangeController {

    private static final Logger logger = LoggerFactory.getLogger(ChangeController.class);

    @GET
    public List<Change> getAll() {
        return Change.listAll();
    }

    @POST
    @Transactional
    public Response create(Change change) {
        logger.info("Creating change: {}", change);
        change.persist();
        return Response.ok(change).status(201).build();
    }
}