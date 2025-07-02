package io.vepo.morphoboard.ticket;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StatusRepository implements PanacheRepository<Status> {
} 