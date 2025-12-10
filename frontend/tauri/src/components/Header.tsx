import React from "react";
import { Link } from "react-router-dom";
import { Bus, HelpCircle, User, LogOut, Ticket, Award } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import "./Header.css";

const Header: React.FC = () => {
    const { isLoggedIn, user, logout } = useAuth();

    return (
        <header className="app-header">
            <div className="container header-content">
                <Link to="/" className="logo" style={{ textDecoration: "none", color: "inherit" }}>
                    <Bus size={32} className="logo-icon" />
                    <span className="logo-text">MarkoubClone</span>
                </Link>
                <nav className="nav-links">
                    <a href="#" className="nav-link">
                        <HelpCircle size={18} />
                        <span>Aide</span>
                    </a>
                    {isLoggedIn ? (
                        <>
                            <Link to="/my-bookings" className="nav-link">
                                <Ticket size={18} />
                                <span>Mes Réservations</span>
                            </Link>
                            <Link to="/loyalty" className="nav-link">
                                <Award size={18} />
                                <span>Loyalty</span>
                            </Link>
                            <span className="nav-link user-name">
                                <User size={18} />
                                <span>{user?.name}</span>
                            </span>
                            <button onClick={logout} className="nav-link logout-btn">
                                <LogOut size={18} />
                                <span>Déconnexion</span>
                            </button>
                        </>
                    ) : (
                        <Link to="/login" className="nav-link login-btn">
                            <User size={18} />
                            <span>Se connecter</span>
                        </Link>
                    )}
                </nav>
            </div>
        </header>
    );
};

export default Header;
