export interface PaymentRequest {
    userId: number;
    orderId: number;
    amount: number;
    paymentMethod: string;
  }
  
  export interface StripePaymentResponse {
    clientSecret: string;
    paymentId: number;
  }
  