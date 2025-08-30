import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Project {
  id: number;
  name: string;
  prefix: string;
  description: string;
}

export interface WorkflowTransition {
  from: string;
  to: string;
}

export interface ProjectWorkflow {
  id: number;
  name: string;
  statuses: string[];
  start: string;
  transitions: WorkflowTransition[];
}

export interface CreateOrUpdateProjectRequest {
  name: string;
  prefix: string;
  description: string;
  workflowId: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectsService {
  private readonly API_URL = 'http://localhost:8080/api/projects';

  constructor(private readonly http: HttpClient) { }

  findById(projectId: number): Observable<Project> {
    return this.http.get<Project>(`${this.API_URL}/${projectId}`);
  }

  findAll(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.API_URL}`);
  }

  create(request: CreateOrUpdateProjectRequest): Observable<Project> {
    return this.http.post<Project>(`${this.API_URL}`, request);
  }


  update(projectId: number, request: CreateOrUpdateProjectRequest): Observable<Project> {
    return this.http.post<Project>(`${this.API_URL}/${projectId}`, request);
  }

  findWorkflowByProjectId(projectId: number): Observable<ProjectWorkflow> {
    return this.http.get<ProjectWorkflow>(`${this.API_URL}/${projectId}/workflow`);
  }
}
