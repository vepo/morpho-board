import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface WorkflowTranstion {
  from: string;
  to: string;
}
export interface Workflow {
  id: number;
  name: string;
  start: string;
  statuses: string[];
  transitions:  WorkflowTranstion[];
}

@Injectable({
  providedIn: 'root'
})
export class WorkflowService {
  private readonly httpClient = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080/api';

  findAll(): Observable<Workflow[]> {
    return this.httpClient.get<Workflow[]>(`${this.API_URL}/workflows`);
  }

  findById(workflowId: number): Observable<Workflow> {
    return this.httpClient.get<Workflow>(`${this.API_URL}/workflows/${workflowId}`);
  }

}
