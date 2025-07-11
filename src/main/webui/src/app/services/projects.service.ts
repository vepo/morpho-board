import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Project {
  id: string;
  name: string;
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
}
