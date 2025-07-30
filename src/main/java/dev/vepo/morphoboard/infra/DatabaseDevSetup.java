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
import dev.vepo.morphoboard.categories.Category;
import dev.vepo.morphoboard.categories.CategoryRepository;
import dev.vepo.morphoboard.project.Project;
import dev.vepo.morphoboard.project.ProjectRepository;
import dev.vepo.morphoboard.ticket.Ticket;
import dev.vepo.morphoboard.ticket.TicketRepository;
import dev.vepo.morphoboard.user.Role;
import dev.vepo.morphoboard.user.User;
import dev.vepo.morphoboard.user.UserRepository;
import dev.vepo.morphoboard.workflow.Workflow;
import dev.vepo.morphoboard.workflow.WorkflowRepository;
import dev.vepo.morphoboard.workflow.WorkflowStatus;
import dev.vepo.morphoboard.workflow.WorkflowTransition;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@IfBuildProfile(anyOf = { "dev", "test" })
public class DatabaseDevSetup {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseDevSetup.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProjectRepository projectRepository;
    private final WorkflowRepository workflowRepository;
    private final TicketRepository ticketRepository;

    @Inject
    public DatabaseDevSetup(PasswordEncoder passwordEncoder,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            ProjectRepository projectRepository,
                            WorkflowRepository workflowRepository,
                            TicketRepository ticketRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.projectRepository = projectRepository;
        this.workflowRepository = workflowRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        var encodedDefaultPassword = passwordEncoder.hashPassword("qwas1234");
        logger.info("Populating database with initial data for development...");
        userRepository.save(new User("Gestor", "pm@morpho-board.io", encodedDefaultPassword, Set.of(Role.PROJECT_MANAGER)));
        userRepository.save(new User("Desenvolvedor", "dev@morpho-board.io", encodedDefaultPassword, Set.of(Role.USER)));
        userRepository.save(new User("Administrador", "admin@morpho-board.io", encodedDefaultPassword, Set.of(Role.ADMIN)));
        userRepository.save(new User("Super Admin", "sudo@morpho-board.io", encodedDefaultPassword, Set.of(Role.ADMIN, Role.PROJECT_MANAGER)));
        userRepository.save(new User("Usuário", "user@morpho-board.io", encodedDefaultPassword, Set.of(Role.USER)));

        var todo = workflowRepository.save(new WorkflowStatus("TO_DO"));
        var inProgress = workflowRepository.save(new WorkflowStatus("IN_PROGRESS"));
        var blocked = workflowRepository.save(new WorkflowStatus("BLOCKED"));
        var done = workflowRepository.save(new WorkflowStatus("DONE"));

        workflowRepository.save(new Workflow("Kanban",
                                             List.of(todo, inProgress, blocked, done),
                                             todo,
                                             List.of(new WorkflowTransition(todo, inProgress),
                                                     new WorkflowTransition(inProgress, blocked),
                                                     new WorkflowTransition(blocked, inProgress),
                                                     new WorkflowTransition(inProgress, done))));

        // Buscar usuários
        var author = userRepository.findByEmail("user@morpho-board.io").orElseThrow();
        var assignee = userRepository.findByEmail("dev@morpho-board.io").orElseThrow();
        loadCsvs(author);

        projectRepository.save(new Project("Projeto 1",
                                           "Descrição do projeto 1", workflowRepository.findByName("Kanban").orElseThrow()));

        // Categoria para os tickets
        var categoria = new Category("Bug");
        categoryRepository.save(categoria);
        categoryRepository.save(new Category("Melhoria"));
        categoryRepository.save(new Category("Integração"));

        // Buscar projeto
        var projeto = projectRepository.findByName("Projeto 1").orElseThrow();

        // Criar tickets de teste
        ticketRepository.save(new Ticket("Corrigir bug na tela de login",
                                         "Usuários não conseguem acessar com senha especial.",
                                         categoria,
                                         author,
                                         assignee,
                                         projeto,
                                         todo));

        ticketRepository.save(new Ticket("Implementar exportação de relatórios",
                                         "Adicionar opção de exportar relatórios em PDF.",
                                         categoria,
                                         author,
                                         assignee,
                                         projeto,
                                         inProgress));

        ticketRepository.save(new Ticket("Importar dados por CSV",
                                         "A aplicação deve ser capaz de importar dados via CSV.",
                                         categoria,
                                         author,
                                         assignee,
                                         projeto,
                                         inProgress));

        ticketRepository.save(new Ticket("Ajustar layout mobile",
                                         "Elementos sobrepostos em telas pequenas.",
                                         categoria,
                                         author,
                                         assignee,
                                         projeto,
                                         blocked));

        ticketRepository.save(new Ticket("Atualizar documentação",
                                         "Documentação desatualizada após últimas mudanças.",
                                         categoria,
                                         author,
                                         assignee,
                                         projeto,
                                         done));

        logger.info("Database populated with initial data for development.");
    }

