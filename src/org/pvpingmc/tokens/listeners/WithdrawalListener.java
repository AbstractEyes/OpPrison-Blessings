package org.pvpingmc.tokens.listeners;

import java.text.NumberFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.pvpingmc.tokens.Main;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.tokens.TokenStorage;
import org.pvpingmc.tokens.utils.ItemStackUtils;
import org.pvpingmc.tokens.utils.Sounds;

public class WithdrawalListener implements Listener {
	OPConfigController utils;
	OPEnchantData data;

	public WithdrawalListener(OPConfigController utilsIn, OPEnchantData dataIn) {
		super();
		utils = utilsIn;
		data = dataIn;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		if (!(e.hasItem())) {
			return;
		}
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		if (item.getType() == Material.INK_SACK && item.getDurability() == 14) {
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				String display = item.getItemMeta().getDisplayName();
				if (display.equalsIgnoreCase(Main.getInstance().getPrimary() + "+1 Token §7(Right Click to Redeem)")) {
					int amount = 0;
					
					for (int slot = 0; slot < p.getInventory().getSize(); slot++) {
						ItemStack stack = p.getInventory().getItem(slot);
						if (ItemStackUtils.isSimilar(item, stack, true)) {
							amount += stack.getAmount();
							p.getInventory().setItem(slot, new ItemStack(Material.AIR, 1));
						}
					}
					
					p.updateInventory();
					
					e.setCancelled(true);
					e.setUseItemInHand(Result.DENY);
					e.setUseInteractedBlock(Result.DENY);
					
					p.playSound(p.getLocation(), Sounds.ORB_PICKUP.bukkitSound(), 1.0F, 0.65F);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l+" + NumberFormat.getInstance().format(amount) + " TOKENS"));
					
					TokenStorage.getInstance().giveTokens(p, amount);
				}	
			}
		}
	}
}
