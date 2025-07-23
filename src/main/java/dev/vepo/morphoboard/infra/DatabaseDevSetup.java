package dev.vepo.morphoboard.infra;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.StartupEvent;
import dev.vepo.morphoboard.auth.PasswordEncoder;
import dev.vepo.morphoboard.project.Project;
import dev.vepo.morphoboard.ticket.Category;
import dev.vepo.morphoboard.ticket.Ticket;
import dev.vepo.morphoboard.user.Role;
import dev.vepo.morphoboard.user.User;
import dev.vepo.morphoboard.workflow.Workflow;
import dev.vepo.morphoboard.workflow.WorkflowStatus;
import dev.vepo.morphoboard.workflow.WorkflowTransition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@IfBuildProfile("dev")
public class DatabaseDevSetup {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseDevSetup.class);

    private PasswordEncoder passwordEncoder;

    @Inject
    public DatabaseDevSetup(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        var encodedDefaultPassword = passwordEncoder.hashPassword("qwas1234");
        logger.info("Populating database with initial data for development...");
        User.persist(new User("Gestor", "pm@morpho-board.io", encodedDefaultPassword, Set.of(Role.PROJECT_MANAGER)));
        User.persist(new User("Desenvolvedor", "dev@morpho-board.io", encodedDefaultPassword, Set.of(Role.USER)));
        User.persist(new User("Administrador", "admin@morpho-board.io", encodedDefaultPassword, Set.of(Role.ADMIN)));
        User.persist(new User("Super Admin", "sudo@morpho-board.io", encodedDefaultPassword, Set.of(Role.ADMIN, Role.PROJECT_MANAGER)));
        User.persist(new User("Usuário", "user@morpho-board.io", encodedDefaultPassword, Set.of(Role.USER)));

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

        
        // Buscar usuários
        var author = User.<User>find("email", "user@morpho-board.io").firstResult();
        var assignee = User.<User>find("email", "dev@morpho-board.io").firstResult();
        loadCsvs(author);

        Project.persist(new Project("Projeto 1",
                "Descrição do projeto 1", Workflow.findByName("Kanban").orElseThrow()));

        // Categoria para os tickets
        var categoria = new Category("Bug");
        categoria.persist();
        new Category("Melhoria").persist();
        new Category("Integração").persist();

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
    private void loadCsvs(User author) {
        var categories = new HashMap<String, Category>();
        try (Reader reader = new InputStreamReader(
                DatabaseDevSetup.class.getResourceAsStream("/dev/data/categorias.csv"))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;
                boolean header = false;
                while ((line = csvReader.readNext()) != null) {
                    if (!header) {
                        header = true;
                        continue;
                    }
                    var category = new Category(line[1], line[2]);
                    category.persist();
                    categories.put(line[0], category);
                }
            }
        } catch (IOException | CsvValidationException ioe) {
            throw new IllegalStateException("Cannot reader categories.csv", ioe);
        }

        var workflows = new HashMap<String, Workflow>();
        var allStatus = new HashMap<String, WorkflowStatus>();
        try (Reader reader = new InputStreamReader(DatabaseDevSetup.class.getResourceAsStream("/dev/data/workflows.csv"))) {
            Map<String, List<String[]>> workflowData = new HashMap<>();
            Map<String, String> workflowStart = new HashMap<>();
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;
                boolean header = false;
                while ((line = csvReader.readNext()) != null) {
                    if (!header) {
                        header = true;
                        continue;
                    }
                    workflowData.computeIfAbsent(line[0], __ -> new ArrayList<>()).add(line);
                    workflowStart.putIfAbsent(line[0], line[1]);
                }
            }
            workflowData.forEach((workflowName, data) -> {
                List<WorkflowStatus> statuses = Stream.concat(data.stream()
                                                                  .map(line -> line[1]),
                                                              data.stream()
                                                                  .map(line -> line[2]))
                                                      .distinct()
                                                      .map(status -> WorkflowStatus.findByName(status)
                                                                                   .orElseGet(() -> {
                                                                                        WorkflowStatus dbStatus = new WorkflowStatus(status);
                                                                                        dbStatus.persist();
                                                                                        allStatus.put(status, dbStatus);
                                                                                        return dbStatus;
                                                                                    })).toList();
                WorkflowStatus start = statuses.stream()
                                               .filter(s -> s.name.equals(workflowStart.get(workflowName)))
                                               .findFirst()
                                               .orElseThrow(() -> new IllegalStateException("Status inicial não encontrado"));                                               ;
                List<WorkflowTransition> transitions = data.stream()
                                                           .map(line -> new WorkflowTransition(WorkflowStatus.findByName(line[1]).orElseThrow(),
                                                                                               WorkflowStatus.findByName(line[2]).orElseThrow()))
                                                           .toList();
                Workflow workflow = new Workflow(workflowName, statuses, start, transitions);
                workflow.persist();
                workflows.put(workflowName, workflow);
            });
        } catch(IOException | CsvValidationException ioe) {
            throw new IllegalStateException("Cannot reader categories.csv", ioe);
        }


        var projetos = new HashMap<String, Project>();
        try (Reader reader = new InputStreamReader(DatabaseDevSetup.class.getResourceAsStream("/dev/data/projetos.csv"))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;
                boolean header = false;
                while ((line = csvReader.readNext()) != null) {
                    if (!header) {
                        header = true;
                        continue;
                    }
                    var workflowName = line[3];
                    var project = new Project(line[1], line[2], Workflow.findByName(workflowName)
                            .orElseThrow(() -> new IllegalStateException("Workflow not found! " + workflowName)));
                   project .persist();
                   projetos.put(line[0], project);
                }
            }
        } catch(IOException | CsvValidationException ioe) {
            throw new IllegalStateException("Cannot reader categories.csv", ioe);
        }

        try (Reader reader = new InputStreamReader(DatabaseDevSetup.class.getResourceAsStream("/dev/data/tickets.csv"))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;
                boolean header = false;
                while ((line = csvReader.readNext()) != null) {
                    if (!header) {
                        header = true;
                        continue;
                    }
                    var ticket = new Ticket(line[0], line[1], categories.get(line[4]), author, null, projetos.get(line[2]), allStatus.get(line[3]));
                    ticket.persist();
                }
            }
        } catch(IOException | CsvValidationException ioe) {
            throw new IllegalStateException("Cannot reader categories.csv", ioe);
        }
    }
}
