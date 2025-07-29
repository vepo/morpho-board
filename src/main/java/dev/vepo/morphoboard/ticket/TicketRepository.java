package dev.vepo.morphoboard.ticket;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.rangeClosed;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class TicketRepository {
    private static final Logger logger = LoggerFactory.getLogger(TicketRepository.class);
    @PersistenceContext
    private EntityManager em;

    public Stream<Ticket> search(String[] terms, long statusId) {
        Objects.requireNonNull(terms, "terms cannot be null!");

        var params = new ArrayList<Object>();
        var statements = new ArrayList<String>();
        if (terms.length > 0) {
            statements.add(rangeClosed(1, terms.length)
                                                       .mapToObj(index -> format("(LOWER(t.title) LIKE LOWER(?%d) OR LOWER(t.description) LIKE LOWER(?%d))",
                                                                                 index, index))
                                                       .collect(joining(" AND ", " (", ") ")));
            params.addAll(Stream.of(terms)
                                .map(v -> format("%%%s%%", v))
                                .toList());
        }

        if (statusId != -1) {
            params.add(statusId);
            statements.add(format("t.status.id = ?%d", params.size()));
        }

        if (statements.isEmpty()) {
            logger.info("Returning all tickets because no filter is defined!");
            return em.createQuery("FROM Ticket", Ticket.class)
                     .getResultStream();
        }

        logger.atInfo()
              .setMessage("Searching statements={}, parameters={}")
              .addArgument(statements)
              .addArgument(() -> params.stream()
                                       .map(Object::toString)
                                       .collect(joining(", ", "[", "]")))
              .log();

        var query = em.createQuery(format("FROM Ticket t WHERE %s", statements.stream()
                                                                              .collect(joining(" AND "))),
                                   Ticket.class);
        IntStream.rangeClosed(1, params.size())
                 .forEach(index -> query.setParameter(index, params.get(index - 1)));
        return query.getResultStream();

    }

    public Stream<Ticket> findByStatusId(long statusId) {
        return em.createQuery("FROM Ticket where status.id = :id", Ticket.class)
                 .setParameter("id", statusId)
                 .getResultStream();
    }

    public Optional<Ticket> findById(long id) {
        return em.createQuery("FROM Ticket where id = :id", Ticket.class)
                 .setParameter("id", id)
                 .getResultStream()
                 .findFirst();
    }

    public Stream<Ticket> findByStatusName(String status) {
        return em.createQuery("FROM Ticket where status.name = :name", Ticket.class)
                 .setParameter("name", status)
                 .getResultStream();
    }

    public Stream<Ticket> findAll() {
        return em.createQuery("FROM Ticket", Ticket.class)
                 .getResultStream();
    }

    public void save(Ticket ticket) {
        em.persist(ticket);
    }

    public void delete(long id) {
        int deletedItems = em.createQuery("DELETE FROM Ticket WHERE id = :id")
                             .setParameter("id", id)
                             .executeUpdate();
        logger.warn("Deleted tickets! count={}", deletedItems);
    }

    public Stream<Ticket> findByProjectId(long id) {
        return em.createQuery("FROM Ticket where project.id = :id", Ticket.class)
                 .setParameter("id", id)
                 .getResultStream();
    }
}