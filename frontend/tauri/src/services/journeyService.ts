import api from "./api";
import { RouteStopDTO, TrainPositionDTO } from "../types";

export const getRouteStops = async (scheduleId: number): Promise<RouteStopDTO[]> => {
    const response = await api.get<RouteStopDTO[]>(`/schedules/${scheduleId}/route`);
    return response.data;
};

export const getTrainPosition = async (scheduleId: number): Promise<TrainPositionDTO> => {
    const response = await api.get<TrainPositionDTO>(`/schedules/${scheduleId}/position`);
    return response.data;
};

export const getRoutePath = async (stops: RouteStopDTO[]): Promise<[number, number][]> => {
    const validStops = stops.filter((s) => s.station.latitude != null && s.station.longitude != null);

    if (validStops.length < 2) {
        if (validStops.length === 1) {
            return [[validStops[0].station.latitude!, validStops[0].station.longitude!]];
        }
        return [];
    }

    // Use OSRM to get a real path
    const coordinates = validStops.map((s) => `${s.station.longitude},${s.station.latitude}`).join(";");

    try {
        const response = await fetch(
            `https://router.project-osrm.org/route/v1/driving/${coordinates}?overview=full&geometries=geojson`
        );
        const data = await response.json();

        if (data.routes && data.routes.length > 0) {
            // OSRM returns [lon, lat], Leaflet needs [lat, lon]
            const coords = data.routes[0].geometry.coordinates;
            const path = coords.map((coord: number[]) => [coord[1], coord[0]] as [number, number]);

            // Sanity check: Ensure path starts near the first station
            if (path.length > 0 && validStops.length > 0) {
                const firstStop = validStops[0].station;
                const firstPoint = path[0];
                const lastPoint = path[path.length - 1];

                const distToFirst = Math.sqrt(
                    Math.pow(firstPoint[0] - firstStop.latitude!, 2) + Math.pow(firstPoint[1] - firstStop.longitude!, 2)
                );
                const distToLast = Math.sqrt(
                    Math.pow(lastPoint[0] - firstStop.latitude!, 2) + Math.pow(lastPoint[1] - firstStop.longitude!, 2)
                );

                if (distToLast < distToFirst) {
                    // Path seems reversed, flip it
                    return path.reverse();
                }
            }
            return path;
        }
    } catch (error) {
        console.error("Failed to fetch route geometry", error);
    }

    // Fallback to straight lines
    return validStops.map((s) => [s.station.latitude!, s.station.longitude!]);
};
