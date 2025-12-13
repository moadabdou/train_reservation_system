import api from "./api";

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
};
