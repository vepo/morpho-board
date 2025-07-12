import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { KanbanComponent } from './components/kanban/kanban.component';
import { SearchTicketsComponent } from './components/search-tickets/search-tickets.component';
import { projectResolver } from './resolvers/project-resolver';
import { statusResolver } from './resolvers/status-resolver';
import { ticketsResolver } from './resolvers/tickets-resolver';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'kanban/:projectId',
    component: KanbanComponent,
    resolve: {
      project: projectResolver,
      statuses: statusResolver,
      tickets: ticketsResolver
    }
  },
  { path: 'search', component: SearchTicketsComponent },
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: '**', redirectTo: '/' }
];
