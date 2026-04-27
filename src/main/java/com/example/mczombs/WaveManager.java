package com.example.mczombs;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WaveManager {

    private static int wave = 0;
    private static int zombiesAlive = 0;
    private static int countdownTicks = 100;
    private static int dayTickCounter = 0;
    private static boolean gameStarted = false;
    private static boolean waveBonusGiven = false;

    private static final BlockPos[] SPAWNS = {
            new BlockPos(10, -55, 58),
            new BlockPos(52, -59, 59),
            new BlockPos(18, -51, 26),
            new BlockPos(8, -48, 58),
            new BlockPos(31, -46, 96)
    };

    private static final BlockPos PLAYER_START = new BlockPos(39, -59, 58);

    public static void serverTick(MinecraftServer server) {
        if (server.getPlayerList().getPlayers().isEmpty()) return;

        ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
        ServerLevel level = server.overworld();

        if (!gameStarted) {
            gameStarted = true;
            wave = 0;
            zombiesAlive = 0;
            countdownTicks = 100;
            dayTickCounter = 0;
            waveBonusGiven = false;

            runCommand(server, "gamerule doMobSpawning false");
            runCommand(server, "gamerule doDaylightCycle false");
            runCommand(server, "time set day");
            runCommand(server, "gamemode survival @a");
            runCommand(server, "setworldspawn 39 -59 58");
            runCommand(server, "spawnpoint @a 39 -59 58");

            player.teleportTo(
                    PLAYER_START.getX() + 0.5,
                    PLAYER_START.getY(),
                    PLAYER_START.getZ() + 0.5
            );

            giveStarterKit(player);
            PointsManager.resetPoints(player);
            clearRandomMobs(level);

            UIManager.showStartMessage(player);
            player.sendSystemMessage(Component.literal("You spawned with a Stone Sword and food."));
        }

        dayTickCounter++;
        if (dayTickCounter >= 200) {
            runCommand(server, "time set day");
            dayTickCounter = 0;
        }

        zombiesAlive = countLivingZombies(level);

        if (zombiesAlive <= 0) {
            if (wave > 0 && !waveBonusGiven) {
                PointsManager.addWaveBonusPoints(player);
                player.sendSystemMessage(Component.literal("+3 bonus points for completing the wave!"));
                waveBonusGiven = true;
            }

            countdownTicks--;

            if (countdownTicks == 80) UIManager.showCountdown(player, 4);
            if (countdownTicks == 60) UIManager.showCountdown(player, 3);
            if (countdownTicks == 40) UIManager.showCountdown(player, 2);
            if (countdownTicks == 20) UIManager.showCountdown(player, 1);

            if (countdownTicks <= 0) {
                startNextWave(player, level);
                countdownTicks = 100;
            }
        }
    }

    private static void startNextWave(ServerPlayer player, ServerLevel level) {
        wave++;
        waveBonusGiven = false;

        int zombieCount = 2 + wave;
        zombiesAlive = zombieCount;

        player.sendSystemMessage(Component.literal("Wave " + wave + " started!"));

        for (int i = 0; i < zombieCount; i++) {
            BlockPos spawnPos = getClosestSpawn(player, i);

            Entity entity = EntityType.ZOMBIE.create(level, EntitySpawnReason.EVENT);

            if (!(entity instanceof Mob zombie)) {
                zombiesAlive--;
                continue;
            }

            zombie.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            zombie.setPersistenceRequired();
            zombie.setTarget(player);
            zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));

            level.addFreshEntity(zombie);
        }
    }

    private static int countLivingZombies(ServerLevel level) {
        int count = 0;

        for (Entity entity : level.getEntities().getAll()) {
            if (entity == null) continue;

            if (entity.getType() == EntityType.ZOMBIE && entity.isAlive() && entity instanceof Mob mob) {
                if (mob.isPersistenceRequired()) {
                    count++;
                }
            }
        }

        return count;
    }

    private static BlockPos getClosestSpawn(ServerPlayer player, int zombieNumber) {
        return SPAWNS[zombieNumber % SPAWNS.length];
    }

    private static void giveStarterKit(ServerPlayer player) {
        player.getInventory().clearContent();
        player.getInventory().add(new ItemStack(Items.STONE_SWORD));
        player.getInventory().add(new ItemStack(Items.COOKED_BEEF, 5));
    }

    private static void clearRandomMobs(ServerLevel level) {
        for (Entity entity : level.getAllEntities()) {
            if (entity == null || !entity.isAlive()) continue;

            if (!(entity instanceof ServerPlayer) && entity.getType() != EntityType.ZOMBIE) {
                entity.discard();
            }
        }
    }

    private static void runCommand(MinecraftServer server, String command) {
        server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(),
                command
        );
    }

    public static void zombieKilled(ServerPlayer player) {
        PointsManager.addZombieKillPoints(player);

        int remaining = countLivingZombies((ServerLevel) player.level());

        if (remaining == 3) {
            player.sendSystemMessage(Component.literal("3 zombies remaining!"));
        }

        if (remaining == 1) {
            player.sendSystemMessage(Component.literal("Last zombie!"));
        }
    }

    public static void gameOver(ServerPlayer player) {
        UIManager.showGameOver(player, wave);
        clearRandomMobs((ServerLevel) player.level());

        gameStarted = false;
        wave = 0;
        zombiesAlive = 0;
        countdownTicks = 100;
        dayTickCounter = 0;
        waveBonusGiven = false;
    }

    public static void respawnPlayer(ServerPlayer player) {
        player.teleportTo(
                PLAYER_START.getX() + 0.5,
                PLAYER_START.getY(),
                PLAYER_START.getZ() + 0.5
        );

        clearRandomMobs((ServerLevel) player.level());
        giveStarterKit(player);
        PointsManager.resetPoints(player);

        player.sendSystemMessage(Component.literal("You respawned with a Stone Sword and food."));
    }
}