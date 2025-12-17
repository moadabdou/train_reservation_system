import React, { useEffect, useState } from "react";
import { adminService, CityGuide, OnboardItem } from "../../services/adminService";
import "./AdminContent.css";

const AdminContent: React.FC = () => {
    const [cityGuides, setCityGuides] = useState<CityGuide[]>([]);
    const [onboardItems, setOnboardItems] = useState<OnboardItem[]>([]);
    const [loading, setLoading] = useState(true);

    // Form States
    const [editingGuide, setEditingGuide] = useState<Partial<CityGuide>>({});
    const [editingItem, setEditingItem] = useState<Partial<OnboardItem>>({});

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const [guidesData, itemsData] = await Promise.all([
                adminService.getAllCityGuides(),
                adminService.getAllOnboardItems(),
            ]);
            setCityGuides(guidesData);
            setOnboardItems(itemsData);
        } catch (error) {
            console.error("Failed to load content", error);
        } finally {
            setLoading(false);
        }
    };

    // --- City Guide Handlers ---

    const handleSaveGuide = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!editingGuide.cityName || !editingGuide.content) return;

        try {
            await adminService.saveCityGuide(editingGuide as CityGuide);
            setEditingGuide({});
            loadData();
        } catch (error) {
            console.error("Failed to save guide", error);
        }
    };

    const handleDeleteGuide = async (id: number) => {
        if (!window.confirm("Delete this city guide?")) return;
        try {
            await adminService.deleteCityGuide(id);
            loadData();
        } catch (error) {
            console.error("Failed to delete guide", error);
        }
    };

    // --- Onboard Item Handlers ---

    const handleSaveItem = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!editingItem.name || !editingItem.price) return;

        try {
            await adminService.saveOnboardItem({
                ...editingItem,
                available: editingItem.available ?? true,
            } as OnboardItem);
            setEditingItem({});
            loadData();
        } catch (error) {
            console.error("Failed to save item", error);
        }
    };

    const handleDeleteItem = async (id: number) => {
        if (!window.confirm("Delete this item?")) return;
        try {
            await adminService.deleteOnboardItem(id);
            loadData();
        } catch (error) {
            console.error("Failed to delete item", error);
        }
    };

    if (loading) return <div>Loading content...</div>;

    return (
        <div className="admin-content-cms">
            <h2>Smart Companion CMS</h2>

            <div className="cms-grid">
                {/* City Guides Section */}
                <div className="cms-section">
                    <h3>City Guides</h3>
                    <form className="cms-form" onSubmit={handleSaveGuide}>
                        <h4>{editingGuide.id ? "Edit Guide" : "Add New Guide"}</h4>
                        <input
                            type="text"
                            placeholder="City Name (e.g. Casablanca)"
                            value={editingGuide.cityName || ""}
                            onChange={(e) => setEditingGuide({ ...editingGuide, cityName: e.target.value })}
                            required
                        />
                        <textarea
                            placeholder="Guide Content (Tips, Places to visit...)"
                            value={editingGuide.content || ""}
                            onChange={(e) => setEditingGuide({ ...editingGuide, content: e.target.value })}
                            required
                        />
                        <input
                            type="text"
                            placeholder="Weather API ID (Optional)"
                            value={editingGuide.weatherApiId || ""}
                            onChange={(e) => setEditingGuide({ ...editingGuide, weatherApiId: e.target.value })}
                        />
                        <div className="form-actions">
                            <button type="submit" className="primary">
                                Save Guide
                            </button>
                            {editingGuide.id && (
                                <button type="button" onClick={() => setEditingGuide({})}>
                                    Cancel
                                </button>
                            )}
                        </div>
                    </form>

                    <div className="cms-list">
                        {cityGuides.map((guide) => (
                            <div key={guide.id} className="cms-item">
                                <div className="cms-item-info">
                                    <h4>{guide.cityName}</h4>
                                    <span className="cms-item-meta">{guide.content.substring(0, 50)}...</span>
                                </div>
                                <div className="cms-actions">
                                    <button className="edit-btn" onClick={() => setEditingGuide(guide)}>
                                        Edit
                                    </button>
                                    <button className="delete-btn" onClick={() => handleDeleteGuide(guide.id!)}>
                                        Delete
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Onboard Menu Section */}
                <div className="cms-section">
                    <h3>Onboard Menu Items</h3>
                    <form className="cms-form" onSubmit={handleSaveItem}>
                        <h4>{editingItem.id ? "Edit Item" : "Add New Item"}</h4>
                        <div className="form-row">
                            <input
                                type="text"
                                placeholder="Item Name"
                                value={editingItem.name || ""}
                                onChange={(e) => setEditingItem({ ...editingItem, name: e.target.value })}
                                required
                            />
                            <input
                                type="number"
                                placeholder="Price (DH)"
                                value={editingItem.price || ""}
                                onChange={(e) => setEditingItem({ ...editingItem, price: parseFloat(e.target.value) })}
                                required
                                step="0.5"
                            />
                        </div>
                        <div className="form-row">
                            <select
                                value={editingItem.category || "SNACK"}
                                onChange={(e) => setEditingItem({ ...editingItem, category: e.target.value })}
                            >
                                <option value="SNACK">Snack</option>
                                <option value="DRINK">Drink</option>
                                <option value="MEAL">Meal</option>
                            </select>
                            <label>
                                <input
                                    type="checkbox"
                                    checked={editingItem.available ?? true}
                                    onChange={(e) => setEditingItem({ ...editingItem, available: e.target.checked })}
                                />{" "}
                                Available
                            </label>
                        </div>
                        <div className="form-actions">
                            <button type="submit" className="primary">
                                Save Item
                            </button>
                            {editingItem.id && (
                                <button type="button" onClick={() => setEditingItem({})}>
                                    Cancel
                                </button>
                            )}
                        </div>
                    </form>

                    <div className="cms-list">
                        {onboardItems.map((item) => (
                            <div key={item.id} className="cms-item">
                                <div className="cms-item-info">
                                    <h4>{item.name}</h4>
                                    <div className="cms-item-meta">
                                        {item.category} • {item.price} DH
                                        <span
                                            className={`status-badge ${item.available ? "available" : "unavailable"}`}
                                            style={{ marginLeft: "10px" }}
                                        >
                                            {item.available ? "In Stock" : "Sold Out"}
                                        </span>
                                    </div>
                                </div>
                                <div className="cms-actions">
                                    <button className="edit-btn" onClick={() => setEditingItem(item)}>
                                        Edit
                                    </button>
                                    <button className="delete-btn" onClick={() => handleDeleteItem(item.id!)}>
                                        Delete
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminContent;
