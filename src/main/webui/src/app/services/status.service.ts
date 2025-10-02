import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface ProjectStatus {
  id: number;
  name: string;
  moveable: number[];
}

export interface Status {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class StatusService {
  private readonly httpClient = inject(HttpClient);

  private readonly API_URL = 'http://localhost:8080/api';

  findProjectsStatuses(projectId: number): Observable<ProjectStatus[]> {
    return this.httpClient.get<ProjectStatus[]>(`${this.API_URL}/projects/${projectId}/status`);
  }

  findAll(): Observable<Status[]> {
    return this.httpClient.get<ProjectStatus[]>(`${this.API_URL}/status/`);
  }
}
