import { getJSON } from "api/client";

export type Schedule = {
  id: number;
  trainName: string;
  departureStationName: string;
  arrivalStationName: string;
  departureTime: string; // ISO
  arrivalTime: string;   // ISO
  price: number;
  availableSeats: number;
};

export async function getSchedules(params: { from: string | number; to: string | number; date: string; }): Promise<Schedule[]> {
  const q = new URLSearchParams({
    from: String(params.from),
    to: String(params.to),
    date: params.date,
  }).toString();
  return getJSON<Schedule[]>(`/api/schedules?${q}`);
}
