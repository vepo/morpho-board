package dev.vepo.morphoboard.ticket;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.ResponseStatus;

import dev.vepo.morphoboard.project.ProjectRepository;
import dev.vepo.morphoboard.user.Role;
import dev.vepo.morphoboard.user.User;
import dev.vepo.morphoboard.user.UserRepository;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
    private TicketRepository repository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private TicketHistoryRepository historyRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Context
    private SecurityContext securityContext;

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
            return repository.findAll()
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
        return repository.findById(id)
                         .map(TicketResponse::load)
                         .orElseThrow(() -> new NotFoundException(String.format("Ticket does not found! ticketId=%d", id)));
    }

    @GET
    @Path("/{id}/expanded")
    @Transactional
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketExpandedResponse findExpandedById(@PathParam("id") Long id) {
        return repository.findById(id)
                         .map(TicketExpandedResponse::load)
                         .orElseThrow(() -> new NotFoundException(String.format("Ticket does not found! ticketId=%d", id)));
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketResponse create(@Valid @Parameter(name = "request") CreateTicketRequest request) {
        var project = projectRepository.findById(request.projectId())
                                       .orElseThrow(() -> new NotFoundException("Projeto não encontrado"));
        var author = userRepository.findByEmail(securityContext.getUserPrincipal().getName())
                                   .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        var ticket = new Ticket(request.title(),
                                request.description(),
                                categoryRepository.findById(request.categoryId())
                                                  .orElseThrow(() -> new NotFoundException("Categoria não encontrada")),
                                author,
                                null,
                                project,
                                project.getWorkflow().getStart());
        repository.save(ticket);
        // Registrar histórico de criação
        var history = new TicketHistory(ticket, author, "Ticket criado", Instant.now());
        historyRepository.save(history);
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
        Ticket entity = repository.findById(id)
                                  .orElseThrow(() -> new NotFoundException("Ticket not found!"));
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setCategory(categoryRepository.findById(request.categoryId()).orElseThrow(() -> new NotFoundException()));
        entity.setAssignee(request.assigneeId() != null ? userRepository.findById(request.assigneeId()).orElseThrow(() -> new NotFoundException()) : null);
        entity.setUpdatedAt(LocalDateTime.now());
        // Pega usuário autenticado via JWT
        String email = securityContext.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException());
        // Registrar histórico de edição
        var history = new TicketHistory(entity, user, "Ticket editado", Instant.now());
        historyRepository.save(history);
        return TicketResponse.load(entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({ Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public void delete(@PathParam("id") Long id) {
        repository.delete(id);
    }

    @GET
    @Path("/{id}/comments")
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public List<CommentResponse> listComments(@PathParam("id") Long id) {
        return repository.findById(id)
                         .orElseThrow(() -> new NotFoundException())
                         .getComments()
                         .stream()
                         .map(CommentResponse::load)
                         .toList();
    }

    @POST
    @Path("/{id}/comments")
    @Transactional
    @ResponseStatus(201)
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public CommentResponse addComment(@PathParam("id") Long id, CommentRequest request) {
        var ticket = repository.findById(id)
                               .orElseThrow(() -> new NotFoundException());
        // Pega usuário autenticado via JWT
        var email = securityContext.getUserPrincipal().getName();
        var user = userRepository.findByEmail(email)
                                 .orElseThrow(() -> new NotFoundException());
        var comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(user);
        comment.setCreatedAt(Instant.now());
        ticket.getComments().add(comment);
        // Registrar histórico de comentário
        var history = new TicketHistory(ticket, user, "Comentário adicionado", Instant.now());
        historyRepository.save(history);
        return CommentResponse.load(comment);
    }

    @PATCH
    @Path("/{id}/move")
    @Transactional
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketResponse moveTicket(@PathParam("id") Long id,
                                     @Valid @Parameter(name = "request") MoveTicketRequest request) {
        var ticket = repository.findById(id)
                               .orElseThrow(() -> new NotFoundException());

        if (Objects.isNull(request) || Objects.isNull(request.to())) {
            throw new BadRequestException("Destino não informado");
        }

        var to = ticket.getProject()
                       .getWorkflow()
                       .getStatuses()
                       .stream()
                       .filter(s -> s.getId() == request.to())
                       .findFirst()
                       .orElseThrow(() -> new BadRequestException(String.format("Destino inválido! id=%d", request.to())));
        if (ticket.getProject()
                  .getWorkflow()
                  .getTransitions()
                  .stream()
                  .noneMatch(t -> t.getTo().getId() == request.to() && t.getFrom().getId() == ticket.getStatus().getId())) {
            throw new BadRequestException("Transição não permitida");
        }
        ticket.setStatus(to);
        // Pega usuário autenticado via JWT
        var email = securityContext.getUserPrincipal().getName();
        var user = userRepository.findByEmail(email)
                                 .orElseThrow(() -> new NotFoundException());
        // Registrar histórico de movimentação
        var history = new TicketHistory(ticket, user, "Ticket movido para status: " + to.getName(), Instant.now());
        historyRepository.save(history);
        return TicketResponse.load(ticket);
    }

    @GET
    @Path("/{id}/history")
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public List<TicketHistoryResponse> getHistory(@PathParam("id") Long id) {
        return repository.findById(id)
                         .orElseThrow(() -> new NotFoundException())
                         .getHistory()
                         .stream()
                         .map(TicketHistoryResponse::load)
                         .toList();
    }
}