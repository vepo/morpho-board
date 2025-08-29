package dev.vepo.morphoboard.ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.vepo.morphoboard.ticket.comments.Comment;
import dev.vepo.morphoboard.ticket.history.TicketHistory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;

@ApplicationScoped
public class TicketRepository {
    private static final Logger logger = LoggerFactory.getLogger(TicketRepository.class);
    @PersistenceContext
    private EntityManager em;

    public Stream<Ticket> search(String[] terms, long statusId) {
        Objects.requireNonNull(terms, "terms cannot be null!");

        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Ticket.class);
        var ticket = cq.from(Ticket.class);

        var predicates = new ArrayList<Predicate>();

        predicates.add(cb.isFalse(ticket.get("deleted")));

        if (terms.length > 0) {
            var termPredicates = new ArrayList<Predicate>();
            for (String term : terms) {
                var pattern = "%%%s%%".formatted(term).toLowerCase();
                termPredicates.add(cb.or(cb.like(cb.lower(ticket.get("title")), pattern),
                                         cb.like(cb.lower(ticket.get("description")), pattern)));
            }
            predicates.add(cb.and(termPredicates.toArray(Predicate[]::new)));
        }

        if (statusId != -1) {
            predicates.add(cb.equal(ticket.get("status")
                                          .get("id"),
                                    statusId));
        }

        cq.where(cb.and(predicates.toArray(Predicate[]::new)));

        logger.atInfo()
              .setMessage("Searching with criteria query")
              // This is a simplified logging of the query - in reality CriteriaQuery doesn't
              // expose its structure easily
              .addArgument(() -> "Terms: " + Arrays.toString(terms) + ", statusId: " + (statusId != -1 ? statusId : "any"))
              .log();

        return em.createQuery(cq)
                 .getResultStream();
    }

    public Stream<Ticket> findByStatusId(long statusId) {
        return em.createQuery("FROM Ticket WHERE deleted = false AND status.id = :id", Ticket.class)
                 .setParameter("id", statusId)
                 .getResultStream();
    }

    public Optional<Ticket> findById(long id) {
        return em.createQuery("FROM Ticket WHERE deleted = false AND id = :id", Ticket.class)
                 .setParameter("id", id)
                 .getResultStream()
                 .findFirst();
    }

    public Optional<Ticket> findByIdentifier(String id) {
        return em.createQuery("FROM Ticket WHERE deleted = false AND identifier = :identifier", Ticket.class)
                 .setParameter("identifier", id)
                 .getResultStream()
                 .findFirst();
    }

    public Stream<Ticket> findByStatusName(String status) {
        return em.createQuery("FROM Ticket WHERE deleted = false AND status.name = :name", Ticket.class)
                 .setParameter("name", status)
                 .getResultStream();
    }

    public Stream<Ticket> findAll() {
        return em.createQuery("FROM Ticket WHERE deleted = false", Ticket.class)
                 .getResultStream();
    }

    public Ticket save(Ticket ticket) {
        em.persist(ticket);
        return ticket;
    }

    public void delete(long id) {
        int deletedItems = em.createQuery("UPDATE Ticket SET deleted = true WHERE id = :id")
                             .setParameter("id", id)
                             .executeUpdate();
        logger.warn("Deleted tickets! count={}", deletedItems);
    }

    public Stream<Ticket> findByProjectId(long id) {
        return em.createQuery("FROM Ticket WHERE deleted = false AND project.id = :id", Ticket.class)
                 .setParameter("id", id)
                 .getResultStream();
    }

    public Stream<TicketHistory> findHistoryByTicketId(Long id) {
        return em.createQuery("FROM TicketHistory where ticket.id = :id", TicketHistory.class)
                 .setParameter("id", id)
                 .getResultStream();
    }

    public Stream<Comment> findCommentsByTicketId(Long id) {
        return em.createQuery("FROM Comment where ticket.id = :id", Comment.class)
                 .setParameter("id", id)
                 .getResultStream();
    }

    public int countProjectTickets(long projectId) {
        return (int) em.createQuery("SELECT id FROM Ticket WHERE project.id = :id", Long.class)
                       .setParameter("id", projectId)
                       .getResultStream()
                       .count();
    }

    public Comment saveComment(Comment comment) {
        em.persist(comment);
        return comment;
    }
}