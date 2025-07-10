import { Routes } from '@angular/router';
import { KanbanComponent } from './kanban/kanban.component';
import { HomeComponent } from './home/home.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'kanban', component: KanbanComponent },
  { path: '**', redirectTo: '' },
];
