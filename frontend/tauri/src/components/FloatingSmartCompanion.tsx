import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import SmartCompanion from "./SmartCompanion";
import "./FloatingSmartCompanion.css";

const FloatingSmartCompanion: React.FC = () => {
    const [isOpen, setIsOpen] = useState(false);
    const location = useLocation();

    // Hide on journey pages where it's already embedded
    if (location.pathname.startsWith("/journey/")) {
        return null;
    }

    return (
        <div className="floating-companion-wrapper">
            <div className="floating-popup" style={{ display: isOpen ? "flex" : "none" }}>
                <SmartCompanion destination="Morocco" />
            </div>
            <button
                className="floating-toggle-btn"
                onClick={() => setIsOpen(!isOpen)}
                title={isOpen ? "Close Companion" : "Open Smart Companion"}
            >
                {isOpen ? "✕" : "🤖"}
            </button>
        </div>
    );
};

export default FloatingSmartCompanion;
