import React from "react";
import { RouteStopDTO, TrainPositionDTO } from "../types";
import { getStationInfo } from "../data/stationInfo";
import "./Timeline.css";

interface TimelineProps {
    routeStops: RouteStopDTO[];
    trainPosition: TrainPositionDTO | null;
    onStationSelect?: (stationName: string) => void;
}

const Timeline: React.FC<TimelineProps> = ({ routeStops, trainPosition, onStationSelect }) => {
    const sortedStops = [...routeStops].sort((a, b) => a.stopOrder - b.stopOrder);

    // Determine current stop index based on nextStationId
    const nextStationIndex = trainPosition
        ? sortedStops.findIndex((s) => s.station.id === trainPosition.nextStationId)
        : -1;

    return (
        <div className="timeline-container">
            <h3>Journey Timeline</h3>
            <ul className="timeline">
                {sortedStops.map((stop, index) => {
                    const isPassed = nextStationIndex !== -1 && index < nextStationIndex;
                    const isNext = nextStationIndex !== -1 && index === nextStationIndex;
                    const info = getStationInfo(stop.station.name);

                    return (
                        <li
                            key={stop.id}
                            className={`timeline-item ${isPassed ? "passed" : ""} ${isNext ? "next" : ""}`}
                            onClick={() => onStationSelect && onStationSelect(stop.station.name)}
                            style={{ cursor: "pointer" }}
                        >
                            <div className="timeline-marker"></div>
                            <div className="timeline-content">
                                <div className="timeline-header">
                                    <h4>{stop.station.name}</h4>
                                    {info.funFact && (
                                        <span className="info-icon" title={info.funFact}>
                                            ℹ️
                                        </span>
                                    )}
                                </div>
                                <p>
                                    Arr:{" "}
                                    {new Date(stop.arrivalTime).toLocaleTimeString([], {
                                        hour: "2-digit",
                                        minute: "2-digit",
                                    })}
                                </p>
                                <p>
                                    Dep:{" "}
                                    {new Date(stop.departureTime).toLocaleTimeString([], {
                                        hour: "2-digit",
                                        minute: "2-digit",
                                    })}
                                </p>
                                {isNext && <span className="badge">Next Stop</span>}
                            </div>
                        </li>
                    );
                })}
            </ul>
        </div>
    );
};

export default Timeline;
