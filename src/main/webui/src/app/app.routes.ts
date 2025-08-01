import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { KanbanComponent } from './components/kanban/kanban.component';
import { SearchTicketsComponent } from './components/search-tickets/search-tickets.component';
import { projectResolver } from './resolvers/project-resolver';
import { statusResolver } from './resolvers/status-resolver';
import { ticketsResolver } from './resolvers/tickets-resolver';
import { LoginComponent } from './components/login/login.component';
import { authGuard } from './services/auth.guard';
import { TicketViewComponent } from './components/ticket-view/ticket-view.component';
import { ticketResolver } from './resolvers/ticket.resolver';

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
    path: '',
    redirectTo: '/',
    pathMatch: 'full'
  },

  { path: '**', redirectTo: '/' }
];