    private void loadCsvs(User author) {
        var categories = loadCategories();

        var workflows = new HashMap<String, Workflow>();
        var allStatus = new HashMap<String, WorkflowStatus>();
        loadWorkflows(workflows, allStatus);

        var projetos = loadProjects();

        loadTickets(author, categories, allStatus, projetos);
    }

    private void loadTickets(User author, HashMap<String, Category> categories, HashMap<String, WorkflowStatus> allStatus, HashMap<String, Project> projetos) {
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
                    ticketRepository.save(ticket);
                }
            }
        } catch (IOException | CsvValidationException ioe) {
            throw new IllegalStateException("Cannot reader categories.csv", ioe);
        }
    }

    private HashMap<String, Project> loadProjects() {
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
                    var project = new Project(line[1], line[2], workflowRepository.findByName(workflowName)
                                                                                  .orElseThrow(() -> new IllegalStateException("Workflow not found! "
                                                                                          + workflowName)));
                    projectRepository.save(project);
                    projetos.put(line[0], project);
                }
            }
        } catch (IOException | CsvValidationException ioe) {
            throw new IllegalStateException("Cannot reader categories.csv", ioe);
        }
        return projetos;
    }

    private void loadWorkflows(HashMap<String, Workflow> workflows, HashMap<String, WorkflowStatus> allStatus) {
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
                var statuses = Stream.concat(data.stream()
                                                 .map(line -> line[1]),
                                             data.stream()
                                                 .map(line -> line[2]))
                                     .distinct()
                                     .map(status -> workflowRepository.findStatusByName(status)
                                                                      .orElseGet(() -> {
                                                                          WorkflowStatus dbStatus = new WorkflowStatus(status);
                                                                          workflowRepository.save(dbStatus);
                                                                          allStatus.put(status, dbStatus);
                                                                          return dbStatus;
                                                                      }))
                                     .toList();
                var start = statuses.stream()
                                    .filter(s -> s.getName().equals(workflowStart.get(workflowName)))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException("Status inicial não encontrado"));

                var transitions = data.stream()
                                      .map(line -> new WorkflowTransition(workflowRepository.findStatusByName(line[1]).orElseThrow(),
                                                                          workflowRepository.findStatusByName(line[2]).orElseThrow()))
                                      .toList();
                var workflow = new Workflow(workflowName, statuses, start, transitions);
                workflowRepository.save(workflow);
                workflows.put(workflowName, workflow);
            });
        } catch (IOException | CsvValidationException ioe) {
            throw new IllegalStateException("Cannot reader categories.csv", ioe);
        }
    }

    private HashMap<String, Category> loadCategories() {
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
                    categoryRepository.save(category);
                    categories.put(line[0], category);
                }
            }
        } catch (IOException | CsvValidationException ioe) {
            throw new IllegalStateException("Cannot reader categories.csv", ioe);
        }
        return categories;
    }
}
