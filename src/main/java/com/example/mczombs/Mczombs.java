package com.example.mczombs;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod(Mczombs.MODID)
public class Mczombs {

    public static final String MODID = "mczombs";

    public Mczombs(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onZombieDeath(LivingDeathEvent event) {
        if (event.getEntity().getType() == EntityType.ZOMBIE) {
            if (event.getSource().getEntity() instanceof ServerPlayer player) {
                WaveManager.zombieKilled(player);
            }
        }
    }

    @SubscribeEvent
    public void onZombieDrops(LivingDropsEvent event) {
        if (event.getEntity().getType() == EntityType.ZOMBIE) {
            event.getDrops().clear();
        }
    }

    @SubscribeEvent
    public void onZombieXpDrop(LivingExperienceDropEvent event) {
        if (event.getEntity().getType() == EntityType.ZOMBIE) {
            event.setDroppedExperience(0);
        }
    }

    @SubscribeEvent
    public void onItemFrameAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof net.minecraft.world.entity.decoration.ItemFrame) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WaveManager.gameOver(player);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WaveManager.respawnPlayer(player);
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        WaveManager.serverTick(event.getServer());
    }

    @SubscribeEvent
    public void onItemFrameClick(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ShopManager.handleItemFrameClick(player, event.getTarget());
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer) {
            event.setCanceled(true);
        }
    }
}