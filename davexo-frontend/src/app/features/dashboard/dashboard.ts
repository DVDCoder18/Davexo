import { Component, inject } from '@angular/core';
import { Auth } from '../../core/services/auth';
import { DashboardService } from '../../api';

@Component({
  imports: [],
  selector: 'app-dashboard',
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html',
})
export class Dashboard {

  private readonly auth = inject(Auth)

  logout() {
    this.auth.logout();
  }
}
