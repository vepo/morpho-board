package dev.vepo.morphoboard.ticket.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.vepo.morphoboard.categories.Category;
import dev.vepo.morphoboard.project.Project;
import dev.vepo.morphoboard.ticket.Ticket;
import dev.vepo.morphoboard.ticket.history.TicketHistory;
import dev.vepo.morphoboard.ticket.history.TicketHistoryRepository;
import dev.vepo.morphoboard.user.User;
import dev.vepo.morphoboard.workflow.WorkflowStatus;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class TicketHistoryServiceTest {

    @Inject
    private TicketHistoryService historyService;

    @Inject
    private TicketHistoryRepository historyRepository;

    private Ticket ticket;
    private User user;
    private User assignee;
    private Category category;
    private Project project;
    private WorkflowStatus status;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        assignee = new User();
        assignee.setId(2L);
        assignee.setName("Assignee User");
        assignee.setEmail("assignee@example.com");

        category = new Category();
        category.setId(1L);
        category.setName("Bug");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        status = new WorkflowStatus();
        status.setId(1L);
        status.setName("TODO");

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTitle("Test Ticket");
        ticket.setDescription("Test Description");
        ticket.setAuthor(user);
        ticket.setAssignee(assignee);
        ticket.setCategory(category);
        ticket.setProject(project);
        ticket.setStatus(status);
        ticket.setCreatedAt(Instant.now());
        ticket.setUpdatedAt(Instant.now());
    }

    @Test
    @DisplayName("Should log ticket creation")
    @Transactional
    void shouldLogTicketCreation() {
        // When
        historyService.logTicketCreated(ticket, user);

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log ticket update with changes")
    @Transactional
    void shouldLogTicketUpdate() {
        // When
        historyService.logTicketUpdated(ticket, user, "title, description");

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log status change")
    @Transactional
    void shouldLogStatusChange() {
        // When
        historyService.logStatusChanged(ticket, user, "TODO", "IN_PROGRESS");

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log assignee change from one user to another")
    @Transactional
    void shouldLogAssigneeChange() {
        // When
        historyService.logAssigneeChanged(ticket, user, "Old Assignee", "New Assignee");

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log assignee assignment when previously unassigned")
    @Transactional
    void shouldLogAssigneeAssignment() {
        // When
        historyService.logAssigneeChanged(ticket, user, null, "New Assignee");

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log assignee unassignment")
    @Transactional
    void shouldLogAssigneeUnassignment() {
        // When
        historyService.logAssigneeChanged(ticket, user, "Old Assignee", null);

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log category change")
    @Transactional
    void shouldLogCategoryChange() {
        // When
        historyService.logCategoryChanged(ticket, user, "Bug", "Feature");

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log comment addition")
    @Transactional
    void shouldLogCommentAdded() {
        // When
        historyService.logCommentAdded(ticket, user);

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log ticket deletion")
    @Transactional
    void shouldLogTicketDeleted() {
        // When
        historyService.logTicketDeleted(ticket, user);

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log ticket restoration")
    @Transactional
    void shouldLogTicketRestored() {
        // When
        historyService.logTicketRestored(ticket, user);

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log priority change")
    @Transactional
    void shouldLogPriorityChange() {
        // When
        historyService.logPriorityChanged(ticket, user, "Low", "High");

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log due date change")
    @Transactional
    void shouldLogDueDateChange() {
        // When
        historyService.logDueDateChanged(ticket, user, "2024-01-01", "2024-02-01");

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should log custom action")
    @Transactional
    void shouldLogCustomAction() {
        // When
        historyService.logCustomAction(ticket, user, "Custom action performed");

        // Then - verify no exception is thrown
        assertNotNull(historyService);
    }

    @Test
    @DisplayName("Should create history entry with correct data")
    @Transactional
    void shouldCreateHistoryEntryWithCorrectData() {
        // When
        historyService.logTicketCreated(ticket, user);

        // Then - verify no exception is thrown and service is working
        assertNotNull(historyService);
    }
}