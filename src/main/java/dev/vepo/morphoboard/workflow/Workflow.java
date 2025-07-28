package dev.vepo.morphoboard.workflow;

import java.util.List;
import java.util.Optional;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_workflows", uniqueConstraints = @jakarta.persistence.UniqueConstraint(name = "tb_workflow_UK", columnNames = "name"))
public class Workflow extends PanacheEntity {
    public String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "start_id")
    public WorkflowStatus start;

    @ManyToMany(fetch = FetchType.EAGER)
    public List<WorkflowStatus> statuses;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "workflow_id")
    public List<WorkflowTransition> transitions;

    public Workflow() {}

    public Workflow(String name, List<WorkflowStatus> statuses, WorkflowStatus start, List<WorkflowTransition> transitions) {
        this.name = name;
        this.statuses = statuses;
        this.start = start;
        this.transitions = transitions;
    }

    public static Optional<Workflow> findByName(String string) {
        return find("name", string).firstResultOptional();
    }
}