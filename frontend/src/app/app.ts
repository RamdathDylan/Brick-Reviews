import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  template: `
    <h1>Brick Reviews</h1>
    <p>API status: <strong>{{ status() }}</strong></p>
  `,
})
export class App {
  private http = inject(HttpClient);
  status = signal('checking...');

  constructor() {
    this.http.get<{ status: string }>('/api/health').subscribe({
      next: (res) => this.status.set(res.status),
      error: () => this.status.set('DOWN'),
    });
  }
}