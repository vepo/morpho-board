import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

interface Ticket {
  id: number;
  title: string;
  description: string;
  categoryId: number;
  authorId: number;
  assigneeId: number;
  projectId: number;
  workflowStageId?: number;
}

interface KanbanColumn {
  status: string;
  statusId: number;
  tickets: Ticket[];
}

@Component({
  selector: 'app-kanban',
  templateUrl: './kanban.component.html',
  styleUrls: ['./kanban.component.scss'],
  imports: [CommonModule, HttpClientModule],
  standalone: true
})
export class KanbanComponent implements OnInit {
  columns: KanbanColumn[] = [];
  loading = true;
  error = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchTickets();
  }

  fetchTickets() {
    this.loading = true;
    this.error = '';
    
    this.http.get<Ticket[]>('/api/tickets').subscribe({
      next: (tickets) => {
        this.organizeTicketsIntoColumns(tickets);
        this.loading = false;
      },
      error: (err) => {
        console.error('Erro ao buscar tickets:', err);
        this.error = 'Erro ao carregar tickets';
        this.loading = false;
      }
    });
  }

  private organizeTicketsIntoColumns(tickets: Ticket[]) {
    // Agrupar tickets por workflowStageId
    const groupedTickets = new Map<number, Ticket[]>();
    
    tickets.forEach(ticket => {
      const stageId = ticket.workflowStageId || 0;
      if (!groupedTickets.has(stageId)) {
        groupedTickets.set(stageId, []);
      }
      groupedTickets.get(stageId)!.push(ticket);
    });

    // Criar colunas
    this.columns = Array.from(groupedTickets.entries()).map(([stageId, tickets]) => ({
      status: `Estágio ${stageId}`,
      statusId: stageId,
      tickets: tickets
    }));
  }
} 