package io.vepo.morphoboard.infra;

import java.util.List;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.StartupEvent;
import io.vepo.morphoboard.project.Project;
import io.vepo.morphoboard.ticket.Category;
import io.vepo.morphoboard.ticket.Ticket;
import io.vepo.morphoboard.user.User;
import io.vepo.morphoboard.workflow.Workflow;
import io.vepo.morphoboard.workflow.WorkflowStatus;
import io.vepo.morphoboard.workflow.WorkflowTransition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@ApplicationScoped
@IfBuildProfile("dev")
public class DatabaseDevSetup {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseDevSetup.class);

    private String loremIpsum() {
        return "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
    }

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        logger.info("Populating database with initial data for development...");
        User.persist(new User("Gestor", "gestor@demo.com", "123", "MANAGER"));
        User.persist(new User("Desenvolvedor", "dev@demo.com", "123", "DEV"));
        User.persist(new User("Administrador", "admin@demo.com", "123", "ADMIN"));
        User.persist(new User("Super Admin", "superadmin@demo.com", "123", "SUPER_ADMIN"));
        User.persist(new User("Usuário", "user@demo.com", "123", "USER"));

        WorkflowStatus.persist(new WorkflowStatus("TO_DO"));
        WorkflowStatus.persist(new WorkflowStatus("IN_PROGRESS"));
        WorkflowStatus.persist(new WorkflowStatus("BLOCKED"));
        WorkflowStatus.persist(new WorkflowStatus("DONE"));

        Workflow.persist(new Workflow("Kanban",
                                      List.of(WorkflowStatus.findByName("TO_DO").orElseThrow(),
                                              WorkflowStatus.findByName("IN_PROGRESS").orElseThrow(),
                                              WorkflowStatus.findByName("BLOCKED").orElseThrow(),
                                              WorkflowStatus.findByName("DONE").orElseThrow()),
                                      WorkflowStatus.findByName("TO_DO").orElseThrow(),
                                      List.of(new WorkflowTransition(WorkflowStatus.findByName("TO_DO").orElseThrow(),
                                                                     WorkflowStatus.findByName("IN_PROGRESS").orElseThrow()),
                                              new WorkflowTransition(WorkflowStatus.findByName("IN_PROGRESS").orElseThrow(),
                                                                     WorkflowStatus.findByName("BLOCKED").orElseThrow()),
                                              new WorkflowTransition(WorkflowStatus.findByName("BLOCKED").orElseThrow(),
                                                                     WorkflowStatus.findByName("IN_PROGRESS").orElseThrow()),
                                              new WorkflowTransition(WorkflowStatus.findByName("IN_PROGRESS").orElseThrow(),
                                                                     WorkflowStatus.findByName("DONE").orElseThrow()))));

        IntStream.range(1, 31)
                 .forEach(index -> Project.persist(new Project("Projeto " + index,
                                                               "Descrição do projeto " + index + " " + loremIpsum(), Workflow.findByName("Kanban").orElseThrow())));

        // Categoria para os tickets
        var categoria = new Category("Bug");
        categoria.persist();
        new Category("Melhoria").persist();
        new Category("Integração").persist();

        // Buscar usuários
        var author = User.<User>find("email", "user@demo.com").firstResult();
        var assignee = User.<User>find("email", "dev@demo.com").firstResult();

        // Buscar projeto
        var projeto = Project.<Project>find("name", "Projeto 1").firstResult();

        // Buscar estágios do workflow
        var todo = WorkflowStatus.findByName("TO_DO").orElseThrow();
        var inProgress = WorkflowStatus.findByName("IN_PROGRESS").orElseThrow();
        var blocked = WorkflowStatus.findByName("BLOCKED").orElseThrow();
        var done = WorkflowStatus.findByName("DONE").orElseThrow();

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
