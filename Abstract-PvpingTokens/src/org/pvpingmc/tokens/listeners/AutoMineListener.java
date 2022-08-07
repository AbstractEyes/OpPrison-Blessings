package org.pvpingmc.tokens.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.pvpingmc.tokens.Main;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantConfig;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.nms.reflection.ParticleEffect;
import org.pvpingmc.tokens.tasks.AutoMineTask;
import org.pvpingmc.tokens.tasks.AutoMineTask.AbstractInfo;
import org.pvpingmc.tokens.utils.MRLUtils;
import org.pvpingmc.tokens.utils.PlayerUtils;

import com.google.common.collect.Maps;
import com.koletar.jj.mineresetlite.Mine;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import ninja.coelho.dimm.DIMMPlugin;
import ninja.coelho.dimm.libutil.BlockData;
import ninja.coelho.dimm.libutil.Fast;

public class AutoMineListener implements Listener {
	OPConfigController utils;
	static OPEnchantData data;

	public AutoMineListener(OPConfigController utilsIn) {
		super();
		utils = utilsIn;
		setAutoMineTask(Maps.<UUID, AutoMineTask>newConcurrentMap());
	}

	private static AutoMineListener instance;

	public static AutoMineListener getInstance(OPConfigController utilsIn, OPEnchantData dataIn) {
		data = dataIn;
		if (instance == null) {
			synchronized (AutoMineListener.class) {
				if (instance == null) {
					instance = new AutoMineListener(utilsIn);
				}
			}
		}

		return instance;
	}

	private OPEnchantData getData() {
		return data;
	}

	private int getRange(String rangeKey)
	{
		if(data == null && !rangeKey.equals("animation-range")) return 3;
		if(data == null) return 8;
		return (int)Math.round(data.getRanges().get(rangeKey));
	}

	private long getTimer(String key) {
		return Math.round(data.getTimers().get(key));
	}

	// This is a server listener.
	private static final long AUTOMINE_BASE_VAL = 20L * 500;

	@Getter @Setter private Map<UUID, AutoMineTask> autoMineTask;

	public AutoMineListener() {
		setAutoMineTask(Maps.<UUID, AutoMineTask>newConcurrentMap());
	}

	public void create(Location loc, Player... players) {
		if (players == null || players.length == 0) return;

		ParticleEffect.CLOUD.display((float) Math.random(), (float) Math.random(), (float) Math.random(), (float) Math.random(), ThreadLocalRandom.current().nextInt(5, 20), loc, players);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onHeld(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		AutoMineTask prevTask = getAutoMineTask().remove(p.getUniqueId());
		if (prevTask != null) prevTask.cancel();

		if (p.getGameMode() != GameMode.SURVIVAL || p.getInventory().firstEmpty() == -1) return;

		String worldName = p.getWorld().getName().toLowerCase();
		if (Main.getInstance().getDisableWorlds().contains(worldName)) return;

		ItemStack item = p.getInventory().getItem(e.getNewSlot());
		if (!hasAutoMine(item)) return;

		int level = 0;
		for (String s : item.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("AutoMine ")) {
				try {
					level = Integer.parseInt(stripStr.split(" ")[1]);
					if (Main.MAYHEM_ENCHS) {
						level *= 2;
					}
					break;
				} catch (NumberFormatException ignore) {
					break;
				}
			}
		}
		if (level > 0) {
			final int finalLevel = level;
			int x1 = getRange("x1"),
				x2 = getRange("x2"),
				y1 = getRange("y1"),
				y2 = getRange("y2"),
				z1 = getRange("z1"),
				z2 = getRange("z2");
			//final long miningTicks = getTimer("auto-mine-ticks"),
				//minimumTicks = getTimer("auto-mine-minimum-ticks");
			AutoMineTask task = new AutoMineTask(new AbstractInfo() {

				@Override
				public List<Block> getNearbyBlocks(Player p) {
					if (p.getInventory().firstEmpty() == -1) return null;

					Location loc = p.getLocation();
					Mine near = MRLUtils.getMineNearby(loc);
					if (near == null) return null;
					// Todo: test range code.
					//Location rng = getRngNearby(loc,
							//x1 - Math.max(1, (finalLevel / 100)),
							//x2 + Math.max(1, (finalLevel / 100)),
							//y1 - Math.max(1, (finalLevel / 100)),
							//y2 + Math.max(1, (finalLevel / 100)),
							//z1 - Math.max(1, (finalLevel / 100)),
							//z2 + Math.max(1, (finalLevel / 100)));
					Location rng = getRngNearby(loc, 3);
					if (rng == null) return null;

					create(rng, PlayerUtils.getNearbyPlayers(rng, getRange("animation-range")));
					return Collections.singletonList(rng.getBlock());
				}

				@Override
				public ItemStack getItemStack(Player p) {
					return p.getItemInHand();
				}

				@Override
				public long getTicks() {
					return Math.min(Math.max((long)Math.floor((double)(AUTOMINE_BASE_VAL / (long)finalLevel)), 7L), 500L);
				}

			}, item, p.getUniqueId(), utils, data);

			start(task);
		}
	}

