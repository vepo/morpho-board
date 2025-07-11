import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ProjectStage {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class StagesService {
  private readonly API_URL = 'http://localhost:8080/api';

  constructor(private httpClient: HttpClient) { }

  findProjectsStages(projectId: number): Observable<ProjectStage[]> {
    return this.httpClient.get<ProjectStage[]>(`${this.API_URL}/projects/${projectId}/stages`);
  }
}
