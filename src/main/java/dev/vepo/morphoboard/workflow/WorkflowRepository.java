package dev.vepo.morphoboard.workflow;

import java.util.Optional;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class WorkflowRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Workflow> findById(long id) {
        return em.createQuery("FROM Workflow WHERE id = :id", Workflow.class)
                 .setParameter("id", id)
                 .getResultStream()
                 .findFirst();
    }

    public Stream<Workflow> findAll() {
        return em.createQuery("FROM Workflow", Workflow.class)
                 .getResultStream();
    }

    public Stream<WorkflowStatus> findAllStatus() {
        return em.createQuery("FROM WorkflowStatus", WorkflowStatus.class)
                 .getResultStream();
    }

    public Optional<WorkflowStatus> findStatusByName(String name) {
        return em.createQuery("FROM WorkflowStatus WHERE name = :name", WorkflowStatus.class)
                 .setParameter("name", name)
                 .getResultStream()
                 .findFirst();
    }

    public Optional<Workflow> findByName(String name) {
        return em.createQuery("FROM Workflow WHERE name = :name", Workflow.class)
                 .setParameter("name", name)
                 .getResultStream()
                 .findFirst();
    }

    public WorkflowStatus save(WorkflowStatus status) {
        em.persist(status);
        return status;
    }

    public Workflow save(Workflow workflow) {
        em.persist(workflow);
        return workflow;
    }
}
