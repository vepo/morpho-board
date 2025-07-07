package io.vepo.morphoboard.ticket;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_workflow_stages")
public class WorkflowStage extends PanacheEntity {
    public String name;
    public int position;
    @ManyToOne
    public Workflow workflow;
    @ManyToOne(optional = false)
    public Project project;
} 