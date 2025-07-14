import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Project {
  id: number;
  name: string;
  description: string;
}

export interface WorkflowTransition {
  from: string;
  to: string;
}

export interface Workflow {
  id: number;
  name: string;
  statuses: string[];
  start: string;
  transitions: WorkflowTransition[];
}

@Injectable({
  providedIn: 'root'
})
export class ProjectsService {
  private readonly API_URL = 'http://localhost:8080/api/projects';

  constructor(private http: HttpClient) { }

  findById(projectId: number): Observable<Project> {
    return this.http.get<Project>(`${this.API_URL}/${projectId}`);
  }

  findAll(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.API_URL}`);
  }

  findWorkflowByProjectId(projectId: number) {
    return this.http.get<Workflow>(`${this.API_URL}/${projectId}/workflow`);
  }
}
