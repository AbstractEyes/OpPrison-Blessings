package org.pvpingmc.tokens.listeners;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.pvpingmc.tokens.Main;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantConfig;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.tasks.PotionTask;
import org.pvpingmc.tokens.tasks.PotionTask.AbstractItemStack;
import org.pvpingmc.tokens.tokens.GUI;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;

public class PotionEffectsListener implements Listener {
	OPConfigController utils;
	static OPEnchantData data;

	public PotionEffectsListener(OPConfigController utilsIn) {
		super();
		utils = utilsIn;
		setPotionTask(Maps.<UUID, PotionTask> newConcurrentMap());
	}

	private static PotionEffectsListener instance;

	public static PotionEffectsListener getInstance(OPConfigController utilsIn, OPEnchantData dataIn) {
		data = dataIn;
		if (instance == null) {
			synchronized (PotionEffectsListener.class) {
				if (instance == null) {
					instance = new PotionEffectsListener(utilsIn);
				}
			}
		}

		return instance;
	}

	@Getter @Setter private Map<UUID, PotionTask> potionTask;

	public PotionEffectsListener() {		
		setPotionTask(Maps.<UUID, PotionTask> newConcurrentMap());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHeld(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		PotionTask prevTask = getPotionTask().remove(p.getUniqueId());
		if (prevTask != null) prevTask.cancel(false);

		ItemStack item = p.getInventory().getItem(e.getNewSlot());
		if (item == null || !GUI.getInstance().isTool(item.getType())) return;
		
		if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
			Set<PotionEffect> types = Sets.newHashSet();

			for (String s : item.getItemMeta().getLore()) {
				String stripStr = ChatColor.stripColor(s);

				char charIt = stripStr.charAt(0);
				boolean hasNV = charIt == 'N' && stripStr.startsWith("NightVision ");
				boolean hasSpeed = charIt == 'S' && stripStr.startsWith("Speed ");
				boolean hasHaste = charIt == 'H' && stripStr.startsWith("Haste ");
				boolean hasJump = charIt == 'J' && stripStr.startsWith("Jump");
				if (!hasNV && !hasSpeed && !hasHaste && !hasJump) continue;

				int level = 0;
				try {
					level = Integer.parseInt(stripStr.split(" ")[1]);
				} catch (NumberFormatException ignore) {
					continue;
				}
				if (level < 1) continue;
				
				if (hasNV) {
					types.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 31, level - 1));
				} else if (hasSpeed) {
					// If pvp world, reduce to level 3 speed.
					if(p.getWorld().getName().equalsIgnoreCase("warzone")) if(level > 3) level = 3;
					types.add(new PotionEffect(PotionEffectType.SPEED, 20 * 11, level - 1));
				} else if (hasHaste) {
					types.add(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 11, level - 1));
				} else if (hasJump) {
					// If pvp world, reduce to level 3 jump.
					if(p.getWorld().getName().equalsIgnoreCase("warzone")) if(level > 3) level = 3;
					types.add(new PotionEffect(PotionEffectType.JUMP, 20 * 11, level - 1));
				}
			}
			if (!types.isEmpty()) {
				PotionTask task = new PotionTask(new AbstractItemStack() {
					@Override
					public ItemStack call(Player p) {
						return p.getItemInHand();
					}
				}, item, p.getUniqueId(), utils, data, types.toArray(new PotionEffect[0]));
				start(task);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		PotionTask prevTask = getPotionTask().remove(p.getUniqueId());
		if (prevTask != null) prevTask.cancel();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		PotionTask prevTask = getPotionTask().remove(p.getUniqueId());
		if (prevTask != null) prevTask.cancel();
	}

	public void start(PotionTask task) {
		getPotionTask().computeIfAbsent(task.getUuid(), uuid -> {
			task.runTaskTimer(Main.getInstance(), 1L, 20L * 5);
			return task;
		});
	}

	public void stop(UUID uuid) {
		PotionTask prevTask = getPotionTask().remove(uuid);
		if (prevTask != null) prevTask.cancel();
	}
}
