package dev.vepo.morphoboard.ticket;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TicketHistoryRepository implements PanacheRepository<TicketHistory> {
} 