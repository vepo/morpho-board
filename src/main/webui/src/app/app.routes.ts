import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { HomeComponent } from './components/home/home.component';
import { KanbanComponent } from './components/kanban/kanban.component';
import { LoginComponent } from './components/login/login.component';
import { PasswordResetRequestComponent } from './components/password-reset-request/password-reset-request.component';
import { PasswordResetComponent } from './components/password-reset/password-reset.component';
import { ProjectEditComponent } from './components/project-edit.component/project-edit.component';
import { ProjectsViewComponent } from './components/projects-view.component/projects-view.component';
import { SearchTicketsComponent } from './components/search-tickets/search-tickets.component';
import { TicketViewComponent } from './components/ticket-view/ticket-view.component';
import { UsersEditComponent } from './components/users-edit.component/users-edit.component';
import { UsersViewComponent } from './components/users-view.component/users-view.component';
import { projectResolver, projectsResolver } from './resolvers/project-resolver';
import { statusResolver } from './resolvers/status-resolver';
import { ticketResolver } from './resolvers/ticket.resolver';
import { ticketsResolver } from './resolvers/tickets-resolver';
import { userResolver, usersResolver } from './resolvers/users.resolver';
import { workflowsResolver } from './resolvers/workflow-resolver';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'login/reset-password', component: PasswordResetRequestComponent },
  { path: 'login/reset-password/:token', component: PasswordResetComponent },
  {
    path: '',
    component: HomeComponent,
    canActivate: [authGuard]
  },
  {
    path: 'project/:projectId/kanban',
    component: KanbanComponent,
    resolve: {
      project: projectResolver,
      statuses: statusResolver,
      tickets: ticketsResolver
    },
    canActivate: [authGuard],
  },
  {
    path: 'project/:projectId/dashboard',
    component: DashboardComponent,
    resolve: {
      project: projectResolver,
      statuses: statusResolver,
      tickets: ticketsResolver
    },
    canActivate: [authGuard],
  },
  {
    path: 'search',
    component: SearchTicketsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'ticket/:ticketIdentifier',
    component: TicketViewComponent,
    resolve: {
      ticket: ticketResolver
    },
    canActivate: [authGuard],
  },
  {
    path: 'users',
    component: UsersViewComponent,
    resolve: {
      users: usersResolver
    },
    canActivate: [authGuard],
  },
  {
    path: 'users/new',
    component: UsersEditComponent,
    canActivate: [authGuard],
  },
  {
    path: 'users/:userId',
    component: UsersEditComponent,
    resolve: {
      user: userResolver
    },
    canActivate: [authGuard],
  },
  {
    path: 'projects',
    component: ProjectsViewComponent,
    resolve: {
      projects: projectsResolver
    },
    canActivate: [authGuard],
  },
  {
    path: 'projects/new',
    component: ProjectEditComponent,
    canActivate: [authGuard],
    resolve: {
      workflows: workflowsResolver
    }
  },
  {
    path: 'projects/:projectId',
    component: ProjectEditComponent,
    resolve: {
      project: projectResolver,
      workflows: workflowsResolver
    },
    canActivate: [authGuard]
  },
  {
    path: '',
    redirectTo: '/',
    pathMatch: 'full'
  },

  { path: '**', redirectTo: '/' }
];
