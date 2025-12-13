import React, { useEffect, useState } from "react";
import { adminService, Provider } from "../../services/adminService";

const Providers: React.FC = () => {
    const [providers, setProviders] = useState<Provider[]>([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [currentProvider, setCurrentProvider] = useState<Provider>({ name: "", logoUrl: "", contactInfo: "" });
    const [isEditing, setIsEditing] = useState(false);

    useEffect(() => {
        loadProviders();
    }, []);

    const loadProviders = async () => {
        try {
            const data = await adminService.getAllProviders();
            setProviders(data);
        } catch (error) {
            console.error("Failed to load providers", error);
        }
    };

    const handleOpenModal = (provider?: Provider) => {
        if (provider) {
            setCurrentProvider(provider);
            setIsEditing(true);
        } else {
            setCurrentProvider({ name: "", logoUrl: "", contactInfo: "" });
            setIsEditing(false);
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentProvider({ name: "", logoUrl: "", contactInfo: "" });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (isEditing && currentProvider.id) {
                await adminService.updateProvider(currentProvider.id, currentProvider);
            } else {
                await adminService.createProvider(currentProvider);
            }
            loadProviders();
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save provider", error);
        }
    };

    const handleDelete = async (id: number) => {
        if (window.confirm("Are you sure you want to delete this provider?")) {
            try {
                await adminService.deleteProvider(id);
                loadProviders();
            } catch (error) {
                console.error("Failed to delete provider", error);
            }
        }
    };

    return (
        <div>
            <div className="admin-page-header">
                <h1>Providers Management</h1>
                <button className="btn-primary" onClick={() => handleOpenModal()}>
                    Add Provider
                </button>
            </div>

            <table className="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Logo</th>
                        <th>Contact Info</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {providers.map((provider) => (
                        <tr key={provider.id}>
                            <td>{provider.id}</td>
                            <td>{provider.name}</td>
                            <td>
                                {provider.logoUrl && (
                                    <img src={provider.logoUrl} alt={provider.name} style={{ height: "30px" }} />
                                )}
                            </td>
                            <td>{provider.contactInfo}</td>
                            <td>
                                <button className="action-btn edit-btn" onClick={() => handleOpenModal(provider)}>
                                    Edit
                                </button>
                                <button className="action-btn delete-btn" onClick={() => handleDelete(provider.id!)}>
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
                        <h2>{isEditing ? "Edit Provider" : "Add Provider"}</h2>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Name</label>
                                <input
                                    type="text"
                                    value={currentProvider.name}
                                    onChange={(e) => setCurrentProvider({ ...currentProvider, name: e.target.value })}
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>Logo URL</label>
                                <input
                                    type="text"
                                    value={currentProvider.logoUrl}
                                    onChange={(e) =>
                                        setCurrentProvider({ ...currentProvider, logoUrl: e.target.value })
                                    }
                                />
                            </div>
                            <div className="form-group">
                                <label>Contact Info</label>
                                <input
                                    type="text"
                                    value={currentProvider.contactInfo}
                                    onChange={(e) =>
                                        setCurrentProvider({ ...currentProvider, contactInfo: e.target.value })
                                    }
                                />
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

export default Providers;
