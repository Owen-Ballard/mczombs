package com.example.mczombs;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class UIManager {

    private UIManager() {
    }

    public static void showCountdown(ServerPlayer player, int seconds) {
        player.sendSystemMessage(Component.literal("Next wave in " + seconds + "..."));
    }

    public static void showGameOver(ServerPlayer player, int wave) {
        player.sendSystemMessage(Component.literal("Game Over! You reached wave " + wave));
    }

    public static void showStartMessage(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Mczombs started!"));
        player.sendSystemMessage(Component.literal("Survive as many waves as you can."));
        player.sendSystemMessage(Component.literal("Kill zombies to earn points."));
        player.sendSystemMessage(Component.literal("Use item frames to buy shop items."));
    }
}