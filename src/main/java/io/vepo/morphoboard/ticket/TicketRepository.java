package io.vepo.morphoboard.ticket;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.rangeClosed;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TicketRepository implements PanacheRepository<Ticket> {
    private static final Logger logger = LoggerFactory.getLogger(TicketRepository.class);

    public Stream<Ticket> search(String[] terms, long statusId) {
        Objects.requireNonNull(terms, "terms cannot be null!");

        var params = new ArrayList<Object>();
        var statements = new ArrayList<String>();
        if (terms.length > 0) {
            statements.add(rangeClosed(1, terms.length)
                                .mapToObj(index -> format("(LOWER(t.title) LIKE LOWER(?%d) OR LOWER(t.description) LIKE LOWER(?%d))", index, index))
                                .collect(joining(" OR ", " (", ") ")));
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
            return streamAll();
        }

        logger.atInfo()
              .setMessage("Searching statements={}, parameters={}")
              .addArgument(statements)
              .addArgument(() -> params.stream()
                                       .map(Object::toString)
                                       .collect(joining(", ", "[", "]")))
              .log();
        return find(format("FROM Ticket t WHERE %s", statements.stream()
                                                                      .collect(joining(" AND "))),
                    params.toArray()).stream();
    }
}