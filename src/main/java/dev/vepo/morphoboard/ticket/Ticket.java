package dev.vepo.morphoboard.ticket;

import java.time.Instant;
import java.util.Objects;

import dev.vepo.morphoboard.categories.Category;
import dev.vepo.morphoboard.project.Project;
import dev.vepo.morphoboard.user.User;
import dev.vepo.morphoboard.workflow.WorkflowStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_tickets")
public class Ticket {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private WorkflowStatus status;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "assignee_id", nullable = true)
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;

    public Ticket() {}

    public Ticket(String title, String description, Category category, User author, User assignee, Project project, WorkflowStatus status) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.author = author;
        this.assignee = assignee;
        this.project = project;
        this.status = status;
        this.createdAt = this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (Objects.nonNull(other) && other instanceof Ticket otherTicket) {
            return Objects.equals(id, otherTicket.id);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Ticket [id=" + id + ", title=" + title + ", description=" + description + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
                + ", category=" + category + ", status=" + status + ", author=" + author + ", assignee=" + assignee + ", project=" + project + "]";
    }

}