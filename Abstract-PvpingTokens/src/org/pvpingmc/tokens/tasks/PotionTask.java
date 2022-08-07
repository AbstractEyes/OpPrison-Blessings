package org.pvpingmc.tokens.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.listeners.PotionEffectsListener;
import org.pvpingmc.tokens.utils.ItemStackUtils;

import lombok.Getter;
import lombok.Setter;

public class PotionTask extends BukkitRunnable {

	@Getter @Setter private ItemStack item;
	@Getter @Setter private UUID uuid;
	@Getter @Setter private AbstractItemStack ai;
	@Getter @Setter private PotionEffect[] types;
	@Getter @Setter private OPEnchantData data;
	@Getter @Setter private OPConfigController utils;

	public PotionTask(AbstractItemStack ai, ItemStack item, UUID uuid,
					  OPConfigController utilsIn, OPEnchantData dataIn,
					  PotionEffect... types) {
		setItem(item);
		setUuid(uuid);
		setAi(ai);
		setTypes(types);
		utils = utilsIn;
		data = dataIn;
	}

	public void stop(boolean reap) {
		if (getPlayer() == null || getTypes() == null) return;

		if (!reap || PotionEffectsListener.getInstance(utils, data).getPotionTask().remove(getUuid()) != null) {
			for (PotionEffect type : getTypes()) {
				getPlayer().removePotionEffect(type.getType());
			}
		}
	}

	@Override
	public void run() {
		Player p = getPlayer();
		if (p == null || !(p.isOnline())) {
			cancel(true);
			return;
		}

		ItemStack item = getAi().call(p);
		if (item == null || !(ItemStackUtils.isSimilar(getItem(), item))) {
			cancel(true);
			return;
		}

		for (PotionEffect type : getTypes()) {
			p.addPotionEffect(type, true);
		}
	}

	public void cancel(boolean reap) {
		stop(reap);

		setUuid(null);
		setItem(null);
		setTypes(null);
		setAi(null);
		
		super.cancel();
	}

	public Player getPlayer() {
		return getUuid() == null ? null : Bukkit.getPlayer(getUuid());
	}

	public static abstract class AbstractItemStack {

		public abstract ItemStack call(Player p);

	}
}
