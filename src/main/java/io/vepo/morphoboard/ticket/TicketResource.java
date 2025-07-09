package io.vepo.morphoboard.ticket;

import java.time.Instant;
import java.util.List;

import io.vepo.morphoboard.project.Project;
import io.vepo.morphoboard.user.User;
import io.vepo.morphoboard.workflow.Workflow;
import io.vepo.morphoboard.workflow.WorkflowStage;
import io.vepo.morphoboard.workflow.WorkflowTransition;
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

    public static record MoveTicketRequest(Long toStageId) {
    }

    public static record TicketResponse(long id) {
    }

    public static record CommentResponse(long id, UserResponse author, String content, long createdAt) {
    }

    public static record UserResponse(long id, String email) {
    }

    private static final TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(ticket.id);
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
    @Path("/{id}")
    public TicketResponse findById(@PathParam("id") Long id) {
        return repository.findByIdOptional(id)
                         .map(TicketResource::toResponse)
                         .orElseThrow(() -> new NotFoundException(String.format("Ticket does not found! ticketId=%d", id)));
    }

    @POST
    @Transactional
    public Response create(CreateTicketRequest request) {
        if (request.title() == null || request.description() == null || request.categoryId() == null || request.authorId() == null
                || request.projectId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Campos obrigatórios não podem ser nulos").build();
        }
        Project project = Project.findById(request.projectId());
        if (project == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Projeto não encontrado").build();
        }
        Workflow workflow = Workflow.find("project = ?1 and defaultWorkflow = true", project).firstResult();
        if (workflow == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Workflow padrão não definido para o projeto").build();
        }
        WorkflowStage firstStage = WorkflowStage.find("workflow = ?1 order by position asc", workflow).firstResult();
        if (firstStage == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Workflow padrão do projeto não possui etapas").build();
        }
        Ticket ticket = new Ticket();
        ticket.title = request.title();
        ticket.description = request.description();
        ticket.category = Category.findById(request.categoryId());
        ticket.workflowStage = firstStage;
        ticket.project = project;
        ticket.author = User.findById(request.authorId());
        ticket.assignee = request.assigneeId() != null ? User.findById(request.assigneeId()) : null;
        repository.persist(ticket);
        return Response.status(Response.Status.CREATED).entity(ticket).build();
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
        entity.updatedAt = java.time.LocalDateTime.now();
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
        if (request.toStageId() == null)
            throw new BadRequestException("Destino não informado");
        WorkflowStage fromStage = ticket.workflowStage;
        WorkflowStage toStage = WorkflowStage.findById(request.toStageId());
        if (toStage == null)
            throw new BadRequestException("Destino inválido");
        boolean allowed = WorkflowTransition
                                            .find("workflow = ?1 and fromStage = ?2 and toStage = ?3", ticket.project.workflow, fromStage, toStage)
                                            .firstResultOptional()
                                            .isPresent();
        if (!allowed)
            throw new BadRequestException("Transição não permitida");
        ticket.workflowStage = toStage;
        return Response.ok(ticket).build();
    }
}