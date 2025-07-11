package io.vepo.morphoboard.infra;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.StartupEvent;
import io.vepo.morphoboard.project.Project;
import io.vepo.morphoboard.ticket.Ticket;
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

        Project.persist(new Project("Projeto 2", "Descrição do projeto 2", Workflow.findByName("Kanban")
                                                                                   .orElseThrow()));

        // Categoria para os tickets
        var categoria = new io.vepo.morphoboard.ticket.Category();
        categoria.name = "Bug";
        categoria.persist();

        // Buscar usuários
        var author = User.<User>find("email", "user@demo.com").firstResult();
        var assignee = User.<User>find("email", "dev@demo.com").firstResult();

        // Buscar projeto
        var projeto = Project.<Project>find("name", "Projeto 1").firstResult();

        // Buscar estágios do workflow
        var todo = WorkflowStage.findByName("TODO").orElseThrow();
        var inProgress = WorkflowStage.findByName("IN_PROGRESS").orElseThrow();
        var blocked = WorkflowStage.findByName("BLOCKED").orElseThrow();
        var done = WorkflowStage.findByName("DONE").orElseThrow();

        // Criar tickets de teste
        Ticket.persist(new Ticket("Corrigir bug na tela de login",
                                  "Usuários não conseguem acessar com senha especial.",
                                  categoria,
                                  author,
                                  assignee,
                                  projeto,
                                  todo));

        Ticket.persist(new Ticket("Implementar exportação de relatórios",
                                  "Adicionar opção de exportar relatórios em PDF.",
                                  categoria,
                                  author,
                                  assignee,
                                  projeto,
                                  inProgress));

        Ticket.persist(new Ticket("Importar dados por CSV",
                                  "A aplicação deve ser capaz de importar dados via CSV.",
                                  categoria,
                                  author,
                                  assignee,
                                  projeto,
                                  inProgress));

        Ticket.persist(new Ticket("Ajustar layout mobile",
                                  "Elementos sobrepostos em telas pequenas.",
                                  categoria,
                                  author,
                                  assignee,
                                  projeto,
                                  blocked));

        Ticket.persist(new Ticket("Atualizar documentação",
                                  "Documentação desatualizada após últimas mudanças.",
                                  categoria,
                                  author,
                                  assignee,
                                  projeto,
                                  done));

        logger.info("Database populated with initial data for development.");
    }
}
