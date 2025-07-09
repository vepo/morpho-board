package io.vepo.morphoboard.workflow;

import java.util.Optional;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tb_workflow_stages", uniqueConstraints = @UniqueConstraint(name = "tb_workflow_stage_UK", columnNames = "name"))
public class WorkflowStage extends PanacheEntity {
    @Column(unique = true, nullable = false)
    public String name;

    public WorkflowStage() {
    }

    public WorkflowStage(String name) {
        this.name = name;
    }

    public static Optional<WorkflowStage> findByName(String string) {
        return find("name", string).firstResultOptional();
    }
}