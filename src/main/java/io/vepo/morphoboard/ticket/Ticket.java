package io.vepo.morphoboard.ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.vepo.morphoboard.project.Project;
import io.vepo.morphoboard.user.User;
import io.vepo.morphoboard.workflow.WorkflowStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_tickets")
public class Ticket extends PanacheEntity {
    public String title;
    @Column(columnDefinition = "TEXT")
    public String description;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    @ManyToOne
    public Category category;

    @ManyToOne
    public WorkflowStatus status;

    @ManyToOne
    public User author;

    @ManyToOne
    public User assignee;

    @ManyToOne(optional = false)
    public Project project;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Comment> comments;

    public Ticket() {
    }

    public Ticket(String title, String description, Category category, User author, User assignee, Project project, WorkflowStatus status) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.author = author;
        this.assignee = assignee;
        this.project = project;
        this.status = status;
    }

    public static Stream<Ticket> findByProject(long projectId) {
        return find("project.id", projectId).stream();
    }
}