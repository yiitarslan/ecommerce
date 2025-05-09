// src/app/core/models/order.model.ts

/**
 * Order servisten dönen sipariş bilgisini temsil eden model
 */
export interface Order {
    id: number;
    userId: number;
    totalAmount: number;
    items: Array<{
      productId: number;
      quantity: number;
      price: number;
    }>;
    createdAt: string;  // ISO tarih dizgesi
  }
  