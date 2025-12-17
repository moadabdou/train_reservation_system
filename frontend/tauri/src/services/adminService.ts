import api from "./api";
import { ScheduleDTO } from "../types";

export interface Provider {
    id?: number;
    name: string;
    logoUrl: string;
    contactInfo: string;
}

export interface Train {
    id?: number;
    name: string;
    providerId: number | null;
    providerName?: string;
    trainLayoutId?: number | null;
    trainLayoutName?: string;
    totalSeats: number;
}

export interface Station {
    id?: number;
    name: string;
    latitude: number;
    longitude: number;
    description: string;
    imageUrl: string;
    facilities: string;
}

export interface RouteDefinition {
    id?: number;
    stationId: number;
    stationName?: string;
    stopOrder: number;
    distanceFromPrevKm: number;
    standardTravelTimeMins: number;
}

export interface Route {
    id?: number;
    name: string;
    description: string;
    definitions: RouteDefinition[];
}

export interface User {
    id: number;
    name: string;
    email: string;
    role: string;
    status: "ACTIVE" | "BANNED";
}

export interface BookingSummary {
    bookingId: number;
    referenceCode: string;
    scheduleId: number;
    bookingDate: string;
    status: string;
    passengersCount: number;
    totalPrice: number;
    userEmail: string;
}

export interface Payment {
    id: number;
    bookingId: number;
    amount: number;
    paymentMethod: string;
    transactionId: string;
    paymentDate: string;
    status: "PENDING" | "COMPLETED" | "FAILED" | "REFUNDED";
}

export interface FinancialStats {
    totalRevenue: number;
    totalRefunds: number;
    revenueByRoute: Record<string, number>;
}

export interface DashboardStats {
    totalUsers: number;
    activeTrains: number;
    bookingsToday: number;
    revenueToday: number;
    totalRevenue: number;
    cancellationRate: number;
    topRoute: string;
    occupancyRate: number;
    recentBookings: BookingSummary[];
    upcomingDepartures: ScheduleDTO[];
}

export interface AdminTrainPosition {
    trainId: number;
    trainName: string;
    routeName: string;
    latitude: number;
    longitude: number;
    status: string;
    nextStationName: string;
}

