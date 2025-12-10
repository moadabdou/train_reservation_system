import React from "react";
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap, ZoomControl } from "react-leaflet";
import L from "leaflet";
import { RouteStopDTO, TrainPositionDTO } from "../types";
import { getStationInfo } from "../data/stationInfo";
import "./MapComponent.css";

// Fix for default marker icon
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";

let DefaultIcon = L.icon({
    iconUrl: icon,
    shadowUrl: iconShadow,
    iconSize: [25, 41],
    iconAnchor: [12, 41],
});

L.Marker.prototype.options.icon = DefaultIcon;

const pulsingIcon = L.divIcon({
    className: "pulsing-icon",
    html: '<div class="pulse"></div>',
    iconSize: [20, 20],
    iconAnchor: [10, 10],
});

interface MapComponentProps {
    routeStops: RouteStopDTO[];
    trainPosition: TrainPositionDTO | null;
    routePath?: [number, number][];
    onStationSelect?: (stationName: string) => void;
}

function ChangeView({ center, zoom }: { center: [number, number]; zoom: number }) {
    const map = useMap();
    map.setView(center, zoom);
    return null;
}

const MapComponent: React.FC<MapComponentProps> = ({ routeStops, trainPosition, routePath, onStationSelect }) => {
    // Use provided routePath if available, otherwise fallback to internal logic (or empty)
    // Ideally, we should remove the internal fetching logic if routePath is always provided.
    // For now, let's prioritize routePath.

    const positionsToRender = routePath && routePath.length > 0 ? routePath : [];

    const center: [number, number] = positionsToRender.length > 0 ? positionsToRender[0] : [33.5731, -7.5898]; // Default to Casablanca

    return (
        <MapContainer
            center={center}
            zoom={6}
            style={{
                height: "500px",
                width: "100%",
                borderRadius: "12px",
                overflow: "hidden",
                boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
            }}
            zoomControl={false}
        >
            <ZoomControl position="bottomright" />
            <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            />
            {positionsToRender.length > 0 && (
                <>
                    <Polyline
                        positions={positionsToRender}
                        pathOptions={{ color: "#0056b3", weight: 6, opacity: 0.8 }}
                    />
                    <Polyline
                        positions={positionsToRender}
                        pathOptions={{ color: "white", weight: 2, dashArray: "10, 10", opacity: 0.6 }}
                    />
                </>
            )}
            {routeStops.map((stop) => {
                if (stop.station.latitude == null || stop.station.longitude == null) return null;
                const info = getStationInfo(stop.station.name);

                return (
                    <Marker
                        key={stop.id}
                        position={[stop.station.latitude, stop.station.longitude]}
                        eventHandlers={{
                            click: () => {
                                if (onStationSelect) onStationSelect(stop.station.name);
                            },
                        }}
                    >
                        <Popup className="station-popup">
                            <div className="station-popup-content">
                                <h3>{stop.station.name}</h3>
                                {info.imageUrl && (
                                    <img src={info.imageUrl} alt={stop.station.name} className="station-popup-image" />
                                )}
                                <p className="station-description">{stop.station.description}</p>
                                {info.funFact && (
                                    <div className="station-fun-fact">
                                        <strong>Did you know?</strong>
                                        <p>{info.funFact}</p>
                                    </div>
                                )}
                            </div>
                        </Popup>
                    </Marker>
                );
            })}
            {trainPosition &&
                typeof trainPosition.latitude === "number" &&
                typeof trainPosition.longitude === "number" && (
                    <Marker position={[trainPosition.latitude, trainPosition.longitude]} icon={pulsingIcon}>
                        <Popup>
                            Current Train Position
                            <br />
                            Status: {trainPosition.status}
                            <br />
                            Next Station: {trainPosition.nextStationName}
                        </Popup>
                    </Marker>
                )}
            {trainPosition &&
                typeof trainPosition.latitude === "number" &&
                typeof trainPosition.longitude === "number" && (
                    <ChangeView center={[trainPosition.latitude, trainPosition.longitude]} zoom={8} />
                )}
        </MapContainer>
    );
};

export default MapComponent;
