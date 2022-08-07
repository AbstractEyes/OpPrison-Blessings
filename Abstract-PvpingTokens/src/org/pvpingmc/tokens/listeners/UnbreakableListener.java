package org.pvpingmc.tokens.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.tokens.GUI;
import org.pvpingmc.tokens.utils.ItemStackUtils;

public class UnbreakableListener implements Listener {
	OPConfigController utils;
	OPEnchantData data;

	public UnbreakableListener(OPConfigController utilsIn, OPEnchantData dataIn) {
		super();
		utils = utilsIn;
		data = dataIn;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(PlayerItemDamageEvent e) {
		Player p = e.getPlayer();
		ItemStack stack = e.getItem();

		if (stack != null && stack.hasItemMeta() && stack.getItemMeta().hasLore() && checkLore(stack, "&4&lUNBREAKABLE")) {
			e.setCancelled(true);
			setDurability(p, stack, (short) 0);
		} else if (stack != null && GUI.getInstance().isTool(stack.getType()) && ItemStackUtils.isSimilar(stack, p.getItemInHand(), true)) {
			e.setCancelled(true);
			setDurability(p, stack, (short) 0);
		}
	}

	public boolean checkLore(ItemStack stack, String containsLore) {
		for (String lore : stack.getItemMeta().getLore()) {
			if (lore.contains(containsLore)) return true;
		}
		return false;
	}
	
	public void setDurability(Player p, ItemStack stack, short durability) {	
		if (stack != null && ItemStackUtils.isSimilar(stack, p.getItemInHand(), true)) {
			final ItemStack item = stack.clone();
			item.setDurability(durability);
			p.setItemInHand(item);
		} else if (stack != null) {			
			stack.setDurability(durability);
		}
	}
}
