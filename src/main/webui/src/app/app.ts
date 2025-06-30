import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'morphoboard';

  constructor(http: HttpClient) {
    // Initialize the application, e.g., fetch initial data
    http.get('/api/changes').subscribe(data => {
      console.log('Initial data loaded:', data);
    });
  }
}
