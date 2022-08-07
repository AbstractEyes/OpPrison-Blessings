package org.pvpingmc.tokens.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemStackUtils {

	@SuppressWarnings("deprecation")
	public static boolean isSimilar(ItemStack item, ItemStack compare) {
		return isSimilar(item, compare, false);
	}

	@SuppressWarnings("deprecation")
	public static boolean isSimilar(ItemStack item, ItemStack compare, boolean exactMeta) {
		if (item == null || compare == null) {
			return false;
		}

		if (item == compare) {
			return true;
		}

		if (item.getTypeId() != compare.getTypeId()) {
			return false;
		}

		if (item.getAmount() != compare.getAmount()) {
			return false;
		}

		if (item.hasItemMeta() != compare.hasItemMeta()) {
			return false;
		}

		if (exactMeta) {
			if (item.hasItemMeta() && !(Bukkit.getItemFactory().equals(item.getItemMeta(), compare.getItemMeta()))) {
				return false;
			}
		} else {
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				if (item.getItemMeta().hasDisplayName() != compare.getItemMeta().hasDisplayName()) {
					return false;
				}

				if (!(item.getItemMeta().getDisplayName().equals(compare.getItemMeta().getDisplayName()))) {
					return false;
				}
			}
		}

		return true;
	}
}
