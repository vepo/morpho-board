package dev.vepo.morphoboard.user;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    public Optional<User> findByEmail(String email) {
        return em.createQuery("FROM User WHERE email = :email", User.class)
                 .setParameter("email", email)
                 .getResultStream()
                 .findFirst();
    }

    public Optional<User> findById(Long id) {
        return em.createQuery("FROM User WHERE id = :id", User.class)
                 .setParameter("id", id)
                 .getResultStream()
                 .findFirst();
    }

    public void save(User user) {
        em.persist(user);
    }
}
