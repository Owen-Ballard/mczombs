package com.example.mczombs;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ShopManager {

    private static final int COST_STONE_SWORD = 10;
    private static final int COST_IRON_SWORD = 20;
    private static final int COST_DIAMOND_SWORD = 45;
    private static final int COST_BOW = 18;
    private static final int COST_ARROWS = 8;
    private static final int COST_HELMET = 12;
    private static final int COST_CHESTPLATE = 25;
    private static final int COST_LEGGINGS = 20;
    private static final int COST_BOOTS = 10;
    private static final int COST_FOOD = 8;
    private static final int COST_SHIELD = 18;
    private static final int COST_GOLDEN_APPLE = 35;

    private ShopManager() {
    }

    public static void handleItemFrameClick(ServerPlayer player, Entity target) {
        if (!(target instanceof ItemFrame frame)) {
            return;
        }

        ItemStack displayedItem = frame.getItem();

        if (displayedItem.isEmpty()) {
            player.sendSystemMessage(Component.literal("Put an item in this frame to make it a shop item."));
            return;
        }

        Item item = displayedItem.getItem();

        if (item == Items.STONE_SWORD) {
            buyItem(player, new ItemStack(Items.STONE_SWORD), COST_STONE_SWORD, "Stone Sword");
        } else if (item == Items.IRON_SWORD) {
            buyItem(player, new ItemStack(Items.IRON_SWORD), COST_IRON_SWORD, "Iron Sword");
        } else if (item == Items.DIAMOND_SWORD) {
            buyItem(player, new ItemStack(Items.DIAMOND_SWORD), COST_DIAMOND_SWORD, "Diamond Sword");
        } else if (item == Items.BOW) {
            buyItem(player, new ItemStack(Items.BOW), COST_BOW, "Bow");
        } else if (item == Items.ARROW) {
            buyItem(player, new ItemStack(Items.ARROW, 16), COST_ARROWS, "16 Arrows");
        } else if (item == Items.IRON_HELMET) {
            buyItem(player, new ItemStack(Items.IRON_HELMET), COST_HELMET, "Iron Helmet");
        } else if (item == Items.IRON_CHESTPLATE) {
            buyItem(player, new ItemStack(Items.IRON_CHESTPLATE), COST_CHESTPLATE, "Iron Chestplate");
        } else if (item == Items.IRON_LEGGINGS) {
            buyItem(player, new ItemStack(Items.IRON_LEGGINGS), COST_LEGGINGS, "Iron Leggings");
        } else if (item == Items.IRON_BOOTS) {
            buyItem(player, new ItemStack(Items.IRON_BOOTS), COST_BOOTS, "Iron Boots");
        } else if (item == Items.COOKED_BEEF) {
            buyItem(player, new ItemStack(Items.COOKED_BEEF, 5), COST_FOOD, "5 Steak");
        } else if (item == Items.SHIELD) {
            buyItem(player, new ItemStack(Items.SHIELD), COST_SHIELD, "Shield");
        } else if (item == Items.GOLDEN_APPLE) {
            buyItem(player, new ItemStack(Items.GOLDEN_APPLE), COST_GOLDEN_APPLE, "Golden Apple");
        } else {
            player.sendSystemMessage(Component.literal("This item is not for sale."));
        }
    }

    private static void buyItem(ServerPlayer player, ItemStack item, int cost, String name) {
        if (!PointsManager.canAfford(player, cost)) {
            PointsManager.showNotEnoughPoints(player, cost);
            return;
        }

        boolean spent = PointsManager.spendPoints(player, cost);

        if (!spent) {
            PointsManager.showPurchaseFailed(player, name);
            return;
        }

        player.getInventory().add(item);
        PointsManager.showPurchaseSuccess(player, name, cost);
        player.playSound(net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
    }

    public static void showShopHelp(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Shop Help:"));
        player.sendSystemMessage(Component.literal("Right click an item frame to buy the item shown."));
        player.sendSystemMessage(Component.literal("Your XP level is your points."));
        player.sendSystemMessage(Component.literal("Kill zombies to earn more points."));
    }

    public static void showPriceList(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("Shop Prices:"));
        player.sendSystemMessage(Component.literal("Stone Sword: " + COST_STONE_SWORD));
        player.sendSystemMessage(Component.literal("Iron Sword: " + COST_IRON_SWORD));
        player.sendSystemMessage(Component.literal("Diamond Sword: " + COST_DIAMOND_SWORD));
        player.sendSystemMessage(Component.literal("Bow: " + COST_BOW));
        player.sendSystemMessage(Component.literal("16 Arrows: " + COST_ARROWS));
        player.sendSystemMessage(Component.literal("Iron Helmet: " + COST_HELMET));
        player.sendSystemMessage(Component.literal("Iron Chestplate: " + COST_CHESTPLATE));
        player.sendSystemMessage(Component.literal("Iron Leggings: " + COST_LEGGINGS));
        player.sendSystemMessage(Component.literal("Iron Boots: " + COST_BOOTS));
        player.sendSystemMessage(Component.literal("5 Steak: " + COST_FOOD));
        player.sendSystemMessage(Component.literal("Shield: " + COST_SHIELD));
        player.sendSystemMessage(Component.literal("Golden Apple: " + COST_GOLDEN_APPLE));
    }
}