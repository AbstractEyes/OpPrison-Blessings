package org.pvpingmc.tokens.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.utils.ExpUtils;

public class ExperienceListener implements Listener {
	OPConfigController utils;
	OPEnchantData data;

	public ExperienceListener(OPConfigController utilsIn, OPEnchantData dataIn) {
		super();
		utils = utilsIn;
		data = dataIn;
	}

	private double getExpModifier() {
		return data.getMultipliers().get("bonus-exp-multiplier");
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onExperience(BlockBreakEvent e) {
		int origin = e.getExpToDrop();
		if (origin < 1) return;
		
		Block b = e.getBlock();
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();
		if (p.getGameMode() != GameMode.SURVIVAL || !hasExperience(item)) return;
		
		ExpUtils expUtils = new ExpUtils(p);
		for (String s : item.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("Experience ")) {
				int level = 0;
				try {
					level = Integer.parseInt(stripStr.split(" ")[1]);
				} catch (NumberFormatException ignore) {
					break;
				}
				if (level < 1) {
					break;
				}
				double modifier = getExperienceModifier(level);
				double extra = origin * modifier;
				if (extra > 0) {
					expUtils.changeExp(extra);
				}
				break;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onExperience(EntityDeathEvent e) {
		int origin = e.getDroppedExp();
		if (origin <= 0) return;
		
		LivingEntity dead = e.getEntity();
		Player p = getKiller(dead);
		if (p == null || p.getGameMode() != GameMode.SURVIVAL) return;

		ItemStack item = p.getItemInHand();
		if (!hasExperience(item)) return;
		
		ExpUtils expUtils = new ExpUtils(p);
		for (String s : item.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("Experience ")) {
				int level = 0;
				try {
					level = Integer.parseInt(stripStr.split(" ")[1]);
				} catch (NumberFormatException ignore) {
					break;
				}
				if (level < 1) {
					break;
				}
				double modifier = getExperienceModifier(level);
				double extra = origin * modifier;
				if (extra > 0) {
					expUtils.changeExp(extra);
				}
				break;
			}
		}
	}

	private Player getKiller(LivingEntity dead) {
		if (dead.getKiller() != null) return dead.getKiller();
		else {
			Player killer = null;
			if (dead.getLastDamageCause() == null || !(dead.getLastDamageCause() instanceof EntityDamageByEntityEvent)) return null;
			
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) dead.getLastDamageCause();
			if (e.getDamager() instanceof Player) killer = (Player) e.getDamager();
			else if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) killer = (Player) ((Projectile) e.getDamager()).getShooter();
			
			return killer;
		}
	}
	
	private double getExperienceModifier(int level) {
		return level * getExpModifier();
	}

	public boolean hasExperience(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR) return false;
		if (!(stack.hasItemMeta()) || !(stack.getItemMeta().hasLore())) return false;
		for (String s : stack.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("Experience ")) return true;
		}
		return false;
	}
}
