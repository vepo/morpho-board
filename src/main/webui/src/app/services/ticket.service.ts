import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Ticket {
  id: number;
  identifier: string;
  title: string;
  description: string;
  category?: number;
  author: number;
  assignee?: number;
  project: number;
  status: number;
}

export interface TicketUser {
  id: number;
  name: string;
  email: string;
}

export interface TicketHistory {
  description: string;
  user: TicketUser;
  timestamp: number;
}

export interface TicketProject {
  id: number;
  name: string;
}

export interface Comment {
  id: number;
  author: TicketUser;
  content: string;
  createdAt: number;
  isHtml?: boolean;
}

export interface CreateCommentRequest {
  content: string;
}

export interface TicketExpanded {
  id: number;
  title: string;
  description: string;
  category: string;
  author: TicketUser;
  assignee?: TicketUser;
  project: TicketProject;
  status: string;
  history: TicketHistory[];
}

export interface CreateTicketRequest {
  title: string;
  description: string;
  categoryId: number;
  authorId: number;
  assigneeId?: number;
  projectId: number;
}

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  findByProjectId(projectId: number): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`/api/projects/${projectId}/tickets`);
  }

  findById(ticketId: number): Observable<Ticket> {
    return this.http.get<Ticket>(`/api/tickets/${ticketId}`);
  }

  findExpandedById(ticketId: number): Observable<TicketExpanded> {
    return this.http.get<TicketExpanded>(`/api/tickets/${ticketId}/expanded`);
  }

  findExpandedByIdentifier(ticketIdentifier: string): Observable<TicketExpanded> {
    return this.http.get<TicketExpanded>(`/api/tickets/${ticketIdentifier}/expanded`);
  }

  search(term: string, status: number): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.API_URL}/tickets/search`, {
      params: {
        term: term,
        statusId: status
      }
    });
  }

  move(ticketId: number, toStatus: number) :Observable<Ticket> {
    return this.http.post<Ticket>(`/api/tickets/${ticketId}/move`, { to: toStatus });
  }

  createTicket(request: CreateTicketRequest): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.API_URL}/tickets`, request);
  }

  getTicket(id: string) {
    return this.http.get<any>(`/api/tickets/${id}`);
  }

  getTicketHistory(id: string) {
    return this.http.get<any[]>(`/api/tickets/${id}/history`);
  }

  getComments(ticketId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`/api/tickets/${ticketId}/comments`);
  }

  addComment(ticketId: number, request: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(`/api/tickets/${ticketId}/comments`, request);
  }
}
