import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminAnalyticsResponse } from '../models/admin-analytics.model';

@Injectable({
  providedIn: 'root'
})
export class AdminAnalyticsService {
  private apiUrl = 'http://localhost:8080/api/admin/analytics';

  constructor(private http: HttpClient) {}

  getAnalytics(): Observable<AdminAnalyticsResponse> {
    return this.http.get<AdminAnalyticsResponse>(this.apiUrl);
  }
}
