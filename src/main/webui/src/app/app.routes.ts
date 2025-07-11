import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { KanbanComponent } from './components/kanban/kanban.component';
import { projectResolver } from './resolvers/project-resolver';
import { stagesResolver } from './resolvers/stages-resolver';
import { ticketsResolver } from './resolvers/tickets-resolver';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'kanban/:projectId',
    component: KanbanComponent,
    resolve: {
      project: projectResolver,
      stages: stagesResolver,
      tickets: ticketsResolver
    }
  },
  { path: '', redirectTo: '/', pathMatch: 'full' },
  { path: '**', redirectTo: '/' }
];
