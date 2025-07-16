package io.vepo.morphoboard.project;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.vepo.morphoboard.workflow.Workflow;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_projects", uniqueConstraints = @jakarta.persistence.UniqueConstraint(name = "tb_project_UK", columnNames = "name"))
public class Project extends PanacheEntity {
    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Column(columnDefinition = "text")
    public String description;
    
    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    public Workflow workflow;

    public Project() {
    }

    public Project(String name, String description, Workflow workflow) {
        this.name = name;
        this.description = description;
        this.workflow = workflow;
    }
}