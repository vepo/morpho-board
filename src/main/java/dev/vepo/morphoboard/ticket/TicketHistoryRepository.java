package dev.vepo.morphoboard.ticket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class TicketHistoryRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(TicketHistory history) {
        em.persist(history);
    }
}