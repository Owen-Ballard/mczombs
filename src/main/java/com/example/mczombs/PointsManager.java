package com.example.mczombs;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class PointsManager {

    private static final int STARTING_POINTS = 0;
    private static final int POINTS_PER_ZOMBIE = 1;
    private static final int WAVE_BONUS = 3;
    private static final int MAX_POINTS = 999;

    private PointsManager() {
    }

    public static int getPoints(ServerPlayer player) {
        return player.experienceLevel;
    }

    public static void setPoints(ServerPlayer player, int amount) {
        player.setExperienceLevels(clamp(amount));
    }

    public static void resetPoints(ServerPlayer player) {
        setPoints(player, STARTING_POINTS);
    }

    public static void addPoints(ServerPlayer player, int amount) {
        if (amount <= 0) return;
        setPoints(player, getPoints(player) + amount);
    }

    public static void addZombieKillPoints(ServerPlayer player) {
        addPoints(player, POINTS_PER_ZOMBIE);
    }

    public static void addWaveBonusPoints(ServerPlayer player) {
        addPoints(player, WAVE_BONUS);
    }

    public static boolean spendPoints(ServerPlayer player, int cost) {
        if (getPoints(player) < cost) return false;
        setPoints(player, getPoints(player) - cost);
        return true;
    }

    public static boolean canAfford(ServerPlayer player, int cost) {
        return getPoints(player) >= cost;
    }

    public static void showPoints(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Points: " + getPoints(player)));
    }

    public static void showNotEnoughPoints(ServerPlayer player, int cost) {
        player.sendSystemMessage(Component.literal("Not enough points. Need " + cost));
    }

    public static void showPurchaseSuccess(ServerPlayer player, String name, int cost) {
        player.sendSystemMessage(Component.literal("Bought " + name + " for " + cost));
    }

    public static void showPurchaseFailed(ServerPlayer player, String name) {
        player.sendSystemMessage(Component.literal("Could not buy " + name));
    }

    private static int clamp(int amount) {
        if (amount < 0) return 0;
        return Math.min(amount, MAX_POINTS);
    }
}