import React, { useState } from "react";
import { getStationInfo } from "../data/stationInfo";
import "./StationGallery.css";

interface StationGalleryProps {
    stationName: string | null;
}

const StationGallery: React.FC<StationGalleryProps> = ({ stationName }) => {
    const [selectedImageIndex, setSelectedImageIndex] = useState(0);

    if (!stationName) {
        return (
            <div className="station-gallery-placeholder">
                <p>Select a station from the map or timeline to view details.</p>
            </div>
        );
    }

    const info = getStationInfo(stationName);
    const images = info.galleryImages || (info.imageUrl ? [info.imageUrl] : []);

    return (
        <div className="station-gallery-container">
            <div className="gallery-header">
                <h2>{stationName}</h2>
                <span className="gallery-subtitle">Station Details</span>
            </div>

            <div className="gallery-content">
                <div className="gallery-main-image-container">
                    {images.length > 0 ? (
                        <img
                            src={images[selectedImageIndex]}
                            alt={`${stationName} view`}
                            className="gallery-main-image"
                        />
                    ) : (
                        <div className="no-image">No Image Available</div>
                    )}
                </div>

                <div className="gallery-info-panel">
                    <div className="gallery-thumbnails">
                        {images.map((img, index) => (
                            <img
                                key={index}
                                src={img}
                                alt={`Thumbnail ${index}`}
                                className={`gallery-thumbnail ${index === selectedImageIndex ? "active" : ""}`}
                                onClick={() => setSelectedImageIndex(index)}
                            />
                        ))}
                    </div>

                    <div className="gallery-text-content">
                        {info.longDescription && (
                            <div className="gallery-section">
                                <h3>About {stationName}</h3>
                                <p>{info.longDescription}</p>
                            </div>
                        )}

                        {info.funFact && (
                            <div className="gallery-section fun-fact-section">
                                <h3>Did You Know?</h3>
                                <p>{info.funFact}</p>
                            </div>
                        )}

                        {info.attractions && info.attractions.length > 0 && (
                            <div className="gallery-section">
                                <h3>Nearby Attractions</h3>
                                <ul className="attractions-list">
                                    {info.attractions.map((attraction, idx) => (
                                        <li key={idx}>{attraction}</li>
                                    ))}
                                </ul>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default StationGallery;
