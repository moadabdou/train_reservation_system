import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getRouteStops, getTrainPosition, getRoutePath } from "../services/journeyService";
import { RouteStopDTO, TrainPositionDTO } from "../types";
import MapComponent from "./MapComponent";
import Timeline from "./Timeline";
import StationGallery from "./StationGallery";
import SmartCompanion from "./SmartCompanion";
import "./JourneyTracking.css";

const JourneyTracking: React.FC = () => {
    const { scheduleId } = useParams<{ scheduleId: string }>();
    const [routeStops, setRouteStops] = useState<RouteStopDTO[]>([]);
    const [routePath, setRoutePath] = useState<[number, number][]>([]);
    const [trainPosition, setTrainPosition] = useState<TrainPositionDTO | null>(null);
    const [selectedStation, setSelectedStation] = useState<string | null>(null);
    const [activeTab, setActiveTab] = useState<"gallery" | "companion">("gallery");
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const [showDebug, setShowDebug] = useState<boolean>(false);
    const [isDemoMode, setIsDemoMode] = useState<boolean>(false);

    useEffect(() => {
        const fetchData = async () => {
            if (!scheduleId) return;
            try {
                const stops = await getRouteStops(Number(scheduleId));
                setRouteStops(stops);

                // Fetch the detailed path
                const path = await getRoutePath(stops);
                setRoutePath(path);

                // Initialize position at start station if not yet set
                if (stops.length > 0) {
                    const start = stops[0].station;
                    if (start.latitude && start.longitude) {
                        setTrainPosition((prev) => {
                            if (!prev) {
                                return {
                                    latitude: start.latitude!,
                                    longitude: start.longitude!,
                                    status: "NOT_STARTED",
                                    nextStationName: stops.length > 1 ? stops[1].station.name : "End",
                                    nextStationId: stops.length > 1 ? stops[1].station.id : 0,
                                    estimatedArrivalMinutes: 0,
                                };
                            }
                            return prev;
                        });
                    }
                }

                setLoading(false);
            } catch (err) {
                console.error("Failed to fetch route stops", err);
                setError("Failed to load journey details.");
                setLoading(false);
            }
        };

        fetchData();
    }, [scheduleId]);

    useEffect(() => {
        if (!scheduleId || isDemoMode) return;

        const fetchPosition = async () => {
            try {
                const position = await getTrainPosition(Number(scheduleId));
                setTrainPosition(position);

                // Check for notifications
                if (Notification.permission === "granted") {
                    if (position.status === "ARRIVED") {
                        new Notification("Journey Update", { body: "The train has arrived at the destination!" });
                    }
                }
            } catch (err) {
                console.error("Failed to fetch train position", err);
            }
        };

        // Initial fetch
        fetchPosition();

        // Poll every 10 seconds
        const intervalId = setInterval(fetchPosition, 10000);

        return () => clearInterval(intervalId);
    }, [scheduleId, isDemoMode]);

    // Demo Mode Logic
    useEffect(() => {
        if (!isDemoMode || routePath.length < 2) return;

        // Reset to start immediately
        const [startLat, startLng] = routePath[0];
        setTrainPosition({
            latitude: startLat,
            longitude: startLng,
            status: "MOVING",
            nextStationName: "Start",
            nextStationId: 0,
            estimatedArrivalMinutes: 0,
        });

        let progress = 0;
        const interval = setInterval(() => {
            progress += 0.0005; // Slower speed for detailed path
            if (progress > 1) progress = 0;

            // Interpolate along the detailed path
            const totalPoints = routePath.length - 1;
            const currentPointIndex = Math.floor(progress * totalPoints);
            const nextPointIndex = Math.min(currentPointIndex + 1, totalPoints);

            const segmentProgress = progress * totalPoints - currentPointIndex;

            const [lat1, lng1] = routePath[currentPointIndex];
            const [lat2, lng2] = routePath[nextPointIndex];

            const lat = lat1 + (lat2 - lat1) * segmentProgress;
            const lng = lng1 + (lng2 - lng1) * segmentProgress;

            setTrainPosition({
                latitude: lat,
                longitude: lng,
                status: "MOVING",
                nextStationName: "Next Station", // Simplified for demo
                nextStationId: 0,
                estimatedArrivalMinutes: Math.floor((1 - progress) * 60),
            });
        }, 50);

        return () => clearInterval(interval);
    }, [isDemoMode, routePath]);

    useEffect(() => {
        // Request notification permission
        if (Notification.permission !== "granted") {
            Notification.requestPermission();
        }
    }, []);

    if (loading) return <div>Loading journey details...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div className="journey-tracking-container">
            <div className="journey-header">
                <h2>Journey Tracking</h2>
                <div>
                    <button onClick={() => setIsDemoMode(!isDemoMode)} className="debug-toggle">
                        {isDemoMode ? "Stop Demo" : "Start Demo"}
                    </button>
                    <button onClick={() => setShowDebug(!showDebug)} className="debug-toggle">
                        {showDebug ? "Hide Debug" : "Show Debug"}
                    </button>
                </div>
            </div>

            {showDebug && (
                <div className="debug-panel">
                    <h4>Debug Info</h4>
                    <pre>
                        {JSON.stringify(
                            { trainPosition, routeStopsCount: routeStops.length, pathPoints: routePath.length },
                            null,
                            2
                        )}
                    </pre>
                </div>
            )}

            <div className="journey-content">
                <div className="map-section">
                    <MapComponent
                        routeStops={routeStops}
                        trainPosition={trainPosition}
                        routePath={routePath}
                        onStationSelect={(station) => {
                            setSelectedStation(station);
                            setActiveTab("gallery");
                        }}
                    />
                </div>
                <div className="timeline-section">
                    <Timeline
                        routeStops={routeStops}
                        trainPosition={trainPosition}
                        onStationSelect={(station) => {
                            setSelectedStation(station);
                            setActiveTab("gallery");
                        }}
                    />
                </div>
            </div>

            <div className="bottom-section">
                <div className="tabs">
                    <button
                        className={`tab-button ${activeTab === "gallery" ? "active" : ""}`}
                        onClick={() => setActiveTab("gallery")}
                    >
                        Station Details
                    </button>
                    <button
                        className={`tab-button ${activeTab === "companion" ? "active" : ""}`}
                        onClick={() => setActiveTab("companion")}
                    >
                        🤖 Smart Companion
                    </button>
                </div>

                <div className="tab-content">
                    {activeTab === "gallery" ? (
                        <StationGallery stationName={selectedStation} />
                    ) : (
                        <SmartCompanion
                            destination={
                                routeStops.length > 0 ? routeStops[routeStops.length - 1].station.name : "Morocco"
                            }
                            currentStation={trainPosition?.nextStationName}
                        />
                    )}
                </div>
            </div>
        </div>
    );
};
export default JourneyTracking;
