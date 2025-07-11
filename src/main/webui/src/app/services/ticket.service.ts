import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Ticket {
  id: number;
  title: string;
  description: string;
  category?: number;
  author: number;
  assignee?: number;
  project: number;
  stage: number;
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
}
