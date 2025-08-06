import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { KanbanComponent } from './components/kanban/kanban.component';
import { LoginComponent } from './components/login/login.component';
import { SearchTicketsComponent } from './components/search-tickets/search-tickets.component';
import { TicketViewComponent } from './components/ticket-view/ticket-view.component';
import { UsersViewComponent } from './components/users-view.component/users-view.component';
import { projectResolver } from './resolvers/project-resolver';
import { statusResolver } from './resolvers/status-resolver';
import { ticketResolver } from './resolvers/ticket.resolver';
import { ticketsResolver } from './resolvers/tickets-resolver';
import { usersResolver } from './resolvers/users.resolver';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: HomeComponent
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
    path: 'search',
    component: SearchTicketsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'ticket/:ticketId',
    component: TicketViewComponent,
    resolve: {
      ticket: ticketResolver
    }
  },
  {
    path: 'users',
    component: UsersViewComponent,
    resolve: {
      users: usersResolver
    }
  },
  {
    path: '',
    redirectTo: '/',
    pathMatch: 'full'
  },

  { path: '**', redirectTo: '/' }
];
