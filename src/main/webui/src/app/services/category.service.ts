import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface Category {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
 private readonly http = inject(HttpClient);

 private readonly API_URL = 'http://localhost:8080/api/categories';
  
  public findAll():Observable<Category[]> {
    return this.http.get<Category[]>(`${this.API_URL}`);
  }
}
