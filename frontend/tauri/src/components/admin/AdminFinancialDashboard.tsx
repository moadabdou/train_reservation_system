import React, { useEffect, useState } from "react";
import { adminService, FinancialStats } from "../../services/adminService";
import "./AdminFinancialDashboard.css";

const AdminFinancialDashboard: React.FC = () => {
    const [stats, setStats] = useState<FinancialStats | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadStats();
    }, []);

    const loadStats = async () => {
        try {
            const data = await adminService.getFinancialStats();
            setStats(data);
        } catch (error) {
            console.error("Failed to load financial stats", error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>Loading stats...</div>;
    if (!stats) return <div>No stats available.</div>;

    return (
        <div className="financial-dashboard">
            <h2>Financial Dashboard</h2>
            <div className="stats-cards">
                <div className="card">
                    <h3>Total Revenue</h3>
                    <p className="amount">{stats.totalRevenue ? stats.totalRevenue.toFixed(2) : "0.00"} dh</p>
                </div>
                <div className="card">
                    <h3>Total Refunds</h3>
                    <p className="amount">{stats.totalRefunds ? stats.totalRefunds.toFixed(2) : "0.00"} dh</p>
                </div>
                <div className="card">
                    <h3>Net Income</h3>
                    <p className="amount">{((stats.totalRevenue || 0) - (stats.totalRefunds || 0)).toFixed(2)} dh</p>
                </div>
            </div>

            <div className="revenue-by-route">
                <h3>Revenue by Route</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Route</th>
                            <th>Revenue</th>
                        </tr>
                    </thead>
                    <tbody>
                        {Object.entries(stats.revenueByRoute).map(([route, revenue]) => (
                            <tr key={route}>
                                <td>{route}</td>
                                <td>{revenue.toFixed(2)} dh</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminFinancialDashboard;
