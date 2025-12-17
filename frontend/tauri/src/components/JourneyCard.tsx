import React from "react";
import { ScheduleDTO } from "../types";
import { Train, Star } from "lucide-react";
import { format, parseISO, differenceInMinutes } from "date-fns";
import "./JourneyCard.css";

interface JourneyCardProps {
    schedule: ScheduleDTO;
    onSelect: (schedule: ScheduleDTO) => void;
}

const JourneyCard: React.FC<JourneyCardProps> = ({ schedule, onSelect }) => {
    const depTime = parseISO(schedule.departureTime);
    const arrTime = parseISO(schedule.arrivalTime);
    const durationMinutes = differenceInMinutes(arrTime, depTime);
    const hours = Math.floor(durationMinutes / 60);
    const minutes = durationMinutes % 60;

    return (
        <div className="journey-card">
            <div className="company-info">
                <div className="company-logo-placeholder">
                    <Train size={24} color="var(--color-primary)" />
                </div>
                <div className="company-details">
                    <span className="company-name">ONCF</span>
                    <div className="rating">
                        <Star size={12} fill="#FFC107" stroke="none" />
                        <span>4.5</span>
                    </div>
                </div>
            </div>

            <div className="journey-timeline">
                <div className="time-point">
                    <span className="time">{format(depTime, "HH:mm")}</span>
                    <span className="station">{schedule.departureStationName}</span>
                </div>

                <div className="journey-duration">
                    <span className="duration-text">
                        {hours}h {minutes}m
                    </span>
                    <div className="duration-line"></div>
                </div>

                <div className="time-point">
                    <span className="time">{format(arrTime, "HH:mm")}</span>
                    <span className="station">{schedule.arrivalStationName}</span>
                </div>
            </div>

            <div className="journey-action">
                <div className="price-tag">
                    <span className="amount">{schedule.price.toFixed(0)}</span>
                    <span className="currency">dh</span>
                </div>
                <button className="select-btn" onClick={() => onSelect(schedule)}>
                    Sélectionner
                </button>
            </div>
        </div>
    );
};

export default JourneyCard;
