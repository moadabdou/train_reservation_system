export interface StationDTO {
    id: number;
    name: string;
    latitude?: number;
    longitude?: number;
    description?: string;
    funFact?: string;
    imageUrl?: string;
}

export interface ScheduleDTO {
    id: number;
    trainName: string;
    departureStationName: string;
    arrivalStationName: string;
    departureTime: string; // ISO string
    arrivalTime: string; // ISO string
    price: number;
    availableSeats: number;
}

export interface RouteStopDTO {
    id: number;
    station: StationDTO;
    arrivalTime: string;
    departureTime: string;
    stopOrder: number;
}

export interface TrainPositionDTO {
    latitude: number;
    longitude: number;
    status: string;
    nextStationName: string;
    nextStationId: number;
    estimatedArrivalMinutes: number;
}

export interface LoyaltyStatusDTO {
    pointsBalance: number;
    tierLevel: string;
    pointsToNextTier: number;
    nextTier: string;
}

export interface Reward {
    id: number;
    costInPoints: number;
    description: string;
    discountValue?: number;
    type?: string;
}
