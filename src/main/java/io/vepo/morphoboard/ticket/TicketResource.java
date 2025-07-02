package io.vepo.morphoboard.ticket;

import java.util.List;

import io.vepo.morphoboard.user.User;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {
    @Inject
    TicketRepository repository;

    @GET
    public List<Ticket> listAll(@QueryParam("status") Long statusId) {
        if (statusId != null) {
            return repository.list("status.id", statusId);
        }
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    public Ticket findById(@PathParam("id") Long id) {
        return repository.findById(id);
    }

    public static record CreateTicketRequest(
        String title,
        String description,
        Long categoryId,
        Long statusId,
        Long authorId,
        Long assigneeId
    ) {}

    @POST
    @Transactional
    public Response create(CreateTicketRequest request) {
        if (request.title() == null || request.description() == null || request.categoryId() == null || request.statusId() == null || request.authorId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Campos obrigatórios não podem ser nulos").build();
        }
        Ticket ticket = new Ticket();
        ticket.title = request.title();
        ticket.description = request.description();
        ticket.category = Category.findById(request.categoryId());
        ticket.status = Status.findById(request.statusId());
        ticket.author = User.findById(request.authorId());
        ticket.assignee = request.assigneeId() != null ? User.findById(request.assigneeId()) : null;
        repository.persist(ticket);
        return Response.status(Response.Status.CREATED).entity(ticket).build();
    }

    public static record UpdateTicketRequest(
        String title,
        String description,
        Long categoryId,
        Long statusId,
        Long assigneeId
    ) {}

    @PUT
    @Path("/{id}")
    @Transactional
    public Ticket update(@PathParam("id") Long id, UpdateTicketRequest request) {
        if (request.title() == null || request.description() == null || request.categoryId() == null || request.statusId() == null) {
            throw new BadRequestException("Campos obrigatórios não podem ser nulos");
        }
        Ticket entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        entity.title = request.title();
        entity.description = request.description();
        entity.category = Category.findById(request.categoryId());
        entity.status = Status.findById(request.statusId());
        entity.assignee = request.assigneeId() != null ? User.findById(request.assigneeId()) : null;
        entity.updatedAt = java.time.LocalDateTime.now();
        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        repository.deleteById(id);
    }

    @PATCH
    @Path("/{id}/status")
    @Transactional
    public Ticket updateStatus(@PathParam("id") Long id, Status status) {
        Ticket entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        entity.status = status;
        return entity;
    }

    @GET
    @Path("/{id}/comments")
    public List<Comment> listComments(@PathParam("id") Long id) {
        Ticket ticket = repository.findById(id);
        if (ticket == null) {
            throw new NotFoundException();
        }
        return ticket.comments;
    }

    @POST
    @Path("/{id}/comments")
    @Transactional
    public Response addComment(@PathParam("id") Long id, Comment comment) {
        Ticket ticket = repository.findById(id);
        if (ticket == null) {
            throw new NotFoundException();
        }
        comment.ticket = ticket;
        ticket.comments.add(comment);
        return Response.status(Response.Status.CREATED).entity(comment).build();
    }
} 