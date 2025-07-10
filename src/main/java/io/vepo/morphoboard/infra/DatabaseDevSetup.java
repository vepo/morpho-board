package io.vepo.morphoboard.infra;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.StartupEvent;
import io.vepo.morphoboard.project.Project;
import io.vepo.morphoboard.user.User;
import io.vepo.morphoboard.workflow.Workflow;
import io.vepo.morphoboard.workflow.WorkflowStage;
import io.vepo.morphoboard.workflow.WorkflowTransition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@ApplicationScoped
@IfBuildProfile("dev")
public class DatabaseDevSetup {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseDevSetup.class);

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        logger.info("Populating database with initial data for development...");
        User.persist(new User("Gestor", "gestor@demo.com", "123", "MANAGER"));
        User.persist(new User("Desenvolvedor", "dev@demo.com", "123", "DEV"));
        User.persist(new User("Administrador", "admin@demo.com", "123", "ADMIN"));
        User.persist(new User("Super Admin", "superadmin@demo.com", "123", "SUPER_ADMIN"));
        User.persist(new User("Usuário", "user@demo.com", "123", "USER"));

        WorkflowStage.persist(new WorkflowStage("TODO"));
        WorkflowStage.persist(new WorkflowStage("IN_PROGRESS"));
        WorkflowStage.persist(new WorkflowStage("BLOCKED"));
        WorkflowStage.persist(new WorkflowStage("DONE"));

        Workflow.persist(new Workflow("Kanban",
                                      List.of(WorkflowStage.findByName("TODO").orElseThrow(),
                                              WorkflowStage.findByName("IN_PROGRESS").orElseThrow(),
                                              WorkflowStage.findByName("BLOCKED").orElseThrow(),
                                              WorkflowStage.findByName("DONE").orElseThrow()),
                                      WorkflowStage.findByName("TODO").orElseThrow(),
                                      List.of(new WorkflowTransition(WorkflowStage.findByName("TODO").orElseThrow(),
                                                                     WorkflowStage.findByName("IN_PROGRESS").orElseThrow()),
                                              new WorkflowTransition(WorkflowStage.findByName("IN_PROGRESS").orElseThrow(),
                                                                     WorkflowStage.findByName("BLOCKED").orElseThrow()),
                                              new WorkflowTransition(WorkflowStage.findByName("BLOCKED").orElseThrow(),
                                                                     WorkflowStage.findByName("IN_PROGRESS").orElseThrow()),
                                              new WorkflowTransition(WorkflowStage.findByName("IN_PROGRESS").orElseThrow(),
                                                                     WorkflowStage.findByName("DONE").orElseThrow()))));

        Project.persist(new Project("Projeto 1", "Descrição do projeto 1", Workflow.findByName("Kanban")
                                                                                   .orElseThrow()));

        // Categoria para os tickets
        var categoria = new io.vepo.morphoboard.ticket.Category();
        categoria.name = "Bug";
        categoria.persist();

        // Buscar usuários
        var author = (io.vepo.morphoboard.user.User) io.vepo.morphoboard.user.User.find("email", "user@demo.com").firstResult();
        var assignee = (io.vepo.morphoboard.user.User) io.vepo.morphoboard.user.User.find("email", "dev@demo.com").firstResult();

        // Buscar projeto
        var projeto = (io.vepo.morphoboard.project.Project) io.vepo.morphoboard.project.Project.find("name", "Projeto 1").firstResult();

        // Buscar estágios do workflow
        var todo = io.vepo.morphoboard.workflow.WorkflowStage.findByName("TODO").orElseThrow();
        var inProgress = io.vepo.morphoboard.workflow.WorkflowStage.findByName("IN_PROGRESS").orElseThrow();
        var blocked = io.vepo.morphoboard.workflow.WorkflowStage.findByName("BLOCKED").orElseThrow();
        var done = io.vepo.morphoboard.workflow.WorkflowStage.findByName("DONE").orElseThrow();

        // Criar tickets de teste
        var ticket1 = new io.vepo.morphoboard.ticket.Ticket();
        ticket1.title = "Corrigir bug na tela de login";
        ticket1.description = "Usuários não conseguem acessar com senha especial.";
        ticket1.category = categoria;
        ticket1.author = author;
        ticket1.assignee = assignee;
        ticket1.project = projeto;
        ticket1.workflowStage = todo;
        ticket1.persist();

        var ticket2 = new io.vepo.morphoboard.ticket.Ticket();
        ticket2.title = "Implementar exportação de relatórios";
        ticket2.description = "Adicionar opção de exportar relatórios em PDF.";
        ticket2.category = categoria;
        ticket2.author = author;
        ticket2.assignee = assignee;
        ticket2.project = projeto;
        ticket2.workflowStage = inProgress;
        ticket2.persist();

        var ticket3 = new io.vepo.morphoboard.ticket.Ticket();
        ticket3.title = "Ajustar layout mobile";
        ticket3.description = "Elementos sobrepostos em telas pequenas.";
        ticket3.category = categoria;
        ticket3.author = author;
        ticket3.assignee = assignee;
        ticket3.project = projeto;
        ticket3.workflowStage = blocked;
        ticket3.persist();

        var ticket4 = new io.vepo.morphoboard.ticket.Ticket();
        ticket4.title = "Atualizar documentação";
        ticket4.description = "Documentação desatualizada após últimas mudanças.";
        ticket4.category = categoria;
        ticket4.author = author;
        ticket4.assignee = assignee;
        ticket4.project = projeto;
        ticket4.workflowStage = done;
        ticket4.persist();

        logger.info("Database populated with initial data for development.");
    }
}
