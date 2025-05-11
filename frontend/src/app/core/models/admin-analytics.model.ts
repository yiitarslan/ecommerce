export interface AdminAnalyticsResponse {
  totalOrders: number;
  totalRevenue: number;
  topProduct: string;
  topCustomer: string;
  productSales: Record<string, number>;
  customerOrders: Record<string, number>;
}
