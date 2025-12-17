import React, { useEffect, useState } from "react";
import { adminService, TrainLayout, PricingRule } from "../../services/adminService";
import "./AdminPricing.css";

const AdminPricing: React.FC = () => {
    const [layouts, setLayouts] = useState<TrainLayout[]>([]);
    const [rules, setRules] = useState<PricingRule[]>([]);
    const [loading, setLoading] = useState(true);

    // Layout Form State
    const [newLayout, setNewLayout] = useState<Partial<TrainLayout>>({
        layoutName: "",
        totalRows: 10,
        seatsPerRow: 4,
        layoutConfig: "[]",
    });
    const [gridPreview, setGridPreview] = useState<number[][]>([]);

    // Rule Form State
    const [newRule, setNewRule] = useState<Partial<PricingRule>>({
        ruleName: "",
        conditionType: "BOOKING_DAYS_BEFORE",
        conditionValue: "1",
        multiplier: 1.1,
        isActive: true,
    });

    useEffect(() => {
        loadData();
    }, []);

    useEffect(() => {
        // Initialize grid when dimensions change
        if (newLayout.totalRows && newLayout.seatsPerRow) {
            const rows = newLayout.totalRows;
            const cols = newLayout.seatsPerRow;
            // Default: 1 (Seat)
            const newGrid = Array(rows)
                .fill(0)
                .map(() => Array(cols).fill(1));
            setGridPreview(newGrid);
        }
    }, [newLayout.totalRows, newLayout.seatsPerRow]);

    const loadData = async () => {
        try {
            const [layoutsData, rulesData] = await Promise.all([
                adminService.getAllLayouts(),
                adminService.getAllPricingRules(),
            ]);
            setLayouts(layoutsData);
            setRules(rulesData);
        } catch (error) {
            console.error("Failed to load pricing data", error);
        } finally {
            setLoading(false);
        }
    };

    // --- Layout Handlers ---

    const toggleCell = (row: number, col: number) => {
        const newGrid = [...gridPreview];
        // Cycle: 1 (Seat) -> 0 (Aisle) -> 2 (Table) -> 1
        newGrid[row][col] = (newGrid[row][col] + 1) % 3;
        setGridPreview(newGrid);
    };

    const handleSaveLayout = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!newLayout.layoutName) return;

        try {
            const layoutToSave = {
                ...newLayout,
                layoutConfig: JSON.stringify(gridPreview),
            } as TrainLayout;

            await adminService.saveLayout(layoutToSave);
            setNewLayout({ layoutName: "", totalRows: 10, seatsPerRow: 4, layoutConfig: "[]" });
            loadData();
        } catch (error) {
            console.error("Failed to save layout", error);
        }
    };

    const handleDeleteLayout = async (id: number) => {
        if (!window.confirm("Delete this layout?")) return;
        try {
            await adminService.deleteLayout(id);
            loadData();
        } catch (error) {
            console.error("Failed to delete layout", error);
        }
    };

    // --- Rule Handlers ---

    const handleSaveRule = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!newRule.ruleName) return;

        try {
            await adminService.savePricingRule(newRule as PricingRule);
            setNewRule({
                ruleName: "",
                conditionType: "BOOKING_DAYS_BEFORE",
                conditionValue: "1",
                multiplier: 1.1,
                isActive: true,
            });
            loadData();
        } catch (error) {
            console.error("Failed to save rule", error);
        }
    };

    const handleDeleteRule = async (id: number) => {
        if (!window.confirm("Delete this rule?")) return;
        try {
            await adminService.deletePricingRule(id);
            loadData();
        } catch (error) {
            console.error("Failed to delete rule", error);
        }
    };

    if (loading) return <div>Loading pricing settings...</div>;

    return (
        <div className="admin-pricing">
            <h2>Seat Configuration & Dynamic Pricing</h2>

            <div className="pricing-grid">
                {/* Seat Layouts Section */}
                <div className="pricing-section">
                    <h3>Train Layouts</h3>
                    <form className="pricing-form" onSubmit={handleSaveLayout}>
                        <h4>Create New Layout</h4>
                        <input
                            type="text"
                            placeholder="Layout Name (e.g. Al Boraq Standard)"
                            value={newLayout.layoutName}
                            onChange={(e) => setNewLayout({ ...newLayout, layoutName: e.target.value })}
                            required
                        />
                        <div className="form-row">
                            <label>
                                Rows:
                                <input
                                    type="number"
                                    value={newLayout.totalRows}
                                    onChange={(e) =>
                                        setNewLayout({ ...newLayout, totalRows: parseInt(e.target.value) })
                                    }
                                    min="1"
                                    max="20"
                                />
                            </label>
                            <label>
                                Seats/Row:
                                <input
                                    type="number"
                                    value={newLayout.seatsPerRow}
                                    onChange={(e) =>
                                        setNewLayout({ ...newLayout, seatsPerRow: parseInt(e.target.value) })
                                    }
                                    min="1"
                                    max="6"
                                />
                            </label>
                        </div>

                        <div className="seat-grid-preview">
                            <p style={{ fontSize: "0.8em", textAlign: "center", marginBottom: "5px" }}>
                                Click cells to toggle: Orange=Seat, Grey=Aisle, Purple=Table
                            </p>
                            {gridPreview.map((row, rIndex) => (
                                <div key={rIndex} className="seat-row">
                                    {row.map((cell, cIndex) => (
                                        <div
                                            key={`${rIndex}-${cIndex}`}
                                            className={`seat-cell ${cell === 1 ? "seat" : cell === 0 ? "aisle" : "table"}`}
                                            onClick={() => toggleCell(rIndex, cIndex)}
                                        >
                                            {cell === 1 ? "S" : cell === 0 ? "" : "T"}
                                        </div>
                                    ))}
                                </div>
                            ))}
                        </div>

                        <button type="submit" className="primary">
                            Save Layout
                        </button>
                    </form>

                    <div className="pricing-list">
                        {layouts.map((layout) => (
                            <div key={layout.id} className="pricing-item">
                                <div className="pricing-item-info">
                                    <h4>{layout.layoutName}</h4>
                                    <span className="pricing-item-meta">
                                        {layout.totalRows}x{layout.seatsPerRow} Grid
                                    </span>
                                </div>
                                <button className="delete-btn" onClick={() => handleDeleteLayout(layout.id!)}>
                                    Delete
                                </button>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Pricing Rules Section */}
                <div className="pricing-section">
                    <h3>Dynamic Pricing Rules</h3>
                    <form className="pricing-form" onSubmit={handleSaveRule}>
                        <h4>Add Pricing Rule</h4>
                        <input
                            type="text"
                            placeholder="Rule Name (e.g. Weekend Surge)"
                            value={newRule.ruleName}
                            onChange={(e) => setNewRule({ ...newRule, ruleName: e.target.value })}
                            required
                        />
                        <div className="form-row">
                            <select
                                value={newRule.conditionType}
                                onChange={(e) => setNewRule({ ...newRule, conditionType: e.target.value })}
                            >
                                <option value="BOOKING_DAYS_BEFORE">Days Before Departure &lt;</option>
                                <option value="IS_WEEKEND">Is Weekend (Sat/Sun)</option>
                                <option value="PEAK_HOUR">Peak Hour (08-10, 17-19)</option>
                            </select>
                            {newRule.conditionType === "BOOKING_DAYS_BEFORE" && (
                                <input
                                    type="number"
                                    placeholder="Days"
                                    value={newRule.conditionValue}
                                    onChange={(e) => setNewRule({ ...newRule, conditionValue: e.target.value })}
                                />
                            )}
                        </div>
                        <div className="form-row">
                            <label>
                                Multiplier (e.g. 1.1 = +10%)
                                <input
                                    type="number"
                                    step="0.05"
                                    value={newRule.multiplier}
                                    onChange={(e) => setNewRule({ ...newRule, multiplier: parseFloat(e.target.value) })}
                                />
                            </label>
                        </div>
                        <button type="submit" className="primary">
                            Save Rule
                        </button>
                    </form>

                    <div className="pricing-list">
                        {rules.map((rule) => (
                            <div key={rule.id} className="pricing-item">
                                <div className="pricing-item-info">
                                    <h4>{rule.ruleName}</h4>
                                    <div className="pricing-item-meta">
                                        {rule.conditionType} {rule.conditionValue ? `(${rule.conditionValue})` : ""}
                                        <span className="multiplier-badge">x{rule.multiplier}</span>
                                    </div>
                                </div>
                                <button className="delete-btn" onClick={() => handleDeleteRule(rule.id!)}>
                                    Delete
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminPricing;
