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
                            <li className={location.pathname === "/admin/providers" ? "active" : ""}>
                                <Link to="/admin/providers">Providers</Link>
                            </li>
                            <li className={location.pathname === "/admin/trains" ? "active" : ""}>
                                <Link to="/admin/trains">Trains</Link>
                            </li>
                            <li className={location.pathname === "/admin/stations" ? "active" : ""}>
                                <Link to="/admin/stations">Stations</Link>
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
