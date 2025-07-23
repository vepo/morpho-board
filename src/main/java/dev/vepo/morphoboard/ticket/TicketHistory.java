package dev.vepo.morphoboard.ticket;

import java.time.Instant;

import dev.vepo.morphoboard.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TicketHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    public Ticket ticket;

    @ManyToOne(optional = false)
    public User user;

    @Column(nullable = false)
    public String description;

    @Column(nullable = false)
    public Instant timestamp;

    public TicketHistory() {}

    public TicketHistory(Ticket ticket, User user, String description, Instant timestamp) {
        this.ticket = ticket;
        this.user = user;
        this.description = description;
        this.timestamp = timestamp;
    }
} 