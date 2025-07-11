import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Project } from '../../services/projects.service';
import { ProjectStage } from '../../services/stages.service';
import { Ticket } from '../../services/ticket.service';

@Component({
  selector: 'app-kanban',
  templateUrl: './kanban.component.html',
  styleUrls: ['./kanban.component.scss'],
  imports: [CommonModule],
  standalone: true
})
export class KanbanComponent implements OnInit {
  stages: ProjectStage[] = [];
  project: Project = { id: '', name: '' };
  tickets: Ticket[] = [];
  constructor(private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stages, project, tickets }) => {
      this.stages = stages;
      this.project = project;
      this.tickets = tickets;
    });
  }

  ticketsOf(stageId: number): Ticket[] {
    return this.tickets.filter(ticket => ticket.stage == stageId);
  }
} 