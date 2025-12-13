import React, { useEffect, useState } from "react";
import { adminService, Station } from "../../services/adminService";

const Stations: React.FC = () => {
    const [stations, setStations] = useState<Station[]>([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentStation, setCurrentStation] = useState<Station>({
        name: "",
        latitude: 0,
        longitude: 0,
        description: "",
        imageUrl: "",
        facilities: "",
    });
    const [isEditing, setIsEditing] = useState(false);

    useEffect(() => {
        loadStations();
    }, []);

    const loadStations = async () => {
        try {
            const data = await adminService.getAllStations();
            setStations(data);
        } catch (error) {
            console.error("Failed to load stations", error);
        }
    };

    const handleOpenModal = (station?: Station) => {
        if (station) {
            setCurrentStation(station);
            setIsEditing(true);
        } else {
            setCurrentStation({
                name: "",
                latitude: 0,
                longitude: 0,
                description: "",
                imageUrl: "",
                facilities: "",
            });
            setIsEditing(false);
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentStation({
            name: "",
            latitude: 0,
            longitude: 0,
            description: "",
            imageUrl: "",
            facilities: "",
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (isEditing && currentStation.id) {
                await adminService.updateStation(currentStation.id, currentStation);
            } else {
                await adminService.createStation(currentStation);
            }
            loadStations();
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save station", error);
        }
    };

    const handleDelete = async (id: number) => {
        if (window.confirm("Are you sure you want to delete this station?")) {
            try {
                await adminService.deleteStation(id);
                loadStations();
            } catch (error) {
                console.error("Failed to delete station", error);
            }
        }
    };

    const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            try {
                const url = await adminService.uploadFile(e.target.files[0]);
                // Assuming the backend returns the relative path, we might need to prepend the base URL if not handled by backend
                // But for now let's assume the backend returns a usable URL or path
                // If it returns /uploads/filename, and we are on localhost:3000, we need localhost:8080/uploads/filename
                // Let's prepend the API base URL's origin if it's a relative path
                const fullUrl = `http://localhost:8080${url}`;
                setCurrentStation({ ...currentStation, imageUrl: fullUrl });
            } catch (error) {
                console.error("Failed to upload file", error);
            }
        }
    };

    return (
        <div>
            <div className="admin-page-header">
                <h1>Stations Management</h1>
                <button className="btn-primary" onClick={() => handleOpenModal()}>
                    Add Station
                </button>
            </div>

            <table className="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Coordinates</th>
                        <th>Description</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {stations.map((station) => (
                        <tr key={station.id}>
                            <td>{station.id}</td>
                            <td>{station.name}</td>
                            <td>
                                {station.latitude}, {station.longitude}
                            </td>
                            <td>{station.description}</td>
                            <td>
                                <button className="action-btn edit-btn" onClick={() => handleOpenModal(station)}>
                                    Edit
                                </button>
                                <button className="action-btn delete-btn" onClick={() => handleDelete(station.id!)}>
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>{isEditing ? "Edit Station" : "Add Station"}</h2>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Name</label>
                                <input
                                    type="text"
                                    value={currentStation.name}
                                    onChange={(e) => setCurrentStation({ ...currentStation, name: e.target.value })}
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>Latitude</label>
                                <input
                                    type="number"
                                    step="any"
                                    value={currentStation.latitude}
                                    onChange={(e) =>
                                        setCurrentStation({ ...currentStation, latitude: parseFloat(e.target.value) })
                                    }
                                />
                            </div>
                            <div className="form-group">
                                <label>Longitude</label>
                                <input
                                    type="number"
                                    step="any"
                                    value={currentStation.longitude}
                                    onChange={(e) =>
                                        setCurrentStation({ ...currentStation, longitude: parseFloat(e.target.value) })
                                    }
                                />
                            </div>
                            <div className="form-group">
                                <label>Description</label>
                                <textarea
                                    value={currentStation.description}
                                    onChange={(e) =>
                                        setCurrentStation({ ...currentStation, description: e.target.value })
                                    }
                                />
                            </div>
                            <div className="form-group">
                                <label>Facilities</label>
                                <input
                                    type="text"
                                    value={currentStation.facilities}
                                    onChange={(e) =>
                                        setCurrentStation({ ...currentStation, facilities: e.target.value })
                                    }
                                />
                            </div>
                            <div className="form-group">
                                <label>Image</label>
                                <input type="file" onChange={handleFileChange} />
                                {currentStation.imageUrl && (
                                    <img
                                        src={currentStation.imageUrl}
                                        alt="Preview"
                                        style={{ marginTop: "10px", maxWidth: "100%", maxHeight: "150px" }}
                                    />
                                )}
                            </div>
                            <div className="form-actions">
                                <button type="button" className="btn-secondary" onClick={handleCloseModal}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn-primary">
                                    Save
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Stations;
