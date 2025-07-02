package io.vepo.morphoboard.ticket;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.vepo.morphoboard.user.User;
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
    public Status status;

    @ManyToOne
    public User author;

    @ManyToOne
    public User assignee;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Comment> comments;
} 