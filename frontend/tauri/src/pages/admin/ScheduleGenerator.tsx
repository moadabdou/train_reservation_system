import React, { useState, useEffect } from "react";
import { adminService, Route, Train } from "../../services/adminService";
import { Calendar, DollarSign, Train as TrainIcon, Map } from "lucide-react";
import "./AdminLayout.css"; // Reusing admin layout styles

const ScheduleGenerator: React.FC = () => {
    const [routes, setRoutes] = useState<Route[]>([]);
    const [trains, setTrains] = useState<Train[]>([]);

    const [selectedRouteId, setSelectedRouteId] = useState<number | "">("");
    const [selectedTrainId, setSelectedTrainId] = useState<number | "">("");
    const [startTime, setStartTime] = useState<string>("");
    const [basePrice, setBasePrice] = useState<number | "">("");

    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState<{ type: "success" | "error"; text: string } | null>(null);

    // Helper to get selected route details
    const getSelectedRouteDetails = () => {
        if (!selectedRouteId) return null;
        return routes.find((r) => r.id === selectedRouteId);
    };

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const [routesData, trainsData] = await Promise.all([
                adminService.getAllRoutes(),
                adminService.getAllTrains(),
            ]);
            setRoutes(routesData);
            setTrains(trainsData);
        } catch (error) {
            console.error("Failed to load data", error);
            setMessage({ type: "error", text: "Failed to load routes and trains." });
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedRouteId || !selectedTrainId || !startTime || !basePrice) {
            setMessage({ type: "error", text: "Please fill in all fields." });
            return;
        }

        setLoading(true);
        setMessage(null);

        try {
            await adminService.generateSchedule({
                routeId: Number(selectedRouteId),
                trainId: Number(selectedTrainId),
                startTime: new Date(startTime).toISOString(),
                basePrice: Number(basePrice),
                includeIntermediateStops: true, // Always include defined stops
            });
            setMessage({ type: "success", text: "Schedule generated successfully!" });
            // Optional: Reset form
        } catch (error) {
            console.error("Generation failed", error);
            setMessage({ type: "error", text: "Failed to generate schedule. Check console for details." });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="admin-page">
            <div className="admin-header">
                <h1>Schedule Generator</h1>
                <p>Automatically generate train schedules based on defined routes.</p>
            </div>

            <div className="card">
                <form onSubmit={handleSubmit} className="admin-form">
                    <div className="form-group">
                        <label>
                            <Map size={18} /> Select Route
                        </label>
                        <select
                            value={selectedRouteId}
                            onChange={(e) => setSelectedRouteId(Number(e.target.value))}
                            required
                        >
                            <option value="">-- Choose a Route --</option>
                            {routes.map((route) => (
                                <option key={route.id} value={route.id}>
                                    {route.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>
                            <TrainIcon size={18} /> Select Train
                        </label>
                        <select
                            value={selectedTrainId}
                            onChange={(e) => setSelectedTrainId(Number(e.target.value))}
                            required
                        >
                            <option value="">-- Choose a Train --</option>
                            {trains.map((train) => (
                                <option key={train.id} value={train.id}>
                                    {train.name} ({train.providerName})
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label>
                            <Calendar size={18} /> Start Date & Time
                        </label>
                        <input
                            type="datetime-local"
                            value={startTime}
                            onChange={(e) => setStartTime(e.target.value)}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>
                            <DollarSign size={18} /> Base Price (MAD)
                        </label>
                        <input
                            type="number"
                            min="0"
                            step="0.01"
                            value={basePrice}
                            onChange={(e) => setBasePrice(Number(e.target.value))}
                            placeholder="e.g. 150.00"
                            required
                        />
                    </div>

                    {/* Route Info Message */}
                    {selectedRouteId && (
                        <div
                            className="info-message"
                            style={{
                                padding: "10px",
                                backgroundColor: "#e8f4fd",
                                border: "1px solid #b6e0fe",
                                borderRadius: "4px",
                                marginBottom: "15px",
                                color: "#0c5460",
                                fontSize: "0.9rem",
                            }}
                        >
                            {(() => {
                                const route = getSelectedRouteDetails();
                                if (!route) return null;
                                const stopCount = route.definitions ? route.definitions.length : 0;
                                const intermediateCount = Math.max(0, stopCount - 2);

                                if (intermediateCount > 0) {
                                    return (
                                        <>
                                            <strong>Note:</strong> This route includes{" "}
                                            <strong>{intermediateCount} intermediate stops</strong>. The schedule will
                                            automatically include stops at:{" "}
                                            {route.definitions
                                                ?.slice(1, -1)
                                                .map((d) => d.stationName)
                                                .join(", ")}
                                            .
                                        </>
                                    );
                                } else {
                                    return (
                                        <>
                                            <strong>Note:</strong> This is a <strong>Direct Route</strong> (Start to End
                                            only). No intermediate stops will be generated.
                                        </>
                                    );
                                }
                            })()}
                        </div>
                    )}

                    {message && <div className={`message ${message.type}`}>{message.text}</div>}

                    <button type="submit" className="btn-primary" disabled={loading}>
                        {loading ? "Generating..." : "Generate Schedule"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ScheduleGenerator;
