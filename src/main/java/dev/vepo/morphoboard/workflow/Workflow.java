package dev.vepo.morphoboard.workflow;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_workflows", uniqueConstraints = @jakarta.persistence.UniqueConstraint(name = "tb_workflows_UK", columnNames = "name"))
public class Workflow {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "start_id", referencedColumnName = "id", nullable = false)
    private WorkflowStatus start;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_workflow_statuses", joinColumns = @JoinColumn(name = "workflow_id"), inverseJoinColumns = @JoinColumn(name = "status_id"))
    private List<WorkflowStatus> statuses;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "workflow_id")
    private List<WorkflowTransition> transitions;

    public Workflow() {}

    public Workflow(String name, List<WorkflowStatus> statuses, WorkflowStatus start, List<WorkflowTransition> transitions) {
        this.name = name;
        this.statuses = statuses;
        this.start = start;
        this.transitions = transitions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorkflowStatus getStart() {
        return start;
    }

    public void setStart(WorkflowStatus start) {
        this.start = start;
    }

    public List<WorkflowStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<WorkflowStatus> statuses) {
        this.statuses = statuses;
    }

    public List<WorkflowTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<WorkflowTransition> transitions) {
        this.transitions = transitions;
    }

}