import api from "./api";
import { LoyaltyStatusDTO, Reward } from "../types";

export const getLoyaltyStatus = async (): Promise<LoyaltyStatusDTO> => {
    const response = await api.get("/loyalty/status");
    return response.data;
};

export const getRewards = async (): Promise<Reward[]> => {
    const response = await api.get("/loyalty/rewards");
    return response.data;
};

export const redeemReward = async (rewardId: number): Promise<{ voucherCode: string }> => {
    const response = await api.post(`/loyalty/redeem/${rewardId}`);
    return response.data;
};
