import React, { useEffect, useState } from "react";
import { adminService, Train, Provider, TrainLayout } from "../../services/adminService";

const Trains: React.FC = () => {
    const [trains, setTrains] = useState<Train[]>([]);
    const [providers, setProviders] = useState<Provider[]>([]);
    const [layouts, setLayouts] = useState<TrainLayout[]>([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentTrain, setCurrentTrain] = useState<Train>({
        name: "",
        providerId: null,
        totalSeats: 0,
        trainLayoutId: null,
    });
    const [isEditing, setIsEditing] = useState(false);

    useEffect(() => {
        loadTrains();
        loadProviders();
        loadLayouts();
    }, []);

    const loadTrains = async () => {
        try {
            const data = await adminService.getAllTrains();
            setTrains(data);
        } catch (error) {
            console.error("Failed to load trains", error);
        }
    };

    const loadProviders = async () => {
        try {
            const data = await adminService.getAllProviders();
            setProviders(data);
        } catch (error) {
            console.error("Failed to load providers", error);
        }
    };

    const loadLayouts = async () => {
        try {
            const data = await adminService.getAllLayouts();
            setLayouts(data);
        } catch (error) {
            console.error("Failed to load layouts", error);
        }
    };

    const handleOpenModal = (train?: Train) => {
        if (train) {
            setCurrentTrain(train);
            setIsEditing(true);
        } else {
            setCurrentTrain({
                name: "",
                providerId: providers.length > 0 ? providers[0].id! : null,
                totalSeats: 0,
                trainLayoutId: null,
            });
            setIsEditing(false);
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentTrain({ name: "", providerId: null, totalSeats: 0 });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (isEditing && currentTrain.id) {
                await adminService.updateTrain(currentTrain.id, currentTrain);
            } else {
                await adminService.createTrain(currentTrain);
            }
            loadTrains();
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save train", error);
        }
    };

    const handleDelete = async (id: number) => {
        if (window.confirm("Are you sure you want to delete this train?")) {
            try {
                await adminService.deleteTrain(id);
                loadTrains();
            } catch (error) {
                console.error("Failed to delete train", error);
            }
        }
    };

    return (
        <div>
            <div className="admin-page-header">
                <h1>Trains Management</h1>
                <button className="btn-primary" onClick={() => handleOpenModal()}>
                    Add Train
                </button>
            </div>

            <table className="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Provider</th>
                        <th>Layout</th>
                        <th>Total Seats</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {trains.map((train) => (
                        <tr key={train.id}>
                            <td>{train.id}</td>
                            <td>{train.name}</td>
                            <td>{train.providerName || "-"}</td>
                            <td>{train.trainLayoutName || "Manual"}</td>
                            <td>{train.totalSeats}</td>
                            <td>
                                <button className="action-btn edit-btn" onClick={() => handleOpenModal(train)}>
                                    Edit
                                </button>
                                <button className="action-btn delete-btn" onClick={() => handleDelete(train.id!)}>
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
                        <h2>{isEditing ? "Edit Train" : "Add Train"}</h2>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Name</label>
                                <input
                                    type="text"
                                    value={currentTrain.name}
                                    onChange={(e) => setCurrentTrain({ ...currentTrain, name: e.target.value })}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Seat Layout (Optional)</label>
                                <select
                                    value={currentTrain.trainLayoutId || ""}
                                    onChange={(e) => {
                                        const layoutId = e.target.value ? Number(e.target.value) : null;
                                        setCurrentTrain({
                                            ...currentTrain,
                                            trainLayoutId: layoutId,
                                            // If layout selected, totalSeats will be calculated by backend, but we can keep 0 here
                                        });
                                    }}
                                >
                                    <option value="">Manual Seat Count</option>
                                    {layouts.map((l) => (
                                        <option key={l.id} value={l.id}>
                                            {l.layoutName} ({l.totalRows}x{l.seatsPerRow})
                                        </option>
                                    ))}
                                </select>
                            </div>

                            {!currentTrain.trainLayoutId && (
                                <div className="form-group">
                                    <label>Total Seats</label>
                                    <input
                                        type="number"
                                        value={currentTrain.totalSeats}
                                        onChange={(e) =>
                                            setCurrentTrain({ ...currentTrain, totalSeats: Number(e.target.value) })
                                        }
                                        required
                                        min="1"
                                    />
                                </div>
                            )}

                            <div className="form-group">
                                <label>Provider</label>
                                <select
                                    value={currentTrain.providerId || ""}
                                    onChange={(e) =>
                                        setCurrentTrain({ ...currentTrain, providerId: Number(e.target.value) })
                                    }
                                >
                                    <option value="">Select Provider</option>
                                    {providers.map((p) => (
                                        <option key={p.id} value={p.id}>
                                            {p.name}
                                        </option>
                                    ))}
                                </select>
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

export default Trains;
