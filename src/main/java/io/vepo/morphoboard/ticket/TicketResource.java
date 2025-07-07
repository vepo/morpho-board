package io.vepo.morphoboard.ticket;

import java.util.List;

import io.vepo.morphoboard.user.User;
import io.vepo.morphoboard.ticket.Project;
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
        Long assigneeId,
        Long projectId
    ) {}

    @POST
    @Transactional
    public Response create(CreateTicketRequest request) {
        if (request.title() == null || request.description() == null || request.categoryId() == null || request.statusId() == null || request.authorId() == null || request.projectId() == null) {
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

    public static record MoveTicketRequest(Long toStageId) {}

    @PATCH
    @Path("/{id}/move")
    @Transactional
    public Response moveTicket(@PathParam("id") Long id, MoveTicketRequest request) {
        Ticket ticket = repository.findById(id);
        if (ticket == null) throw new NotFoundException();
        if (request.toStageId() == null) throw new BadRequestException("Destino não informado");
        WorkflowStage fromStage = ticket.workflowStage;
        WorkflowStage toStage = WorkflowStage.findById(request.toStageId());
        if (toStage == null) throw new BadRequestException("Destino inválido");
        boolean allowed = WorkflowTransition
            .find("workflow = ?1 and fromStage = ?2 and toStage = ?3", toStage.workflow, fromStage, toStage)
            .firstResultOptional()
            .isPresent();
        if (!allowed) throw new BadRequestException("Transição não permitida");
        ticket.workflowStage = toStage;
        return Response.ok(ticket).build();
    }
} 