export const adminService = {
    // Providers
    getAllProviders: async () => {
        const response = await api.get<Provider[]>("/admin/providers");
        return response.data;
    },
    createProvider: async (provider: Provider) => {
        const response = await api.post<Provider>("/admin/providers", provider);
        return response.data;
    },
    updateProvider: async (id: number, provider: Provider) => {
        const response = await api.put<Provider>(`/admin/providers/${id}`, provider);
        return response.data;
    },
    deleteProvider: async (id: number) => {
        await api.delete(`/admin/providers/${id}`);
    },

    // Trains
    getAllTrains: async () => {
        const response = await api.get<Train[]>("/admin/trains");
        return response.data;
    },
    createTrain: async (train: Train) => {
        const response = await api.post<Train>("/admin/trains", train);
        return response.data;
    },
    updateTrain: async (id: number, train: Train) => {
        const response = await api.put<Train>(`/admin/trains/${id}`, train);
        return response.data;
    },
    deleteTrain: async (id: number) => {
        await api.delete(`/admin/trains/${id}`);
    },

    // Stations
    getAllStations: async () => {
        const response = await api.get<Station[]>("/admin/stations");
        return response.data;
    },
    createStation: async (station: Station) => {
        const response = await api.post<Station>("/admin/stations", station);
        return response.data;
    },
    updateStation: async (id: number, station: Station) => {
        const response = await api.put<Station>(`/admin/stations/${id}`, station);
        return response.data;
    },
    deleteStation: async (id: number) => {
        await api.delete(`/admin/stations/${id}`);
    },

    // Routes
    getAllRoutes: async () => {
        const response = await api.get<Route[]>("/admin/routes");
        return response.data;
    },
    createRoute: async (route: Route) => {
        const response = await api.post<Route>("/admin/routes", route);
        return response.data;
    },
    deleteRoute: async (id: number) => {
        await api.delete(`/admin/routes/${id}`);
    },

    // File Upload
    uploadFile: async (file: File) => {
        const formData = new FormData();
        formData.append("file", file);
        const response = await api.post<string>("/admin/upload", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });
        return response.data;
    },

    // Users
    getAllUsers: async (page = 0, size = 10) => {
        const response = await api.get<{ content: User[]; totalPages: number }>(
            `/admin/users?page=${page}&size=${size}`
        );
        return response.data;
    },
    banUser: async (id: number) => {
        await api.post(`/admin/users/${id}/ban`);
    },
    unbanUser: async (id: number) => {
        await api.post(`/admin/users/${id}/unban`);
    },

    // Bookings
    getAllBookings: async (page = 0, size = 10) => {
        const response = await api.get<{ content: BookingSummary[]; totalPages: number }>(
            `/admin/bookings?page=${page}&size=${size}`
        );
        return response.data;
    },
    cancelBooking: async (id: number) => {
        await api.post(`/admin/bookings/${id}/cancel`);
    },

    // Schedules
    getAllSchedules: async () => {
        const response = await api.get<ScheduleDTO[]>("/admin/schedules");
        return response.data;
    },
    deleteSchedule: async (id: number) => {
        await api.delete(`/admin/schedules/${id}`);
    },
    generateSchedule: async (request: {
        routeId: number;
        trainId: number;
        startTime: string;
        basePrice: number;
        includeIntermediateStops: boolean;
    }) => {
        const response = await api.post("/admin/schedules/generate", request);
        return response.data;
    },

    // Payments & Financials
    getAllPayments: async () => {
        const response = await api.get<Payment[]>("/admin/payments");
        return response.data;
    },
    validatePayment: async (id: number) => {
        const response = await api.post<Payment>(`/admin/payments/${id}/validate`);
        return response.data;
    },
    getFinancialStats: async () => {
        const response = await api.get<FinancialStats>("/admin/stats/financial");
        return response.data;
    },

    // Dashboard & Analytics
    getDashboardStats: async () => {
        const response = await api.get<DashboardStats>("/admin/dashboard-stats");
        return response.data;
    },
    getLiveMap: async () => {
        const response = await api.get<AdminTrainPosition[]>("/admin/live-map");
        return response.data;
    },

    // Loyalty & Rewards
    getAllRewards: async () => {
        const response = await api.get<Reward[]>("/admin/loyalty/rewards");
        return response.data;
    },
    createReward: async (reward: Omit<Reward, "id">) => {
        const response = await api.post<Reward>("/admin/loyalty/rewards", reward);
        return response.data;
    },
    updateReward: async (id: number, reward: Reward) => {
        const response = await api.put<Reward>(`/admin/loyalty/rewards/${id}`, reward);
        return response.data;
    },
    deleteReward: async (id: number) => {
        await api.delete(`/admin/loyalty/rewards/${id}`);
    },
    getAllLoyaltyRules: async () => {
        const response = await api.get<LoyaltyRule[]>("/admin/loyalty/rules");
        return response.data;
    },
    updateLoyaltyRule: async (ruleName: string, value: number) => {
        const response = await api.put<LoyaltyRule>(`/admin/loyalty/rules/${ruleName}`, { value });
        return response.data;
    },

    // Content Management (Smart Companion)
    getAllCityGuides: async () => {
        const response = await api.get<CityGuide[]>("/admin/content/cities");
        return response.data;
    },
    saveCityGuide: async (guide: CityGuide) => {
        if (guide.id) {
            const response = await api.put<CityGuide>(`/admin/content/cities/${guide.id}`, guide);
            return response.data;
        } else {
            const response = await api.post<CityGuide>("/admin/content/cities", guide);
            return response.data;
        }
    },
    deleteCityGuide: async (id: number) => {
        await api.delete(`/admin/content/cities/${id}`);
    },

    getAllOnboardItems: async () => {
        const response = await api.get<OnboardItem[]>("/admin/content/items");
        return response.data;
    },
    saveOnboardItem: async (item: OnboardItem) => {
        if (item.id) {
            const response = await api.put<OnboardItem>(`/admin/content/items/${item.id}`, item);
            return response.data;
        } else {
            const response = await api.post<OnboardItem>("/admin/content/items", item);
            return response.data;
        }
    },
    deleteOnboardItem: async (id: number) => {
        await api.delete(`/admin/content/items/${id}`);
    },

    // Pricing & Layouts
    getAllLayouts: async () => {
        const response = await api.get<TrainLayout[]>("/admin/pricing/layouts");
        return response.data;
    },
    saveLayout: async (layout: TrainLayout) => {
        const response = await api.post<TrainLayout>("/admin/pricing/layouts", layout);
        return response.data;
    },
    deleteLayout: async (id: number) => {
        await api.delete(`/admin/pricing/layouts/${id}`);
    },

    getAllPricingRules: async () => {
        const response = await api.get<PricingRule[]>("/admin/pricing/rules");
        return response.data;
    },
    savePricingRule: async (rule: PricingRule) => {
        const response = await api.post<PricingRule>("/admin/pricing/rules", rule);
        return response.data;
    },
    deletePricingRule: async (id: number) => {
        await api.delete(`/admin/pricing/rules/${id}`);
    },
};

export interface Reward {
    id: number;
    description: string;
    costInPoints: number;
    discountValue?: number;
    type: string; // DISCOUNT, UPGRADE, FREE_TICKET
}

export interface LoyaltyRule {
    id: number;
    ruleName: string;
    value: number;
    description: string;
}

export interface CityGuide {
    id?: number;
    cityName: string;
    content: string;
    weatherApiId?: string;
}

export interface OnboardItem {
    id?: number;
    name: string;
    price: number;
    category: string;
    available: boolean;
}

export interface TrainLayout {
    id?: number;
    layoutName: string;
    totalRows: number;
    seatsPerRow: number;
    layoutConfig: string; // JSON string
}

export interface PricingRule {
    id?: number;
    ruleName: string;
    conditionType: string;
    conditionValue: string;
    multiplier: number;
    isActive: boolean;
}
