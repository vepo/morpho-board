package io.vepo.morphoboard.ticket;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "tb_workflows")
public class Workflow extends PanacheEntity {
    public String name;
    @ManyToOne(optional = false)
    public Project project;
    public boolean defaultWorkflow;
} 