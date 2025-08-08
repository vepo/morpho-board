import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface User {
  id: number;
  name: string;
  email: string;
  roles: string[];
}

export interface UserSearchFilter {
  name: string;
  email: string;
  roles: string[];
}

export interface UpdateOrCreateUserRequest {
  name: string;
  email: string;
  roles: string[];
}

export function emptyFilter(): UserSearchFilter {
  return {
    name: '',
    email: '',
    roles: []
  };
}

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  constructor(private readonly http: HttpClient) { }
  private readonly API_URL = 'http://localhost:8080/api';

  findById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.API_URL}/users/${userId}`);
  }

  create(user: UpdateOrCreateUserRequest): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/users`, user);
  }

  update(userId: number, user: UpdateOrCreateUserRequest): Observable<User> {
    return this.http.post<User>(`${this.API_URL}/users/${userId}`, user);
  }

  search(filter?: UserSearchFilter): Observable<User[]> {
    let params = new HttpParams();

    if (filter) {
      Object.keys(filter).forEach(key => {
        const value = filter[key as keyof UserSearchFilter];

        // Skip null, undefined, empty string, or empty array
        if (value !== null && value !== undefined && value !== '' &&
          !(Array.isArray(value) && value.length === 0)) {

          // Handle array values by joining them or adding multiple params
          if (Array.isArray(value)) {
            console.debug("Appending query array", value);
            value.forEach(item => params = params.append(key, item));
          } else {
            params = params.append(key, value.toString());
          }
        }
      });
    }

    return this.http.get<User[]>(`${this.API_URL}/users/search`, { params: params });
  }
}
