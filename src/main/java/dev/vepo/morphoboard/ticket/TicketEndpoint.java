package dev.vepo.morphoboard.ticket;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.jboss.resteasy.reactive.ResponseStatus;

import dev.vepo.morphoboard.project.Project;
import dev.vepo.morphoboard.user.Role;
import dev.vepo.morphoboard.user.User;
import dev.vepo.morphoboard.workflow.WorkflowStatus;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@DenyAll
public class TicketEndpoint {

    @Inject
    TicketRepository repository;

    @Inject
    TicketHistoryRepository historyRepository;

    @Context
    SecurityContext securityContext;

    private static final String NUMBER_REGEX = "\\d+";
    private static final Predicate<String> IS_NUMBER = Pattern.compile(NUMBER_REGEX).asMatchPredicate();

    @GET
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public List<TicketResponse> listAll(@QueryParam("status") String status) {
        if (Objects.nonNull(status) && IS_NUMBER.test(status)) {
            return repository.findByStatusId(Long.parseLong(status))
                             .map(TicketResponse::load)
                             .toList();
        } else if (Objects.nonNull(status) && !status.isBlank()) {
            return repository.findByStatusName(status)
                             .map(TicketResponse::load)
                             .toList();
        } else {
            return repository.streamAll()
                             .map(TicketResponse::load)
                             .toList();
        }
    }

    @GET
    @Path("search")
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public List<TicketResponse> search(@QueryParam("term") String term,
                                       @QueryParam("statusId") @DefaultValue("-1") long statusId) {

        return repository.search(Optional.ofNullable(term)
                                         .filter(Predicate.not(String::isBlank))
                                         .map(String::trim)
                                         .map(s -> s.split("\\s+"))
                                         .orElseGet(() -> new String[] {}),
                                 statusId)
                         .map(TicketResponse::load)
                         .toList();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketResponse findById(@PathParam("id") Long id) {
        return repository.findByIdOptional(id)
                         .map(TicketResponse::load)
                         .orElseThrow(() -> new NotFoundException(String.format("Ticket does not found! ticketId=%d", id)));
    }

    @GET
    @Path("/{id}/expanded")
    @Transactional
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketExpandedResponse findExpandedById(@PathParam("id") Long id) {
        return repository.findByIdOptional(id)
                         .map(TicketExpandedResponse::load)
                         .orElseThrow(() -> new NotFoundException(String.format("Ticket does not found! ticketId=%d", id)));
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketResponse create(CreateTicketRequest request) {
        if (request.title() == null || request.description() == null || request.categoryId() == null || request.projectId() == null) {
            throw new BadRequestException("Campos obrigatórios não podem ser nulos");
        }
        var project = Project.<Project>findByIdOptional(request.projectId())
                             .orElseThrow(() -> new BadRequestException("Projeto não encontrado"));
        var author = User.<User>find("email", securityContext.getUserPrincipal().getName())
                         .firstResultOptional()
                         .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));
        var ticket = new Ticket(request.title(),
                                request.description(),
                                Category.<Category>findByIdOptional(request.categoryId())
                                        .orElseThrow(() -> new BadRequestException("Categoria não encontrada")),
                                author, 
                                null,
                                project,
                                project.workflow.start);
        repository.persist(ticket);
        // Registrar histórico de criação
        var history = new TicketHistory(ticket, author, "Ticket criado", Instant.now());
        historyRepository.persist(history);
        return TicketResponse.load(ticket);
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
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
        // Pega usuário autenticado via JWT
        String email = securityContext.getUserPrincipal().getName();
        User user = User.find("email", email).firstResult();
        // Registrar histórico de edição
        var history = new TicketHistory(entity, user, "Ticket editado", Instant.now());
        historyRepository.persist(history);
        return TicketResponse.load(entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({ Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public void delete(@PathParam("id") Long id) {
        repository.deleteById(id);
    }

    @GET
    @Path("/{id}/comments")
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public List<CommentResponse> listComments(@PathParam("id") Long id) {
        Ticket ticket = repository.findById(id);
        if (ticket == null) {
            throw new NotFoundException();
        }
        return ticket.comments.stream()
                              .map(CommentResponse::load)
                              .toList();
    }

    @POST
    @Path("/{id}/comments")
    @Transactional
    @ResponseStatus(201)
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public CommentResponse addComment(@PathParam("id") Long id, CommentRequest request) {
        Ticket ticket = repository.findById(id);
        if (ticket == null) {
            throw new NotFoundException();
        }
        // Pega usuário autenticado via JWT
        String email = securityContext.getUserPrincipal().getName();
        User user = User.find("email", email).firstResult();
        var comment = new Comment();
        comment.ticket = ticket;
        comment.author = user;
        comment.createdAt = Instant.now();
        ticket.comments.add(comment);
        // Registrar histórico de comentário
        var history = new TicketHistory(ticket, user, "Comentário adicionado", Instant.now());
        historyRepository.persist(history);
        return CommentResponse.load(comment);
    }

    @PATCH
    @Path("/{id}/move")
    @Transactional
    public TicketResponse moveTicket(@PathParam("id") Long id, MoveTicketRequest request) {
        Ticket ticket = repository.findById(id);
        if (Objects.isNull(ticket)) {
            throw new NotFoundException(String.format("Ticket not found! id=%d", id));
        }

        if (Objects.isNull(request) || Objects.isNull(request.to())) {
            throw new BadRequestException("Destino não informado");
        }

        WorkflowStatus to = ticket.project.workflow.statuses.stream()
                                                            .filter(s -> s.id == request.to())
                                                            .findFirst()
                                                            .orElseThrow(() -> new BadRequestException(String.format("Destino inválido! id=%d", request.to())));
        if (ticket.project.workflow.transitions.stream()
                                               .noneMatch(t -> t.to.id == request.to() && t.from.id == ticket.status.id)) {
            throw new BadRequestException("Transição não permitida");
        }
        ticket.status = to;
        // Pega usuário autenticado via JWT
        String email = securityContext.getUserPrincipal().getName();
        User user = User.find("email", email).firstResult();
        // Registrar histórico de movimentação
        var history = new TicketHistory(ticket, user, "Ticket movido para status: " + to.name, Instant.now());
        historyRepository.persist(history);
        return TicketResponse.load(ticket);
    }

    @GET
    @Path("/{id}/history")
    public List<TicketHistoryResponse> getHistory(@PathParam("id") Long id) {
        Ticket ticket = repository.findById(id);
        if (ticket == null) {
            throw new NotFoundException();
        }
        return ticket.history.stream()
                             .map(TicketHistoryResponse::load)
                             .toList();
    }
}