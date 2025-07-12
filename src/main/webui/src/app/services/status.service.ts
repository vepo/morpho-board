import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ProjectStatus {
  id: number;
  name: string;
  moveable?: number[]; // ids de estágios para os quais é permitido mover
}

export interface Status {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class StatusService {
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private httpClient: HttpClient) { }

  findProjectsStatuses(projectId: number): Observable<ProjectStatus[]> {
    return this.httpClient.get<ProjectStatus[]>(`${this.API_URL}/projects/${projectId}/status`);
  }

  findAll(): Observable<Status[]> {
    return this.httpClient.get<ProjectStatus[]>(`${this.API_URL}/status/`);
  }
}
