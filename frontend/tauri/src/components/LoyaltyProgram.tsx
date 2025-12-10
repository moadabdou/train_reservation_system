import React, { useEffect, useState } from "react";
import { LoyaltyStatusDTO, Reward } from "../types";
import { getLoyaltyStatus, getRewards, redeemReward } from "../services/loyaltyService";
import "./LoyaltyProgram.css";

const LoyaltyProgram: React.FC = () => {
    const [status, setStatus] = useState<LoyaltyStatusDTO | null>(null);
    const [rewards, setRewards] = useState<Reward[]>([]);
    const [loading, setLoading] = useState(true);
    const [voucher, setVoucher] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const [statusData, rewardsData] = await Promise.all([getLoyaltyStatus(), getRewards()]);
            setStatus(statusData);
            setRewards(rewardsData);
        } catch (err) {
            console.error("Failed to fetch loyalty data", err);
            setError("Failed to load loyalty program data.");
        } finally {
            setLoading(false);
        }
    };

    const handleRedeem = async (rewardId: number) => {
        try {
            const result = await redeemReward(rewardId);
            setVoucher(result.voucherCode);
            // Refresh status to show updated points
            const updatedStatus = await getLoyaltyStatus();
            setStatus(updatedStatus);
        } catch (err) {
            console.error("Redemption failed", err);
            alert("Failed to redeem reward. You might not have enough points.");
        }
    };

    if (loading) return <div className="loading">Loading loyalty program...</div>;
    if (error) return <div className="error-message">{error}</div>;
    if (!status) return null;

    const getTierClass = (tier: string) => {
        return tier.toLowerCase();
    };

    const calculateProgress = () => {
        if (status.nextTier === "MAX") return 100;
        // A better way would be to know the range of the current tier.
        // For now, let's just use a simple percentage based on points to next tier?
        // Actually, let's just assume the progress bar represents progress towards the NEXT tier from 0 of CURRENT tier.
        // But we don't have the start of the current tier.
        // Let's just do: (pointsBalance / (pointsBalance + pointsToNextTier)) * 100
        const totalNeeded = status.pointsBalance + status.pointsToNextTier;
        return (status.pointsBalance / totalNeeded) * 100;
    };

    return (
        <div className="loyalty-container">
            <div className="loyalty-header">
                <h2>My Loyalty Rewards</h2>
                <p>Earn points with every trip and unlock exclusive benefits!</p>
            </div>

            <div className={`loyalty-card ${getTierClass(status.tierLevel)}`}>
                <div className="tier-badge">{status.tierLevel} MEMBER</div>

                <div className="points-display">
                    <span className="points-value">{status.pointsBalance}</span>
                    <span className="points-label">Available Points</span>
                </div>

                <div className="progress-section">
                    {status.nextTier !== "MAX" ? (
                        <>
                            <div className="progress-bar-container">
                                <div className="progress-bar" style={{ width: `${calculateProgress()}%` }}></div>
                            </div>
                            <p className="progress-text">
                                {status.pointsToNextTier} points to reach {status.nextTier}
                            </p>
                        </>
                    ) : (
                        <p className="progress-text">You have reached the highest tier!</p>
                    )}
                </div>
            </div>

            <div className="rewards-section">
                <h3>Redeem Rewards</h3>
                <div className="rewards-grid">
                    {rewards.map((reward) => (
                        <div key={reward.id} className="reward-card">
                            <div>
                                <div className="reward-cost">
                                    <span>💎</span> {reward.costInPoints} Points
                                </div>
                                <p className="reward-desc">{reward.description}</p>
                            </div>
                            <button
                                className="redeem-btn"
                                disabled={status.pointsBalance < reward.costInPoints}
                                onClick={() => handleRedeem(reward.id)}
                            >
                                {status.pointsBalance < reward.costInPoints ? "Not Enough Points" : "Redeem"}
                            </button>
                        </div>
                    ))}
                </div>
            </div>

            {voucher && (
                <div className="voucher-modal">
                    <div className="voucher-content">
                        <h3>🎉 Reward Redeemed!</h3>
                        <p>Use this code at checkout:</p>
                        <div className="voucher-code">{voucher}</div>
                        <button className="close-btn" onClick={() => setVoucher(null)}>
                            Close
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default LoyaltyProgram;
