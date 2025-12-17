import React, { useState, useEffect } from "react";
import { adminService, Route, Station, RouteDefinition } from "../../services/adminService";
import "./AdminLayout.css";

const Routes: React.FC = () => {
    const [routes, setRoutes] = useState<Route[]>([]);
    const [stations, setStations] = useState<Station[]>([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);

    // Form State
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");

    // New Route Builder State
    const [startStationId, setStartStationId] = useState<number | "">("");

    const [arrivalStationId, setArrivalStationId] = useState<number | "">("");
    const [arrivalDistance, setArrivalDistance] = useState<number>(0);
    const [arrivalTime, setArrivalTime] = useState<number>(0);

    const [intermediateStops, setIntermediateStops] = useState<
        Array<{
            stationId: number;
            stationName: string;
            distance: number;
            time: number;
        }>
    >([]);

    // Intermediate Stop Input State
    const [tempStationId, setTempStationId] = useState<number | "">("");
    const [tempDistance, setTempDistance] = useState<number>(0);
    const [tempTime, setTempTime] = useState<number>(0);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            // Fetch stations first as they are independent
            try {
                const stationsData = await adminService.getAllStations();
                setStations(stationsData);
            } catch (error) {
                console.error("Error fetching stations:", error);
            }

            // Then fetch routes
            try {
                const routesData = await adminService.getAllRoutes();
                setRoutes(routesData);
            } catch (error) {
                console.error("Error fetching routes:", error);
            }
        } catch (error) {
            console.error("Error in fetchData:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleAddIntermediate = () => {
        if (tempStationId === "") return;

        const station = stations.find((s) => s.id === Number(tempStationId));
        if (!station) return;

        const newStop = {
            stationId: station.id!,
            stationName: station.name,
            distance: tempDistance,
            time: tempTime,
        };

        setIntermediateStops([...intermediateStops, newStop]);

        // Reset intermediate inputs
        setTempStationId("");
        setTempDistance(0);
        setTempTime(0);
    };

    const handleRemoveIntermediate = (index: number) => {
        const newStops = intermediateStops.filter((_, i) => i !== index);
        setIntermediateStops(newStops);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!startStationId || !arrivalStationId) {
            alert("Please select both Start and Arrival stations.");
            return;
        }

        const startStation = stations.find((s) => s.id === Number(startStationId));
        const arrivalStation = stations.find((s) => s.id === Number(arrivalStationId));

        if (!startStation || !arrivalStation) return;

        const definitions: RouteDefinition[] = [];

        // 1. Start Station
        definitions.push({
            stationId: startStation.id!,
            stationName: startStation.name,
            stopOrder: 1,
            distanceFromPrevKm: 0,
            standardTravelTimeMins: 0,
        });

        // 2. Intermediate Stops
        intermediateStops.forEach((stop, index) => {
            definitions.push({
                stationId: stop.stationId,
                stationName: stop.stationName,
                stopOrder: index + 2,
                distanceFromPrevKm: stop.distance,
                standardTravelTimeMins: stop.time,
            });
        });

        // 3. Arrival Station
        definitions.push({
            stationId: arrivalStation.id!,
            stationName: arrivalStation.name,
            stopOrder: definitions.length + 1,
            distanceFromPrevKm: arrivalDistance,
            standardTravelTimeMins: arrivalTime,
        });

        try {
            const newRoute: Route = {
                name,
                description,
                definitions,
            };
            await adminService.createRoute(newRoute);
            setShowForm(false);

            // Reset Form
            setName("");
            setDescription("");
            setStartStationId("");
            setArrivalStationId("");
            setArrivalDistance(0);
            setArrivalTime(0);
            setIntermediateStops([]);
            setTempStationId("");
            setTempDistance(0);
            setTempTime(0);

            fetchData();
        } catch (error) {
            console.error("Error creating route:", error);
            alert("Failed to create route");
        }
    };

    const handleDelete = async (id: number) => {
        if (window.confirm("Are you sure you want to delete this route?")) {
            try {
                await adminService.deleteRoute(id);
                fetchData();
            } catch (error) {
                console.error("Error deleting route:", error);
            }
        }
    };

    if (loading) return <div>Loading...</div>;

    return (
        <div className="admin-page">
            <div className="admin-header">
                <h2>Route Management</h2>
                <button className="btn-primary" onClick={() => setShowForm(!showForm)}>
                    {showForm ? "Cancel" : "Add New Route"}
                </button>
            </div>

            {showForm && (
                <div className="admin-form-container">
                    <h3>Create New Route</h3>
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label>Route Name</label>
                            <input type="text" value={name} onChange={(e) => setName(e.target.value)} required />
                        </div>
                        <div className="form-group">
                            <label>Description</label>
                            <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
                        </div>

                        <div className="route-builder">
                            <h4>Route Configuration</h4>

                            {/* 1. Start Station */}
                            <div className="form-group">
                                <label>Start Station</label>
                                <select
                                    value={startStationId}
                                    onChange={(e) => setStartStationId(Number(e.target.value))}
                                    required
                                >
                                    <option value="">Select Start Station</option>
                                    {stations.map((s) => (
                                        <option key={s.id} value={s.id}>
                                            {s.name}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            {/* 2. Intermediate Stops */}
                            <div
                                className="intermediate-stops-section"
                                style={{
                                    margin: "20px 0",
                                    padding: "15px",
                                    border: "1px dashed #ccc",
                                    borderRadius: "8px",
                                }}
                            >
                                <h5>Intermediate Stops (Optional)</h5>

                                <div className="definitions-list">
                                    {intermediateStops.map((stop, index) => (
                                        <div key={index} className="definition-item">
                                            <span>
                                                {index + 1}. {stop.stationName}
                                            </span>
                                            <span>{stop.distance} km</span>
                                            <span>{stop.time} mins</span>
                                            <button
                                                type="button"
                                                onClick={() => handleRemoveIntermediate(index)}
                                                className="btn-danger btn-sm"
                                            >
                                                X
                                            </button>
                                        </div>
                                    ))}
                                </div>

                                <div className="add-definition-form">
                                    <div className="form-group" style={{ flex: 2 }}>
                                        <label>Add Stop</label>
                                        <select
                                            value={tempStationId}
                                            onChange={(e) => setTempStationId(Number(e.target.value))}
                                        >
                                            <option value="">Select Station</option>
                                            {stations.map((s) => (
                                                <option key={s.id} value={s.id}>
                                                    {s.name}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="form-group" style={{ flex: 1 }}>
                                        <label>Dist (km)</label>
                                        <input
                                            type="number"
                                            value={tempDistance}
                                            onChange={(e) => setTempDistance(Number(e.target.value))}
                                        />
                                    </div>
                                    <div className="form-group" style={{ flex: 1 }}>
                                        <label>Time (min)</label>
                                        <input
                                            type="number"
                                            value={tempTime}
                                            onChange={(e) => setTempTime(Number(e.target.value))}
                                        />
                                    </div>
                                    <div className="form-group" style={{ display: "flex", alignItems: "flex-end" }}>
                                        <button
                                            type="button"
                                            onClick={handleAddIntermediate}
                                            className="btn-secondary"
                                            style={{ height: "42px" }}
                                        >
                                            Add
                                        </button>
                                    </div>
                                </div>
                            </div>

                            {/* 3. Arrival Station */}
                            <div
                                className="arrival-station-section"
                                style={{ borderTop: "2px solid #eee", paddingTop: "15px" }}
                            >
                                <h5>Arrival Station</h5>
                                <div className="add-definition-form">
                                    <div className="form-group" style={{ flex: 2 }}>
                                        <label>End Station</label>
                                        <select
                                            value={arrivalStationId}
                                            onChange={(e) => setArrivalStationId(Number(e.target.value))}
                                            required
                                        >
                                            <option value="">Select End Station</option>
                                            {stations.map((s) => (
                                                <option key={s.id} value={s.id}>
                                                    {s.name}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="form-group" style={{ flex: 1 }}>
                                        <label>Dist (km)</label>
                                        <input
                                            type="number"
                                            value={arrivalDistance}
                                            onChange={(e) => setArrivalDistance(Number(e.target.value))}
                                            required
                                        />
                                    </div>
                                    <div className="form-group" style={{ flex: 1 }}>
                                        <label>Time (min)</label>
                                        <input
                                            type="number"
                                            value={arrivalTime}
                                            onChange={(e) => setArrivalTime(Number(e.target.value))}
                                            required
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>

                        <button type="submit" className="btn-success" style={{ marginTop: "20px" }}>
                            Save Route
                        </button>
                    </form>
                </div>
            )}

            <div className="admin-table-container">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Start Station</th>
                            <th>End Station</th>
                            <th>Stops</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {routes.map((route) => (
                            <tr key={route.id}>
                                <td>{route.id}</td>
                                <td>{route.name}</td>
                                <td>{route.definitions?.[0]?.stationName || "-"}</td>
                                <td>
                                    {route.definitions && route.definitions.length > 1
                                        ? route.definitions[route.definitions.length - 1].stationName
                                        : "-"}
                                </td>
                                <td>{route.definitions?.length || 0}</td>
                                <td>
                                    <button onClick={() => handleDelete(route.id!)} className="btn-danger">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Routes;
