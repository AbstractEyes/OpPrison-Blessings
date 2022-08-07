package org.pvpingmc.tokens.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class BackpackUtils {

	public static ItemStack getNewBackpack(int capacity) {
		if (Bukkit.getPluginManager().isPluginEnabled("PvPingBackpacks")) {
			org.pvpingmc.backpacks.backpack.Backpack backpack = new org.pvpingmc.backpacks.backpack.Backpack(capacity);
			return backpack.getBackpack();
		}

		return null;
	}

	public static ItemStack upgradeBackpack(ItemStack stack, int addedCapacity) {
		if (Bukkit.getPluginManager().isPluginEnabled("PvPingBackpacks")) {
			org.pvpingmc.backpacks.backpack.Backpack backpack = new org.pvpingmc.backpacks.backpack.Backpack(stack);
			backpack.setMaxItems(backpack.getMaxItems() + addedCapacity);
			return backpack.getBackpack();
		}

		return null;
	}

	public static boolean isBackpack(ItemStack stack) {
		if (Bukkit.getPluginManager().isPluginEnabled("PvPingBackpacks")) {
			return stack != null && org.pvpingmc.backpacks.backpack.BackpackManager.getInstance().isBackpack(stack);
		}

		return false;
	}
}
