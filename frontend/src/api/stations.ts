import { getJSON } from "api/client";

export type Station = {
  id: number;
  name: string;
};

export async function getStations(): Promise<Station[]> {
  return getJSON<Station[]>("/api/stations");
}
