import React, { useEffect, useState } from "react";
import { adminService, Reward, LoyaltyRule } from "../../services/adminService";
import "./AdminLoyalty.css";

const AdminLoyalty: React.FC = () => {
    const [rewards, setRewards] = useState<Reward[]>([]);
    const [rules, setRules] = useState<LoyaltyRule[]>([]);
    const [loading, setLoading] = useState(true);

    // Form State
    const [newReward, setNewReward] = useState<Partial<Reward>>({
        description: "",
        costInPoints: 100,
        type: "DISCOUNT",
        discountValue: 0,
    });

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const [rewardsData, rulesData] = await Promise.all([
                adminService.getAllRewards(),
                adminService.getAllLoyaltyRules(),
            ]);
            setRewards(rewardsData);
            setRules(rulesData);
        } catch (error) {
            console.error("Failed to load loyalty data", error);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateReward = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!newReward.description || !newReward.costInPoints) return;

        try {
            await adminService.createReward(newReward as Reward);
            setNewReward({ description: "", costInPoints: 100, type: "DISCOUNT", discountValue: 0 });
            loadData();
        } catch (error) {
            console.error("Failed to create reward", error);
        }
    };

    const handleDeleteReward = async (id: number) => {
        if (!window.confirm("Are you sure you want to delete this reward?")) return;
        try {
            await adminService.deleteReward(id);
            loadData();
        } catch (error) {
            console.error("Failed to delete reward", error);
        }
    };

    const handleUpdateRule = async (ruleName: string, newValue: string) => {
        const value = parseFloat(newValue);
        if (isNaN(value)) return;

        try {
            await adminService.updateLoyaltyRule(ruleName, value);
            // Optimistic update
            setRules(rules.map((r) => (r.ruleName === ruleName ? { ...r, value } : r)));
        } catch (error) {
            console.error("Failed to update rule", error);
            loadData(); // Revert on error
        }
    };

    if (loading) return <div>Loading loyalty settings...</div>;

    return (
        <div className="admin-loyalty">
            <h2>Loyalty & Rewards Management</h2>

            <div className="loyalty-grid">
                <div className="rewards-section">
                    <h3>Rewards Catalog</h3>

                    <form className="add-reward-form" onSubmit={handleCreateReward}>
                        <h4>Add New Reward</h4>
                        <div className="form-row">
                            <input
                                type="text"
                                placeholder="Description (e.g. 50% Off Ticket)"
                                value={newReward.description}
                                onChange={(e) => setNewReward({ ...newReward, description: e.target.value })}
                                required
                            />
                            <select
                                value={newReward.type}
                                onChange={(e) => setNewReward({ ...newReward, type: e.target.value })}
                            >
                                <option value="DISCOUNT">Discount</option>
                                <option value="UPGRADE">Upgrade</option>
                                <option value="FREE_TICKET">Free Ticket</option>
                            </select>
                        </div>
                        <div className="form-row">
                            <input
                                type="number"
                                placeholder="Cost (Points)"
                                value={newReward.costInPoints}
                                onChange={(e) => setNewReward({ ...newReward, costInPoints: parseInt(e.target.value) })}
                                required
                            />
                            <input
                                type="number"
                                placeholder="Value (e.g. 0.5 for 50%)"
                                value={newReward.discountValue}
                                onChange={(e) =>
                                    setNewReward({ ...newReward, discountValue: parseFloat(e.target.value) })
                                }
                            />
                        </div>
                        <button type="submit" className="primary">
                            Add Reward
                        </button>
                    </form>

                    <div className="rewards-list">
                        {rewards.map((reward) => (
                            <div key={reward.id} className="reward-card">
                                <h4>{reward.description}</h4>
                                <div className="reward-cost">{reward.costInPoints} pts</div>
                                <span className="reward-type">{reward.type}</span>
                                {reward.discountValue && (
                                    <div className="reward-value">Value: {reward.discountValue}</div>
                                )}
                                <div className="reward-actions">
                                    <button className="danger" onClick={() => handleDeleteReward(reward.id)}>
                                        Delete
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="rules-section">
                    <h3>Earning Rules</h3>
                    <div className="rules-list">
                        {rules.map((rule) => (
                            <div key={rule.id} className="rule-item">
                                <div className="rule-info">
                                    <h4>{rule.ruleName}</h4>
                                    <p>{rule.description}</p>
                                </div>
                                <input
                                    type="number"
                                    className="rule-input"
                                    defaultValue={rule.value}
                                    onBlur={(e) => handleUpdateRule(rule.ruleName, e.target.value)}
                                    step="0.1"
                                />
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminLoyalty;
