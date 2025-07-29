package dev.vepo.morphoboard.ticket;

import java.util.Optional;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class CategoryRepository {
    @PersistenceContext
    private EntityManager em;

    public Optional<Category> findById(Long id) {
        return em.createQuery("FROM Category WHERE id = :id", Category.class)
                 .setParameter("id", id)
                 .getResultStream()
                 .findFirst();
    }

    public Stream<Category> findAll() {
        return em.createQuery("FROM Category", Category.class)
                 .getResultStream();
    }

    public void save(Category categoria) {
        em.persist(categoria);
    }
}