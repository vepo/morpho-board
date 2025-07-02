package io.vepo.morphoboard.ticket;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {
    @Inject
    TicketRepository repository;

    @GET
    public List<Ticket> listAll() {
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    public Ticket findById(@PathParam("id") Long id) {
        return repository.findById(id);
    }

    @POST
    @Transactional
    public Response create(Ticket ticket) {
        repository.persist(ticket);
        return Response.status(Response.Status.CREATED).entity(ticket).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Ticket update(@PathParam("id") Long id, Ticket ticket) {
        Ticket entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        entity.title = ticket.title;
        entity.description = ticket.description;
        entity.category = ticket.category;
        entity.status = ticket.status;
        entity.author = ticket.author;
        entity.assignee = ticket.assignee;
        entity.updatedAt = ticket.updatedAt;
        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        repository.deleteById(id);
    }
} 