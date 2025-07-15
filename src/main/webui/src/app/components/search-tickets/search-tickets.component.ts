import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Project, ProjectsService } from '../../services/projects.service';
import { Status, StatusService } from '../../services/status.service';
import { Ticket, TicketService } from '../../services/ticket.service';

@Component({
  selector: 'app-search-tickets',
  templateUrl: './search-tickets.component.html',
  styleUrls: ['./search-tickets.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class SearchTicketsComponent implements OnInit {
  tickets: Ticket[] = [];
  statuses: Status[] = [];
  projects: Project[] = [];
  loading = false;
  error = '';
  term = '';
  statusId: number = -1;

  constructor(private route: ActivatedRoute,
              private ticketService: TicketService,
              private statusService: StatusService,
              private projectService: ProjectsService) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.term = params['q'] || '';
      this.statusId = Number(params['status'] || '-1');
      this.searchTickets();
    });
    this.statusService.findAll()
                      .subscribe(statuses => this.statuses = statuses);
    this.projectService.findAll()
                       .subscribe(projects => this.projects = projects);
  }

  statusName(statusId: number): string {
    let status = this.statuses.find(s => s.id == statusId);
    if (status) {
      return status.name.split('_').map(i => i.substring(0, 1).toUpperCase() + i.substring(1).toLowerCase()).join(' ');
    } else {
      return "";
    }
  }

  projectName(projectId: number): string {
    let project = this.projects.find(p => p.id == projectId);
    if (project) {
      return project.name;
    } else {
      return "";
    }
  }

  searchTickets() {
    this.ticketService.search(this.term, this.statusId)
                      .subscribe(tickets => this.tickets = tickets);
  }
} 