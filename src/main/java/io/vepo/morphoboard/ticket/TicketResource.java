package io.vepo.morphoboard.ticket;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.jboss.resteasy.reactive.ResponseStatus;

import io.vepo.morphoboard.project.Project;
import io.vepo.morphoboard.user.User;
import io.vepo.morphoboard.workflow.WorkflowStatus;
import io.vepo.morphoboard.workflow.WorkflowTransition;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
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

@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {
    public static record CreateTicketRequest(String title,
                                             String description,
                                             Long categoryId,
                                             Long authorId,
                                             Long assigneeId,
                                             Long projectId) {
    }

    public static record CommentRequest(String content, Long authorId) {
    }

    public static record UpdateTicketRequest(String title,
                                             String description,
                                             Long categoryId,
                                             Long assigneeId) {
    }

    public static record MoveTicketRequest(Long to) {
    }

    public static record TicketResponse(long id,
                                        String title,
                                        String description,
                                        Long category,
                                        Long author,
                                        Long assignee,
                                        Long project,
                                        Long status) {
    }

    public static record CommentResponse(long id, UserResponse author, String content, long createdAt) {
    }

    public static record UserResponse(long id, String email) {
    }

    public static final TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(ticket.id,
                                  ticket.title,
                                  ticket.description,
                                  ticket.category != null ? ticket.category.id : null,
                                  ticket.author != null ? ticket.author.id : null,
                                  ticket.assignee != null ? ticket.assignee.id : null,
                                  ticket.project != null ? ticket.project.id : null,
                                  ticket.status != null ? ticket.status.id : null);
    }

    private static final CommentResponse toResponse(Comment comment) {
        return new CommentResponse(comment.id, toResponse(comment.author), comment.content, comment.createdAt.toEpochMilli());
    }

    private static UserResponse toResponse(User user) {
        return new UserResponse(user.id, user.email);
    }

    @Inject
    TicketRepository repository;

    @GET
    public List<TicketResponse> listAll(@QueryParam("status") Long statusId) {
        if (statusId != null) {
            return repository.stream("status.id", statusId)
                             .map(TicketResource::toResponse)
                             .toList();
        }
        return repository.streamAll()
                         .map(TicketResource::toResponse)
                         .toList();
    }

    @GET
    @Path("search")
    public List<TicketResponse> search(@QueryParam("term") String term,
                                       @QueryParam("statusId") @DefaultValue("-1") long statusId) {

        return repository.search(Optional.ofNullable(term)
                                         .filter(Predicate.not(String::isBlank))
                                         .map(String::trim)
                                         .map(s -> s.split("\\s+"))
                                         .orElseGet(() -> new String[] {}),
                                 statusId)
                         .map(TicketResource::toResponse)
                         .toList();
    }

    @GET
    @Path("/{id}")
    public TicketResponse findById(@PathParam("id") Long id) {
        return repository.findByIdOptional(id)
                         .map(TicketResource::toResponse)
                         .orElseThrow(() -> new NotFoundException(String.format("Ticket does not found! ticketId=%d", id)));
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    public TicketResponse create(CreateTicketRequest request) {
        if (request.title() == null || request.description() == null || request.categoryId() == null || request.authorId() == null
                || request.projectId() == null) {
            throw new BadRequestException("Campos obrigatórios não podem ser nulos");
        }
        Project project = Project.findById(request.projectId());
        if (project == null) {
            throw new BadRequestException("Projeto não encontrado");
        }
        Ticket ticket = new Ticket();
        ticket.title = request.title();
        ticket.description = request.description();
        ticket.category = Category.findById(request.categoryId());
        ticket.status = project.workflow.start;
        ticket.project = project;
        ticket.author = User.findById(request.authorId());
        ticket.assignee = request.assigneeId() != null ? User.findById(request.assigneeId()) : null;
        repository.persist(ticket);
        return toResponse(ticket);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public TicketResponse update(@PathParam("id") Long id, UpdateTicketRequest request) {
        if (request.title() == null || request.description() == null || request.categoryId() == null) {
            throw new BadRequestException("Campos obrigatórios não podem ser nulos");
        }
        Ticket entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        entity.title = request.title();
        entity.description = request.description();
        entity.category = Category.findById(request.categoryId());
        entity.assignee = request.assigneeId() != null ? User.findById(request.assigneeId()) : null;
        entity.updatedAt = LocalDateTime.now();
        return toResponse(entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        repository.deleteById(id);
    }

    @GET
    @Path("/{id}/comments")
    public List<CommentResponse> listComments(@PathParam("id") Long id) {
        Ticket ticket = repository.findById(id);
        if (ticket == null) {
            throw new NotFoundException();
        }
        return ticket.comments.stream()
                              .map(TicketResource::toResponse)
                              .toList();
    }

    @POST
    @Path("/{id}/comments")
    @Transactional
    public Response addComment(@PathParam("id") Long id, CommentRequest request) {
        Ticket ticket = repository.findById(id);
        if (ticket == null) {
            throw new NotFoundException();
        }
        var comment = new Comment();
        comment.ticket = ticket;
        comment.author = User.findById(request.authorId());
        comment.createdAt = Instant.now();
        ticket.comments.add(comment);

        return Response.status(Response.Status.CREATED).entity(comment).build();
    }

    @PATCH
    @Path("/{id}/move")
    @Transactional
    public Response moveTicket(@PathParam("id") Long id, MoveTicketRequest request) {
        Ticket ticket = repository.findById(id);
        if (ticket == null)
            throw new NotFoundException();
        if (request.to() == null)
            throw new BadRequestException("Destino não informado");
        WorkflowStatus from = ticket.status;
        WorkflowStatus to = WorkflowStatus.findById(request.to());
        if (to == null)
            throw new BadRequestException("Destino inválido");
        boolean allowed = WorkflowTransition.find("workflow = ?1 and from = ?2 and to = ?3", ticket.project.workflow, from, to)
                                            .firstResultOptional()
                                            .isPresent();
        if (!allowed)
            throw new BadRequestException("Transição não permitida");
        ticket.status = to;
        return Response.ok(ticket).build();
    }
}