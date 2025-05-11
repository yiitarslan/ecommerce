import { Component, OnInit } from '@angular/core';
import { AdminAnalyticsService } from '../../../../core/services/admin-analytics.service';
import { AdminAnalyticsResponse } from '../../../../core/models/admin-analytics.model';
import { ChartData, ChartType, ChartOptions } from 'chart.js';

@Component({
  selector: 'app-admin-analytics',
  standalone:false,
  templateUrl: './admin-analytics.component.html',
  styleUrls: ['./admin-analytics.component.scss']
})
export class AdminAnalyticsComponent implements OnInit {
  analytics: AdminAnalyticsResponse | null = null;

  public orderChartData: ChartData<'bar'> = {
    labels: ['Toplam Sipariş'],
    datasets: [{ label: 'Sipariş Sayısı', data: [0], backgroundColor: '#4F46E5' }]
  };

  public revenueChartData: ChartData<'bar'> = {
    labels: ['Toplam Gelir'],
    datasets: [{ label: 'Gelir (₺)', data: [0], backgroundColor: '#10B981' }]
  };

  public topProductChartData: ChartData<'pie'> = {
    labels: [],
    datasets: [{ label: 'Ürün Satışları', data: [], backgroundColor: [] }]
  };

  public topCustomerChartData: ChartData<'pie'> = {
    labels: [],
    datasets: [{ label: 'Müşteri Siparişleri', data: [], backgroundColor: [] }]
  };

  public chartOptions: ChartOptions = {
    responsive: true,
    plugins: {
      legend: { position: 'top' }
    }
  };

  constructor(private analyticsService: AdminAnalyticsService) {}

  ngOnInit(): void {
    this.analyticsService.getAnalytics().subscribe(data => {
      this.analytics = data;

      this.orderChartData.datasets[0].data = [data.totalOrders];
      this.revenueChartData.datasets[0].data = [data.totalRevenue];

      this.topProductChartData.labels = Object.keys(data.productSales);
      this.topProductChartData.datasets[0].data = Object.values(data.productSales);
      this.topProductChartData.datasets[0].backgroundColor = Object.keys(data.productSales).map(() =>
        this.getRandomColor()
      );

      this.topCustomerChartData.labels = Object.keys(data.customerOrders);
      this.topCustomerChartData.datasets[0].data = Object.values(data.customerOrders);
      this.topCustomerChartData.datasets[0].backgroundColor = Object.keys(data.customerOrders).map(() =>
        this.getRandomColor()
      );
    });
  }

  private getRandomColor(): string {
    const colors = ['#F59E0B', '#6366F1', '#EF4444', '#10B981', '#3B82F6'];
    return colors[Math.floor(Math.random() * colors.length)];
  }
}