	private Location getRngNearby(Location origin, int x1, int x2, int y1, int y2, int z1, int z2) {
		List<Location> internalLocations = new ArrayList();
		World world = origin.getWorld();

		DIMMPlugin dimms = Main.getInstance().getDimmPlugin();
		Fast fastctx = dimms.getLibUtil().getFast().withFlags(true, true);

		int xOrig = origin.getBlockX(), yOrig = origin.getBlockY(), zOrig = origin.getBlockZ();
		int minX = xOrig - x1, maxX = xOrig + x2;
		int minY = yOrig - y1, maxY = yOrig + y2;
		int minZ = zOrig - z1, maxZ = zOrig + z2;
		System.out.println(" origin: " + origin);
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				for (int y = minY; y <= maxY; y++) {
					BlockData blockData = fastctx.blockGet(world, x, y, z);
					if (Main.getInstance().isIgnored(blockData.getId())) continue;

					Vector vector = Vector.toBlockPoint(x, y, z);
					Location relLoc = BukkitUtil.toLocation(world, vector);
					Mine mineAt = MRLUtils.getMine(relLoc);
					if (mineAt != null) internalLocations.add(relLoc);
				}
			}
		}
		if (internalLocations.isEmpty()) return null;
		Collections.shuffle(internalLocations);
		return internalLocations.get(0);
	}

	private Location getRngNearby(Location origin, int range) {
		List<Location> internalLocations = new ArrayList();
		World world = origin.getWorld();

		DIMMPlugin dimms = Main.getInstance().getDimmPlugin();
		Fast fastctx = dimms.getLibUtil().getFast().withFlags(true, true);

		int xOrig = origin.getBlockX(), yOrig = origin.getBlockY(), zOrig = origin.getBlockZ();
		int minX = xOrig - range, maxX = xOrig + range;
		int minY = yOrig - range, maxY = yOrig + range;
		int minZ = zOrig - range, maxZ = zOrig + range;

		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				for (int y = minY; y <= maxY; y++) {
					BlockData blockData = fastctx.blockGet(world, x, y, z);
					if (Main.getInstance().isIgnored(blockData.getId())) continue;

					Vector vector = Vector.toBlockPoint(x, y, z);
					Location relLoc = BukkitUtil.toLocation(world, vector);
					Mine mineAt = MRLUtils.getMine(relLoc);
					if (mineAt != null) internalLocations.add(relLoc);
				}
			}
		}
		if (internalLocations.isEmpty()) return null;
		Collections.shuffle(internalLocations);
		return internalLocations.get(0);
	}

	public void start(AutoMineTask task) {
		getAutoMineTask().computeIfAbsent(task.getUuid(), uuid -> {
			task.runTaskTimer(Main.getInstance(), task.getAi().getTicks(), task.getAi().getTicks());
			return task;
		});
	}

	public void stop(UUID uuid) {
		AutoMineTask prevTask = getAutoMineTask().remove(uuid);
		if (prevTask != null) prevTask.cancel();
	}

	public boolean hasAutoMine(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR) return false;
		if (!(stack.hasItemMeta()) || !(stack.getItemMeta().hasLore())) return false;

		for (String s : stack.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("AutoMine ")) return true;
		}
		return false;
	}
}
