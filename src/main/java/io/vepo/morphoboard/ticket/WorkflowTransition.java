package io.vepo.morphoboard.ticket;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_workflow_transitions")
public class WorkflowTransition extends PanacheEntity {
    @ManyToOne
    public Workflow workflow;
    @ManyToOne
    public WorkflowStage fromStage;
    @ManyToOne
    public WorkflowStage toStage;
} 