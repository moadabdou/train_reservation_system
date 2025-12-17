import React from "react";
import { Link, Outlet, useLocation } from "react-router-dom";
import Header from "../../components/Header";
import "./AdminLayout.css";

const AdminLayout: React.FC = () => {
    const location = useLocation();

    return (
        <>
            <Header variant="admin" />
            <div className="admin-container">
                <aside className="admin-sidebar">
                    <h2>Admin Panel</h2>
                    <nav>
                        <ul>
                            <li
                                className={
                                    location.pathname === "/admin/dashboard" || location.pathname === "/admin"
                                        ? "active"
                                        : ""
                                }
                            >
                                <Link to="/admin/dashboard">Dashboard</Link>
                            </li>
                            <li className={location.pathname === "/admin/providers" ? "active" : ""}>
                                <Link to="/admin/providers">Providers</Link>
                            </li>
                            <li className={location.pathname === "/admin/trains" ? "active" : ""}>
                                <Link to="/admin/trains">Trains</Link>
                            </li>
                            <li className={location.pathname === "/admin/stations" ? "active" : ""}>
                                <Link to="/admin/stations">Stations</Link>
                            </li>
                            <li className={location.pathname === "/admin/routes" ? "active" : ""}>
                                <Link to="/admin/routes">Routes</Link>
                            </li>
                            <li className={location.pathname === "/admin/schedules" ? "active" : ""}>
                                <Link to="/admin/schedules">Schedules</Link>
                            </li>
                            <li className={location.pathname === "/admin/schedules/generate" ? "active" : ""}>
                                <Link to="/admin/schedules/generate">Generate Schedule</Link>
                            </li>
                            <li className={location.pathname === "/admin/users" ? "active" : ""}>
                                <Link to="/admin/users">Users</Link>
                            </li>
                            <li className={location.pathname === "/admin/bookings" ? "active" : ""}>
                                <Link to="/admin/bookings">Bookings</Link>
                            </li>
                            <li className={location.pathname === "/admin/financials" ? "active" : ""}>
                                <Link to="/admin/financials">Financials</Link>
                            </li>
                            <li className={location.pathname === "/admin/payments" ? "active" : ""}>
                                <Link to="/admin/payments">Payment Validation</Link>
                            </li>
                            <li className={location.pathname === "/admin/loyalty" ? "active" : ""}>
                                <Link to="/admin/loyalty">Loyalty & Rewards</Link>
                            </li>
                            <li className={location.pathname === "/admin/content" ? "active" : ""}>
                                <Link to="/admin/content">Content Management</Link>
                            </li>
                            <li className={location.pathname === "/admin/pricing" ? "active" : ""}>
                                <Link to="/admin/pricing">Pricing & Layouts</Link>
                            </li>
                        </ul>
                    </nav>
                </aside>
                <main className="admin-content">
                    <Outlet />
                </main>
            </div>
        </>
    );
};

export default AdminLayout;
