DO $$
DECLARE
    todo_id        INTEGER;
    progress_id    INTEGER;
    blocked_id     INTEGER;
    done_id        INTEGER;
    agile_id       INTEGER;
    feature_id     INTEGER;
    user_cto_id    INTEGER;
    proj_morpho_id INTEGER;
    open_id        INTEGER;
    info_id        INTEGER;
    analyse_id     INTEGER;
    valid_id       INTEGER;
    support_id     INTEGER;
    cancel_id      INTEGER;
BEGIN
    -- Usuário com apenas a role PROJECT_MANAGER
    INSERT INTO tb_users (username, name, email, encoded_password, roles) VALUES 
                         ('proj-leader', 'Project Lead', 'project_lead@morpho-board.ui', 'IwS3Mm4oGEfpwPDC3Vom20ViYgXhVCxHeBGr8aluY9tC9o668ghxJ2fMQQUwq+7GWJkzX1HguXOtdwVkblUzTw==', '{PROJECT_MANAGER}');

    -- Usuário com apenas a role USER
    INSERT INTO tb_users (username, name, email, encoded_password, roles) VALUES 
                         ('junior', 'Junior Developer', 'junior_dev@morpho-board.ui', 'IwS3Mm4oGEfpwPDC3Vom20ViYgXhVCxHeBGr8aluY9tC9o668ghxJ2fMQQUwq+7GWJkzX1HguXOtdwVkblUzTw==', '{USER}');

    -- Combinação ADMIN + PROJECT_MANAGER
    INSERT INTO tb_users (username, name, email, encoded_password, roles) VALUES 
                         ('project-boss', 'Director of Projects', 'director_projects@morpho-board.ui', 'IwS3Mm4oGEfpwPDC3Vom20ViYgXhVCxHeBGr8aluY9tC9o668ghxJ2fMQQUwq+7GWJkzX1HguXOtdwVkblUzTw==', '{ADMIN,PROJECT_MANAGER}');

    -- Combinação ADMIN + USER
    INSERT INTO tb_users (username, name, email, encoded_password, roles) VALUES 
                         ('tech-lead', 'Tech Lead', 'tech_lead@morpho-board.ui', 'IwS3Mm4oGEfpwPDC3Vom20ViYgXhVCxHeBGr8aluY9tC9o668ghxJ2fMQQUwq+7GWJkzX1HguXOtdwVkblUzTw==', '{ADMIN,USER}');

    -- Combinação PROJECT_MANAGER + USER
    INSERT INTO tb_users (username, name, email, encoded_password, roles) VALUES 
                         ('senior', 'Senior Developer', 'senior_dev@morpho-board.ui', 'IwS3Mm4oGEfpwPDC3Vom20ViYgXhVCxHeBGr8aluY9tC9o668ghxJ2fMQQUwq+7GWJkzX1HguXOtdwVkblUzTw==', '{PROJECT_MANAGER,USER}');

    -- Combinação ADMIN + PROJECT_MANAGER + USER (Super Usuário)
    INSERT INTO tb_users (username, name, email, encoded_password, roles) VALUES 
                         ('cto-boss', 'Chief Technology Officer', 'cto@morpho-board.ui', 'IwS3Mm4oGEfpwPDC3Vom20ViYgXhVCxHeBGr8aluY9tC9o668ghxJ2fMQQUwq+7GWJkzX1HguXOtdwVkblUzTw==', '{ADMIN,PROJECT_MANAGER,USER}')
                         RETURNING ID INTO user_cto_id;

    -- Usuário sem roles (se aplicável)
    INSERT INTO tb_users (username, name, email, encoded_password, roles) VALUES 
                         ('guest-user', 'Guest User', 'guest@morpho-board.ui', 'IwS3Mm4oGEfpwPDC3Vom20ViYgXhVCxHeBGr8aluY9tC9o668ghxJ2fMQQUwq+7GWJkzX1HguXOtdwVkblUzTw==', '{}');


    INSERT INTO tb_categories (name, color) VALUES ('Feature', '#01172F') RETURNING id INTO feature_id;

    INSERT INTO tb_workflow_status (name) VALUES ('TODO')        RETURNING id INTO todo_id;
    INSERT INTO tb_workflow_status (name) VALUES ('IN_PROGRESS') RETURNING id INTO progress_id;
    INSERT INTO tb_workflow_status (name) VALUES ('BLOCKED')     RETURNING id INTO blocked_id;
    INSERT INTO tb_workflow_status (name) VALUES ('DONE')        RETURNING id INTO done_id;

    INSERT INTO tb_workflows (name, start_id) VALUES ('Agile', todo_id) RETURNING id INTO agile_id;

    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (agile_id, todo_id);
    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (agile_id, progress_id);
    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (agile_id, blocked_id);
    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (agile_id, done_id);

    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (agile_id, todo_id,     progress_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (agile_id, progress_id, blocked_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (agile_id, blocked_id,  progress_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (agile_id, progress_id, done_id);

    INSERT INTO tb_workflow_status (name) VALUES ('OPEN')                RETURNING id INTO open_id;
    INSERT INTO tb_workflow_status (name) VALUES ('REQUEST_INFORMATION') RETURNING id INTO info_id;
    INSERT INTO tb_workflow_status (name) VALUES ('VALIDATING')          RETURNING id INTO valid_id;
    INSERT INTO tb_workflow_status (name) VALUES ('ANALYSING')           RETURNING id INTO analyse_id;
    INSERT INTO tb_workflow_status (name) VALUES ('CANCEL')              RETURNING id INTO cancel_id;

    INSERT INTO tb_workflows (name, start_id) VALUES ('Support', open_id) RETURNING id INTO support_id;

    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (support_id, open_id);
    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (support_id, progress_id);
    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (support_id, blocked_id);
    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (support_id, valid_id);
    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (support_id, info_id);
    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (support_id, analyse_id);
    INSERT INTO tb_workflow_statuses (workflow_id, status_id) VALUES (support_id, done_id);

    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, open_id,     analyse_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, analyse_id,  progress_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, analyse_id,  info_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, analyse_id,  cancel_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, info_id,     analyse_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, info_id,     cancel_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, info_id,     progress_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, progress_id, blocked_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, blocked_id,  progress_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, progress_id, valid_id);
    INSERT INTO tb_workflow_transitions (workflow_id, from_id, to_id) VALUES (support_id, valid_id,    done_id);

    INSERT INTO tb_projects (name, description, prefix, workflow_id) VALUES ('Morpho Board', 'MVP Morpho Board', 'MORPH', agile_id) RETURNING id INTO proj_morpho_id;

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-001',
                            'Setup do Ambiente de Desenvolvimento', 
                            'Configurar JDK 17+, Maven, Node.js e IDE (ex.: VS Code) nas máquinas do time. Documentar requisitos.',
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-002',
                            'Setup do Projeto Quarkus', 
                            'Criar projeto base com Quarkus CLI (ou Maven), configurar dependências (Hibernate, RESTEasy, JWT) e estrutura de pacotes.', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-003',
                            'Setup do Projeto Angular', 
                            'Criar projeto Angular com Angular CLI, configurar rotas básicas, HttpClient e UI Kit (ex.: Angular Material).', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-004',
                            'Configuração da Build Integrada', 
                            'Criar scripts (Maven/NPM) para build conjunta (ex.: `mvn clean install` + `ng build`). Configurar proxy no Angular para API do Quarkus.', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            todo_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-005',
                            'Modelagem do Banco de Dados', 
                            'Criar tabelas `changes`, `workflow_stages`, `users` e relações no Quarkus (usando Hibernate ORM).', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-006',
                            'API REST (Quarkus)', 
                            'Implementar endpoints para CRUD de mudanças (`/api/changes`).', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-007',
                            'Serviço de Workflow', 
                            'Lógica para transição entre estágios (ex.: `PATCH /api/changes/{id}/move`).', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-008',
                            'Frontend: Listagem Kanban', 
                            'Página Angular com colunas arrastáveis (usando `ngx-dnd` ou similar).', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-009',
                            'Frontend: Formulário de Mudança', 
                            'Componente Angular com campos dinâmicos (JSON configurável).', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-010',
                            'Autenticação Básica', 
                            'Login simples (JWT + Quarkus Security).', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-011',
                            'Notificações', 
                            'Enviar e-mails/alerts quando mudança muda de estágio (ex.: SMTP).', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            progress_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-012',
                            'Dashboard', 
                            'Gráfico simples (ex.: Chart.js) com status das mudanças.', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            todo_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-013',
                            'Logs de Histórico', 
                            'Registrar alterações no BD (ex.: "Usuário X aprovou em DD/MM").', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-014',
                            'Deploy Inicial', 
                            'Subir backend (Quarkus) e front (Angular) em servidor de teste (ex.: Docker Compose ou Heroku).', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            todo_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-015',
                            'Gestão de Usuários', 
                            'Tela de cadastro/edição', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-016',
                            'Alterar/Recuperar Senha', 
                            'Tela de alteração/recuperação de senha usando e-mail como forma de recuperação', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            todo_id,
                            NOW(), 
                            NOW());

    INSERT INTO tb_tickets (identifier, title, description, author_id, project_id, category_id, status_id, created_at, updated_at) VALUES 
                           ('MORPH-017',
                            'Gestão de Projetos', 
                            'Tela de cadastro/edição', 
                            user_cto_id,
                            proj_morpho_id,
                            feature_id,
                            done_id,
                            NOW(), 
                            NOW());
END $$;