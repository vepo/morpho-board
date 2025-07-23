package dev.vepo.morphoboard.project;

public record ProjectResponse(long id, String name, String description, ProjectWorkflowResponse workflow) {

    public static ProjectResponse load(Project project) {
        return new ProjectResponse(project.id,
                                   project.name,
                                   project.description,
                                   new ProjectWorkflowResponse(project.workflow.id,
                                                               project.workflow.name));
    }
}