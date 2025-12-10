import api from './api';
import { ScheduleDTO, RouteStopDTO } from '../types';

export const searchSchedules = async (from: number, to: number, date: string): Promise<ScheduleDTO[]> => {
    const response = await api.get<ScheduleDTO[]>('/schedules', {
        params: { from, to, date }
    });
    return response.data;
};

export const getRoute = async (id: number): Promise<RouteStopDTO[]> => {
    const response = await api.get<RouteStopDTO[]>(`/schedules/${id}/route`);
    return response.data;
};
