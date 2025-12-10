import api from './api';

export interface PassengerRequest {
    name: string;
    age: number;
}

export interface CreateBookingRequest {
    scheduleId: number;
    passengers: PassengerRequest[];
}

export interface BookingResponse {
    bookingId: number;
    referenceCode: string;
    status: string;
    totalPrice: number;
}

export interface BookingSummary {
    bookingId: number;
    referenceCode: string;
    scheduleId: number;
    bookingDate: string;
    status: 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'PENDING_PAYMENT';
    passengersCount: number;
    totalPrice: number;
    // Additional fields from schedule (we'll join these on frontend or backend)
    departureStationName?: string;
    arrivalStationName?: string;
    departureTime?: string;
    arrivalTime?: string;
}

export interface BookingsPage {
    content: BookingSummary[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
}

export const createBooking = async (request: CreateBookingRequest): Promise<BookingResponse> => {
    const response = await api.post<BookingResponse>('/bookings', request);
    return response.data;
};

export const getMyBookings = async (page: number = 0, size: number = 10): Promise<BookingsPage> => {
    const response = await api.get<BookingsPage>('/bookings', {
        params: { page, size }
    });
    return response.data;
};

export const cancelBooking = async (referenceCode: string): Promise<void> => {
    await api.delete(`/bookings/${referenceCode}`);
};

export interface ReceiptDTO {
    bookingReference: string;
    bookingDate: string;
    trainName: string;
    departureStation: string;
    arrivalStation: string;
    departureTime: string;
    arrivalTime: string;
    passengerNames: string[];
    totalAmount: number;
    paymentStatus: string;
    transactionId: string;
}

export const getBookingReceipt = async (referenceCode: string): Promise<ReceiptDTO> => {
    const response = await api.get<ReceiptDTO>(`/bookings/${referenceCode}/receipt`);
    return response.data;
};

export const getBookingDetails = async (referenceCode: string): Promise<BookingResponse> => {
    const response = await api.get<BookingResponse>(`/bookings/${referenceCode}`);
    return response.data;
};

export interface PaymentRequest {
    bookingId: number;
    paymentMethod: string;
    amount: number;
    cardNumber: string;
    expiryDate: string;
    cvv: string;
}

export const processPayment = async (request: PaymentRequest): Promise<void> => {
    await api.post('/payments/process', request);
};
