import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Project } from '../../services/projects.service';
import { ProjectStage } from '../../services/stages.service';
import { Ticket } from '../../services/ticket.service';
import { ProjectsService, Workflow } from '../../services/projects.service';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CdkDragDrop } from '@angular/cdk/drag-drop';
import { TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-kanban',
  templateUrl: './kanban.component.html',
  styleUrls: ['./kanban.component.scss'],
  imports: [CommonModule, DragDropModule],
  standalone: true
})
export class KanbanComponent implements OnInit {
  stages: ProjectStage[] = [];
  project: Project = { id: '', name: '', description: '' };
  tickets: Ticket[] = [];
  workflow?: Workflow;
  constructor(private activatedRoute: ActivatedRoute, private projectsService: ProjectsService, private ticketService: TicketService) { }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stages, project, tickets }) => {
      this.stages = stages;
      this.project = project;
      this.tickets = tickets;
      this.projectsService.findWorkflowByProjectId(Number(project.id)).subscribe(workflow => {
        this.workflow = workflow;
      });
    });
  }

  ticketsOf(stageId: number): Ticket[] {
    return this.tickets.filter(ticket => ticket.stage == stageId);
  }

  onTicketDrop(event: CdkDragDrop<any>, targetStage: ProjectStage) {
    // Busca o ticket pelo index do array de origem
    const prevStageId = this.stages.find(s => this.ticketsOf(s.id).includes(event.item.data))?.id;
    let ticket: Ticket;
    if (event.item.data) {
      ticket = event.item.data;
    } else if (event.previousContainer && event.previousIndex != null) {
      ticket = event.previousContainer.data[event.previousIndex];
    } else {
      return;
    }
    if (!this.workflow) return;
    const currentStage = this.stages.find(s => s.id === ticket.stage);
    if (!currentStage) return;
    const isAllowed = this.workflow.transitions.some(t => t.from === currentStage.name && t.to === targetStage.name);
    if (!isAllowed) {
      alert('Transição não permitida pelo workflow!');
      return;
    }
    this.ticketService.moveTicket(ticket.id, targetStage.id).subscribe({
      next: () => {
        ticket.stage = targetStage.id;
      },
      error: () => alert('Erro ao mover ticket!')
    });
  }
} 