import api from './api';
import { StationDTO } from '../types';

export const getStations = async (): Promise<StationDTO[]> => {
    const response = await api.get<StationDTO[]>('/stations');
    return response.data;
};

export const getStationInfo = async (id: number): Promise<StationDTO> => {
    const response = await api.get<StationDTO>(`/stations/${id}/info`);
    return response.data;
};
