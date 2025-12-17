import React, { useEffect, useState } from "react";
import { adminService } from "../../services/adminService";
import { ScheduleDTO } from "../../types";
import { useNavigate } from "react-router-dom";
import { Plus, Trash2 } from "lucide-react";
import { format } from "date-fns";
import { overflow } from "html2canvas/dist/types/css/property-descriptors/overflow";

const Schedules: React.FC = () => {
    const [schedules, setSchedules] = useState<ScheduleDTO[]>([]);
    const navigate = useNavigate();

    useEffect(() => {
        loadSchedules();
    }, []);

    const loadSchedules = async () => {
        try {
            const data = await adminService.getAllSchedules();
            setSchedules(data);
        } catch (error) {
            console.error("Failed to load schedules", error);
        }
    };

    const handleDelete = async (id: number) => {
        if (window.confirm("Are you sure you want to delete this schedule?")) {
            try {
                await adminService.deleteSchedule(id);
                loadSchedules();
            } catch (error) {
                console.error("Failed to delete schedule", error);
            }
        }
    };

    return (
        <div className="admin-page" style={{ overflowY: "auto" }}>
            <div className="admin-page-header">
                <h1>Schedules Management</h1>
                <button className="btn-primary" onClick={() => navigate("/admin/schedules/generate")}>
                    <Plus size={18} style={{ marginRight: "8px" }} />
                    Generate Schedule
                </button>
            </div>

            <div className="card">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Train</th>
                            <th>Route</th>
                            <th>Departure</th>
                            <th>Arrival</th>
                            <th>Price</th>
                            <th>Seats</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {schedules.length === 0 ? (
                            <tr>
                                <td colSpan={8} style={{ textAlign: "center", padding: "20px" }}>
                                    No schedules found. Generate one to get started.
                                </td>
                            </tr>
                        ) : (
                            schedules.map((schedule) => (
                                <tr key={schedule.id}>
                                    <td
                                        className="nowrap"
                                        style={{ fontWeight: "bold", color: "var(--color-primary)" }}
                                    >
                                        #{schedule.id}
                                    </td>
                                    <td style={{ fontWeight: 500 }}>{schedule.trainName}</td>
                                    <td>
                                        <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                                            <span style={{ fontWeight: 500 }}>{schedule.departureStationName}</span>
                                            <span style={{ color: "var(--color-text-light)", fontSize: "0.9em" }}>
                                                →
                                            </span>
                                            <span style={{ fontWeight: 500 }}>{schedule.arrivalStationName}</span>
                                        </div>
                                    </td>
                                    <td className="nowrap">
                                        <div style={{ fontWeight: 600 }}>
                                            {format(new Date(schedule.departureTime), "HH:mm")}
                                        </div>
                                        <div style={{ fontSize: "0.85em", color: "var(--color-text-light)" }}>
                                            {format(new Date(schedule.departureTime), "MMM dd, yyyy")}
                                        </div>
                                    </td>
                                    <td className="nowrap">
                                        <div style={{ fontWeight: 600 }}>
                                            {format(new Date(schedule.arrivalTime), "HH:mm")}
                                        </div>
                                        <div style={{ fontSize: "0.85em", color: "var(--color-text-light)" }}>
                                            {format(new Date(schedule.arrivalTime), "MMM dd, yyyy")}
                                        </div>
                                    </td>
                                    <td className="nowrap" style={{ fontWeight: 600 }}>
                                        {schedule.price} dh
                                    </td>
                                    <td className="nowrap">{schedule.availableSeats} seats</td>
                                    <td className="nowrap">
                                        <button
                                            className="action-btn delete-btn"
                                            onClick={() => handleDelete(schedule.id)}
                                            title="Delete Schedule"
                                        >
                                            <Trash2 size={16} />
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
export default Schedules;
