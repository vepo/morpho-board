import { CdkDragDrop, DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Project, ProjectsService, Workflow } from '../../services/projects.service';
import { ProjectStatus } from '../../services/status.service';
import { Ticket, TicketService } from '../../services/ticket.service';
import { NormalizePipe } from '../pipes/normalize.pipe';

@Component({
  selector: 'app-kanban',
  templateUrl: './kanban.component.html',
  styleUrls: ['./kanban.component.scss'],
  imports: [CommonModule, DragDropModule, RouterLink, NormalizePipe],
  standalone: true
})
export class KanbanComponent implements OnInit {
  statuses: ProjectStatus[] = [];
  project: Project = { id: -1, name: '', description: '' };
  tickets: Ticket[] = [];
  workflow?: Workflow;
  constructor(private readonly activatedRoute: ActivatedRoute, 
              private readonly projectsService: ProjectsService, 
              private readonly ticketService: TicketService) { }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ statuses, project, tickets }) => {
      this.project = project;
      this.tickets = (tickets as Ticket[]).map(t => this.fixLineBreak(t));
      this.statuses = statuses;
      this.projectsService.findWorkflowByProjectId(Number(project.id))
                          .subscribe(workflow => this.workflow = workflow);
    });
  }

  fixLineBreak(ticket: Ticket): Ticket {
    return {
      id: ticket.id,
      title: ticket.title,
      description: ticket.description.replaceAll('\n', '<br/>'),
      author: ticket.author,
      project: ticket.project,
      status: ticket.status,
      assignee: ticket.assignee,
      category:ticket.category
    };
  }

  ticketsOf(statusId: number): Ticket[] {
    return this.tickets.filter(ticket => ticket.status == statusId);
  }

  connectedTo(status: ProjectStatus): string[] {
    return status.moveable.map(id => 'column-' + id);
  }

  toColumnId(status: ProjectStatus): string {
    return `column-${status.id}`;
  }

  fromColumnId(columnId: string): number {
    return Number(columnId.replace('column-', ''));
  }

  drop(evnt: CdkDragDrop<any>) {
    var ticket = (evnt.previousContainer.data[evnt.previousIndex] as Ticket);
    var statusId = this.fromColumnId(evnt.container.id);
    if (ticket.status != statusId) {
      this.ticketService.move(ticket.id, statusId)
                        .subscribe(ticket => this.tickets[this.tickets.findIndex(t => t.id == ticket.id)] =  this.fixLineBreak(ticket));
    }
  }
} 