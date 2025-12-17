import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import { adminService, DashboardStats, AdminTrainPosition } from "../../services/adminService";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import "./AdminDashboard.css";

// Fix for default marker icon
import icon from "leaflet/dist/images/marker-icon.png";
import iconShadow from "leaflet/dist/images/marker-shadow.png";

// Custom icons for different statuses
const createIcon = (color: string) =>
    new L.Icon({
        iconUrl: `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-${color}.png`,
        shadowUrl: "https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png",
        iconSize: [25, 41],
        iconAnchor: [12, 41],
        popupAnchor: [1, -34],
        shadowSize: [41, 41],
    });

const icons = {
    MOVING: createIcon("green"),
    AT_STATION: createIcon("blue"),
    NOT_STARTED: createIcon("grey"),
    ARRIVED: createIcon("black"),
    default: createIcon("blue"),
};

const AdminDashboard: React.FC = () => {
    const [stats, setStats] = useState<DashboardStats | null>(null);
    const [trains, setTrains] = useState<AdminTrainPosition[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadData();
        const interval = setInterval(loadData, 30000); // Refresh every 30s
        return () => clearInterval(interval);
    }, []);

    const loadData = async () => {
        try {
            const [statsData, trainsData] = await Promise.all([
                adminService.getDashboardStats(),
                adminService.getLiveMap(),
            ]);
            setStats(statsData);
            setTrains(trainsData);
        } catch (error) {
            console.error("Failed to load dashboard data", error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>Loading dashboard...</div>;

    return (
        <div className="admin-dashboard">
            <h2>System Overview</h2>

            {stats && (
                <div className="dashboard-stats">
                    <div className="stat-card">
                        <h3>Total Users</h3>
                        <p className="value">{stats.totalUsers}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Active Trains</h3>
                        <p className="value">{stats.activeTrains}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Bookings Today</h3>
                        <p className="value">{stats.bookingsToday}</p>
                    </div>
                    <div className="stat-card">
                        <h3>Revenue Today</h3>
                        <p className="value">{stats.revenueToday ? stats.revenueToday.toFixed(2) : "0.00"} dh</p>
                    </div>
                    <div className="stat-card">
                        <h3>Occupancy Rate</h3>
                        <p className="value">{(stats.occupancyRate * 100).toFixed(1)}%</p>
                    </div>
                    <div className="stat-card">
                        <h3>Total Revenue</h3>
                        <p className="value">{stats.totalRevenue ? stats.totalRevenue.toFixed(2) : "0.00"} dh</p>
                    </div>
                    <div className="stat-card">
                        <h3>Cancellation Rate</h3>
                        <p className="value">{(stats.cancellationRate * 100).toFixed(1)}%</p>
                    </div>
                    <div className="stat-card">
                        <h3>Top Route</h3>
                        <p className="value" style={{ fontSize: "1rem" }}>
                            {stats.topRoute || "N/A"}
                        </p>
                    </div>
                </div>
            )}

            <div className="dashboard-sections">
                <div className="live-map-section">
                    <h3>Live Train Map (Today's Schedule)</h3>
                    <div className="map-legend">
                        <span>
                            <span className="dot green"></span> Moving
                        </span>
                        <span>
                            <span className="dot blue"></span> At Station
                        </span>
                        <span>
                            <span className="dot grey"></span> Not Started
                        </span>
                        <span>
                            <span className="dot black"></span> Arrived
                        </span>
                    </div>
                    <MapContainer center={[34.0, -6.8]} zoom={6} style={{ height: "400px", width: "100%" }}>
                        <TileLayer
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                        />
                        {trains.map((train) => (
                            <Marker
                                key={train.trainId}
                                position={[train.latitude, train.longitude]}
                                icon={icons[train.status as keyof typeof icons] || icons.default}
                            >
                                <Popup>
                                    <strong>{train.trainName}</strong>
                                    <br />
                                    Route: {train.routeName}
                                    <br />
                                    Status: {train.status}
                                    <br />
                                    Next Stop: {train.nextStationName}
                                </Popup>
                            </Marker>
                        ))}
                    </MapContainer>
                </div>

                <div className="recent-activity-section">
                    <h3>Upcoming Departures</h3>
                    {stats?.upcomingDepartures && stats.upcomingDepartures.length > 0 ? (
                        <table className="recent-bookings-table" style={{ marginBottom: "20px" }}>
                            <thead>
                                <tr>
                                    <th>Train</th>
                                    <th>Route</th>
                                    <th>Time</th>
                                    <th>Seats</th>
                                </tr>
                            </thead>
                            <tbody>
                                {stats.upcomingDepartures.map((schedule) => (
                                    <tr key={schedule.id}>
                                        <td>{schedule.trainName}</td>
                                        <td>
                                            {schedule.departureStationName} → {schedule.arrivalStationName}
                                        </td>
                                        <td>
                                            {new Date(schedule.departureTime).toLocaleTimeString([], {
                                                hour: "2-digit",
                                                minute: "2-digit",
                                            })}
                                        </td>
                                        <td>{schedule.availableSeats}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p>No upcoming departures.</p>
                    )}

                    <h3>Recent Bookings</h3>
                    {stats?.recentBookings && stats.recentBookings.length > 0 ? (
                        <table className="recent-bookings-table">
                            <thead>
                                <tr>
                                    <th>Ref</th>
                                    <th>Date</th>
                                    <th>Status</th>
                                    <th>Amount</th>
                                </tr>
                            </thead>
                            <tbody>
                                {stats.recentBookings.map((booking) => (
                                    <tr key={booking.bookingId}>
                                        <td>{booking.referenceCode}</td>
                                        <td>{new Date(booking.bookingDate).toLocaleDateString()}</td>
                                        <td>
                                            <span className={`status-badge ${booking.status.toLowerCase()}`}>
                                                {booking.status}
                                            </span>
                                        </td>
                                        <td>{booking.totalPrice.toFixed(2)} dh</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p>No recent bookings found.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default AdminDashboard;
