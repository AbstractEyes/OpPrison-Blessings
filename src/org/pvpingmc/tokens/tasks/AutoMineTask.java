package org.pvpingmc.tokens.tasks;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.listeners.AutoMineListener;
import org.pvpingmc.tokens.utils.BlockUtils;
import org.pvpingmc.tokens.utils.ItemStackUtils;

import lombok.Getter;
import lombok.Setter;
import org.pvpingmc.tokens.utils.RedItemsUtils;

public class AutoMineTask extends BukkitRunnable {

	@Getter @Setter private ItemStack item;
	@Getter @Setter private UUID uuid;
	@Getter @Setter private AbstractInfo ai;
	@Getter @Setter private OPEnchantData data;
	@Getter @Setter private OPConfigController utils;

	public AutoMineTask(AbstractInfo ai, ItemStack item, UUID uuid,
						OPConfigController utilsIn, OPEnchantData dataIn) {
		setItem(item);
		setUuid(uuid);
		setAi(ai);
		utils = utilsIn;
		data = dataIn;
	}

	public void reap() {
		AutoMineListener.getInstance(utils, data).getAutoMineTask().remove(getUuid());
	}

	@Override
	public void run() {
		Player p = getPlayer();
		if (p == null || !(p.isOnline())) {
			cancel();
			return;
		}

		ItemStack item = getAi().getItemStack(p);
		if (item == null || !(ItemStackUtils.isSimilar(getItem(), item))) {
			cancel();
			return;
		}

		List<Block> blocks = getAi().getNearbyBlocks(p);
		if (blocks == null) return;

		blocks.forEach(block -> BlockUtils.getInstance().cacheCall(p, block));
	}

	public void cancel(boolean reap) {
		if (reap) reap();

		setUuid(null);
		setItem(null);
		setAi(null);

		super.cancel();
	}

	public Player getPlayer() {
		return getUuid() == null ? null : Bukkit.getPlayer(getUuid());
	}

	public static abstract class AbstractInfo {

		public abstract long getTicks();

		public abstract ItemStack getItemStack(Player p);

		public abstract List<Block> getNearbyBlocks(Player p);
	}
}
