package dev.vepo.morphoboard.ticket;

import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import dev.vepo.morphoboard.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_comments")
public class Comment extends PanacheEntity {
    @Column(columnDefinition = "TEXT")
    public String content;
    public Instant createdAt;

    @ManyToOne
    public Ticket ticket;

    @ManyToOne
    public User author;
}