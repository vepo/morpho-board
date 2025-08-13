package dev.vepo.morphoboard.project;

public record ProjectResponse(long id, String name, String prefix, String description, ProjectWorkflowResponse workflow) {

    public static ProjectResponse load(Project project) {
        return new ProjectResponse(project.getId(),
                                   project.getName(),
                                   project.getPrefix(),
                                   project.getDescription(),
                                   new ProjectWorkflowResponse(project.getWorkflow().getId(),
                                                               project.getWorkflow().getName()));
    }
}