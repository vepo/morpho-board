package dev.vepo.morphoboard.workflow;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_workflow_transitions", uniqueConstraints = @UniqueConstraint(name = "tb_workflow_transition_UK", columnNames = {
    "workflow_id",
    "from_id",
    "to_id" }))
public class WorkflowTransition extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "from_id", nullable = false)
    public WorkflowStatus from;

    @ManyToOne
    @JoinColumn(name = "to_id", nullable = false)
    public WorkflowStatus to;

    public WorkflowTransition() {
    }

    public WorkflowTransition(WorkflowStatus from, WorkflowStatus to) {
        this.from = from;
        this.to = to;
    }
}