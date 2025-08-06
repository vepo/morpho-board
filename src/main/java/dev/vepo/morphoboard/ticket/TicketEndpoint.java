package dev.vepo.morphoboard.ticket;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.vepo.morphoboard.categories.CategoryRepository;
import dev.vepo.morphoboard.project.ProjectRepository;
import dev.vepo.morphoboard.ticket.business.TicketHistoryService;
import dev.vepo.morphoboard.ticket.comments.Comment;
import dev.vepo.morphoboard.ticket.comments.CommentRequest;
import dev.vepo.morphoboard.ticket.comments.CommentResponse;
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

    private static final Logger logger = LoggerFactory.getLogger(TicketEndpoint.class);
    private static final String NUMBER_REGEX = "\\d+";
    private static final Predicate<String> IS_NUMBER = Pattern.compile(NUMBER_REGEX).asMatchPredicate();

    private static final Supplier<NotFoundException> ticketNotFound(long ticketId) {
        return () -> new NotFoundException(String.format("Ticket does not found! ticketId=%d", ticketId));
    }

    private static final Supplier<NotFoundException> userNotFound(long userId) {
        return () -> new NotFoundException(String.format("User does not found! userId=%d", userId));
    }

    private static final Supplier<NotFoundException> userNotFound(String email) {
        return () -> new NotFoundException(String.format("User does not found! email=%s", email));
    }

    private static final Supplier<NotFoundException> projectNotFound(long projectId) {
        return () -> new NotFoundException(String.format("Project does not found! projectId=%d", projectId));
    }

    private static final Supplier<NotFoundException> categoryNotFound(long categoryId) {
        return () -> new NotFoundException(String.format("Category does not found! categoryId=%d", categoryId));
    }

    private TicketRepository repository;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private CategoryRepository categoryRepository;
    private TicketHistoryService historyService;
    private SecurityContext securityContext;

    @Inject
    public TicketEndpoint(TicketRepository repository,
                          UserRepository userRepository,
                          ProjectRepository projectRepository,
                          CategoryRepository categoryRepository,
                          TicketHistoryService historyService,
                          @Context SecurityContext securityContext) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.historyService = historyService;
        this.securityContext = securityContext;
    }

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
                         .orElseThrow(ticketNotFound(id));
    }

    @GET
    @Path("/{id}/expanded")
    @Transactional
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketExpandedResponse findExpandedById(@PathParam("id") Long id) {
        return TicketExpandedResponse.load(repository.findById(id)
                                                     .orElseThrow(ticketNotFound(id)),
                                           repository.findHistoryByTicketId(id)
                                                     .toList());
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketResponse create(@Valid CreateTicketRequest request) {
        var project = projectRepository.findById(request.projectId())
                                       .orElseThrow(projectNotFound(request.projectId()));
        var author = userRepository.findByEmail(securityContext.getUserPrincipal().getName())
                                   .orElseThrow(userNotFound(securityContext.getUserPrincipal().getName()));
        var ticket = new Ticket(request.title(),
                                request.description(),
                                categoryRepository.findById(request.categoryId())
                                                  .orElseThrow(categoryNotFound(request.categoryId())),
                                author,
                                null,
                                project,
                                project.getWorkflow().getStart());
        repository.save(ticket);
        // Log ticket creation
        historyService.logTicketCreated(ticket, author);
        return TicketResponse.load(ticket);
    }

    @POST
    @Path("/{id}")
    @Transactional
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketResponse update(@PathParam("id") Long id, @Valid UpdateTicketRequest request) {
        if (request.title() == null || request.description() == null || request.categoryId() == null) {
            throw new BadRequestException("Campos obrigatórios não podem ser nulos");
        }
        Ticket entity = repository.findById(id)
                                  .orElseThrow(ticketNotFound(id));

        // Track changes for history
        StringBuilder changes = new StringBuilder();
        boolean hasChanges = false;

        // Check title change
        if (!entity.getTitle().equals(request.title())) {
            changes.append("title");
            hasChanges = true;
        }

        // Check description change
        if (!entity.getDescription().equals(request.description())) {
            if (hasChanges)
                changes.append(", ");
            changes.append("description");
            hasChanges = true;
        }

        // Check category change
        var newCategory = categoryRepository.findById(request.categoryId()).orElseThrow(categoryNotFound(request.categoryId()));
        if (!entity.getCategory().equals(newCategory)) {
            if (hasChanges)
                changes.append(", ");
            changes.append("category");
            hasChanges = true;
        }

        // Update entity
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setCategory(newCategory);
        entity.setUpdatedAt(Instant.now());

        // Get authenticated user
        String email = securityContext.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email).orElseThrow(userNotFound(email));

        // Log changes if any
        if (hasChanges) {
            historyService.logTicketUpdated(entity, user, changes.toString());
        }

        return TicketResponse.load(entity);
    }

    @PATCH
    @Path("/{id}/assignee")
    @Transactional
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketResponse updateAssignee(@PathParam("id") Long id, @Valid UpdateAssigneeRequest request) {
        Ticket entity = repository.findById(id)
                                  .orElseThrow(ticketNotFound(id));

        // Get new assignee
        User newAssignee = userRepository.findById(request.assigneeId())
                                         .orElseThrow(userNotFound(request.assigneeId()));

        // Track assignee change
        String fromAssignee = entity.getAssignee() != null ? entity.getAssignee().getName() : null;
        String toAssignee = newAssignee.getName();

        // Update entity
        entity.setAssignee(newAssignee);
        entity.setUpdatedAt(Instant.now());

        // Get authenticated user
        String email = securityContext.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email).orElseThrow(userNotFound(email));

        // Log assignee change
        historyService.logAssigneeChanged(entity, user, fromAssignee, toAssignee);

        return TicketResponse.load(entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({ Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public void delete(@PathParam("id") Long id) {
        Ticket ticket = repository.findById(id)
                                  .orElseThrow(ticketNotFound(id));

        // Get authenticated user
        String email = securityContext.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email).orElseThrow(userNotFound(email));

        // Log ticket deletion
        historyService.logTicketDeleted(ticket, user);

        repository.delete(id);
    }

    @GET
    @Path("/{id}/comments")
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public List<CommentResponse> listComments(@PathParam("id") Long id) {
        return repository.findCommentsByTicketId(id)
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
                               .orElseThrow(ticketNotFound(id));
        // Pega usuário autenticado via JWT
        var email = securityContext.getUserPrincipal().getName();
        var user = userRepository.findByEmail(email)
                                 .orElseThrow(userNotFound(email));
        var comment = new Comment(ticket, user, request.content());

        // Save comment
        comment = repository.saveComment(comment);

        // Log comment addition
        historyService.logCommentAdded(ticket, user);
        return CommentResponse.load(comment);
    }

    @POST
    @Path("/{id}/move")
    @Transactional
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public TicketResponse moveTicket(@PathParam("id") Long id,
                                     @Valid MoveTicketRequest request) {
        logger.debug("Moving ticket to a new status! ticketId={}, request={}", id, request);
        var ticket = repository.findById(id)
                               .orElseThrow(ticketNotFound(id));

        if (Objects.isNull(request) || Objects.isNull(request.to())) {
            throw new BadRequestException("Destino não informado");
        }
        logger.debug("Retrived ticket! ticket={}", ticket);
        var to = ticket.getProject()
                       .getWorkflow()
                       .getStatuses()
                       .stream()
                       .filter(s -> Objects.equals(s.getId(), request.to()))
                       .findFirst()
                       .orElseThrow(() -> new BadRequestException(String.format("Stage not defined in project! stageId=%d", request.to())));
        if (ticket.getProject()
                  .getWorkflow()
                  .getTransitions()
                  .stream()
                  .noneMatch(t -> t.getTo().equals(to) && t.getFrom().equals(ticket.getStatus()))) {
            throw new BadRequestException(String.format("New stage not acceptable by workflow! stageId=%d", request.to()));
        }
        logger.info("Valid transition of {} to {}", ticket, to);

        // Track status change
        String fromStatus = ticket.getStatus().getName();
        String toStatus = to.getName();

        ticket.setStatus(to);

        // Get authenticated user
        var email = securityContext.getUserPrincipal().getName();
        var user = userRepository.findByEmail(email)
                                 .orElseThrow(userNotFound(email));

        // Log status change
        historyService.logStatusChanged(ticket, user, fromStatus, toStatus);

        return TicketResponse.load(ticket);
    }

    @GET
    @Path("/{id}/history")
    @RolesAllowed({ Role.USER_ROLE, Role.ADMIN_ROLE, Role.PROJECT_MANAGER_ROLE })
    public List<TicketHistoryResponse> getHistory(@PathParam("id") Long id) {
        return repository.findHistoryByTicketId(id)
                         .map(TicketHistoryResponse::load)
                         .toList();
    }
}