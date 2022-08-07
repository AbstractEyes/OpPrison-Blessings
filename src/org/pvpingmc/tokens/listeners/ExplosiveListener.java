package org.pvpingmc.tokens.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.abstractphil.enchantments.AbstractEnchantments;
import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.cfg.PassivesData;
import com.abstractphil.enchantments.controller.PassivesController;
import org.bukkit.Bukkit;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.pvpingmc.tokens.Main;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.event.EnchantBlockBreakEvent;
import org.pvpingmc.tokens.event.EnchantToInvEvent;
import org.pvpingmc.tokens.tasks.ExplosiveParticleTask;
import org.pvpingmc.tokens.utils.Area;
import org.pvpingmc.tokens.utils.InventoryUtils;
import org.pvpingmc.tokens.utils.MRLUtils;
import org.pvpingmc.tokens.utils.PlayerUtils;

import com.boydti.fawe.object.collection.BlockVectorSet;
import com.koletar.jj.mineresetlite.Mine;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;

import ninja.coelho.dimm.DIMMPlugin;
import ninja.coelho.dimm.libutil.BlockData;
import ninja.coelho.dimm.libutil.Fast;

public class ExplosiveListener implements Listener {
	OPConfigController utils;
	OPEnchantData data;
	ImbuedData baddaBoom;
	PassivesData armageddonExplosives;
	PassivesData clusterBombs;

	public ExplosiveListener(OPConfigController utilsIn, OPEnchantData dataIn) {
		super();
		utils = utilsIn;
		data = dataIn;
	}

	public void create(Location loc, Player... playerArr) {
		if (playerArr == null || playerArr.length == 0) return;

		ExplosiveParticleTask particleTask = Main.getInstance().getParticleTask();
		particleTask.queue(loc, playerArr);
	}

	private double getMultiplier(String key) {
		return data.getMultipliers().get(key);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onExplosive(BlockBreakEvent e) {
		Block b = e.getBlock();
		String worldName = b.getWorld().getName().toLowerCase();
		if (Main.getInstance().getDisableWorlds().contains(worldName)) return;
		
		Location loc = b.getLocation();
		Player p = e.getPlayer();
		if (p.getGameMode() != GameMode.SURVIVAL || p.getInventory().firstEmpty() == -1) return;

		ItemStack item = p.getItemInHand();
		if (!hasExplosive(item)) return;
		
		Mine mineAt = MRLUtils.getMine(b);
		if (mineAt == null) return;

		for (String s : item.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("Explosive ")) {
				int level = 0;
				try {
					level = Integer.parseInt(stripStr.split(" ")[1]);
					if (Main.MAYHEM_ENCHS) {
						level *= 40;
					}
				} catch (NumberFormatException ignore) {
					break;
				}
				if (level < 1) {
					break;
				}
				double radiusMultiplier = getMultiplier("radius");
				double chanceMultiplier = getMultiplier("chance");
				int levelRadius = Math.min(Math.max((int) Math.ceil(level * radiusMultiplier / 2.25D), 1), 12);
				int levelChance = Math.max((int) Math.ceil(level * chanceMultiplier), 35);

				PassivesController passives = AbstractEnchantments.getInstance().passivesController();
				if(baddaBoom == null) baddaBoom = utils.getUtils().getImbuedConfig(item, "baddaboom");
				if(armageddonExplosives == null) armageddonExplosives = passives.getPassiveData("demolitions");
				if(clusterBombs == null) clusterBombs = passives.getPassiveData("clusterbombs");
				ItemStack passiveItem = passives.getPassiveItem(p);
				float armageddonExplosivesMult = 0;
				int clusterBombsLevels = 0;
				boolean hasShockwave = false;
				hasShockwave = AbstractEnchantments.getInstance().imbuedController().isWearingImbue(p, "shockwaveburst");
				boolean hasCrushing = false;
				hasCrushing = AbstractEnchantments.getInstance().imbuedController().isWearingImbue(p, "crushingboots");
				if(passiveItem != null) {
					armageddonExplosivesMult = (float) armageddonExplosives.getChancePerLevel() *
							passives.getPassiveLevel(passiveItem, "demolitions");
					clusterBombsLevels = passives.getPassiveLevel(passiveItem, "clusterbombs");
				}
				if(baddaBoom != null && utils.getUtils().checkImbueName(item, "baddaboom")) {

					double[] args = baddaBoom.getArgs();
					int     x1 = (int)args[0], x2 = (int)args[1],
							y1 = (int)args[2], y2 = (int)args[3],
							z1 = (int)args[4], z2 = (int)args[5];
					executeBaddaBoomExplosion(p, mineAt, loc,
							levelRadius, levelChance, level, 0.10F,
							x1, x2, y1, y2, z1, z2, armageddonExplosivesMult, clusterBombsLevels, hasShockwave, hasCrushing);
				} else {
					executeExplosion(p, mineAt, loc, levelRadius, levelChance,
							level, 0.10F, armageddonExplosivesMult, clusterBombsLevels, hasShockwave, hasCrushing);
				}
				break;
			}
		}
	}

	private void prepareClusterBombs(BlockVectorSet blockSet, World world,
									 int rangeToStart, Location loc, int clusterBombs) {
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		if(clusterBombs > 0) {
			// Clamp 10 cluster bombs at most.
			for (int i = 0; i < Math.min(10, clusterBombs); i++) {
				int nextX = rand.nextInt(rangeToStart, rangeToStart+5);
				if(rand.nextBoolean()) nextX = -nextX;
				int nextY = rand.nextInt(rangeToStart, rangeToStart+5);
				if(rand.nextBoolean()) nextY = -nextY;
				int nextZ = rand.nextInt(rangeToStart, rangeToStart+5);
				if(rand.nextBoolean()) nextZ = -nextZ;
				Location tempLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
				tempLoc.add(nextX, nextY, nextZ);
				;
				prepareBlockSet(blockSet, world, 3*3*3, nextX, nextY, nextZ,
						rand.nextInt(-1, 1),
						rand.nextInt(-1, 1),
						rand.nextInt(-1, 1),
						rand.nextInt(-1, 1),
						rand.nextInt(-1, 1),
						rand.nextInt(-1, 1));
			}
		}
	}

	private void prepareBlockSet(BlockVectorSet blockSet, World world, int rangeSq,
										   int xOrig, int yOrig, int zOrig,
										   int minX, int minY, int minZ,
										   int maxX, int maxY, int maxZ) {
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				for (int y = minY; y <= maxY; y++) {
					if (x == xOrig && y == yOrig && z == zOrig) continue;

					int xMod = xOrig - x, yMod = yOrig - y, zMod = zOrig - z;
					if (xMod * xMod + yMod * yMod + zMod * zMod <= rangeSq) {
						Vector vector = Vector.toBlockPoint(x, y, z);
						Location relLoc = BukkitUtil.toLocation(world, vector);
						Mine mineAt = MRLUtils.getMine(relLoc);
						if (mineAt != null) {
							blockSet.add(vector);
						}
					}
				}
			}
		}
	}

	private void executeBaddaBoomExplosion(Player p, Mine mine, Location origin,
										   int range, int chanceOfExecution, int level, float chanceOfParticles,
										   int x1, int x2, int y1, int y2, int z1, int z2,
										   float armageddonExplosionMult, int clusterBombLevel,
										   boolean hasShockwave, boolean hasCrushing) {
		World world = origin.getWorld();

		int xOrig = origin.getBlockX(),
				yOrig = origin.getBlockY(),
				zOrig = origin.getBlockZ(),
				normalExplosiveRange = (int)Math.ceil(range / 1.5D),
				changedRange = normalExplosiveRange + (x1 + x2 + 1),
				rangeSq = (changedRange * changedRange);

		double hasShockwaveAmount = 0;
		if(hasShockwave) hasShockwaveAmount += 0.05;
		if(hasCrushing) hasShockwaveAmount += 0.05;
		final double readyShockwave = hasShockwaveAmount;

		DIMMPlugin dimms = Main.getInstance().getDimmPlugin();
		Fast fastctx = dimms.getLibUtil().getFast().withFlags(true, true);
		ninja.coelho.arkjs.extern.bukkit.Bukkit bukkitctx = dimms.getBukkit();
		bukkitctx.runAsync(() -> {
			BlockVectorSet blockSet = new BlockVectorSet();
			// Cube calculation.
			// Explosive calculation.
			int minX = xOrig - normalExplosiveRange - x1, maxX = xOrig + normalExplosiveRange + x2;
			int minY = yOrig - normalExplosiveRange - y1, maxY = yOrig + normalExplosiveRange + y2;
			int minZ = zOrig - normalExplosiveRange - z1, maxZ = zOrig + normalExplosiveRange + z2;
			int minBoomX = xOrig - x1, maxBoomX = xOrig + x2;
			int minBoomY = yOrig - y1, maxBoomY = yOrig + y2;
			int minBoomZ = zOrig - z1, maxBoomZ = zOrig + z2;

			Area boomArea = new Area(minBoomX, maxBoomX, minBoomY, maxBoomY, minBoomZ, maxBoomZ);
			//Area fullArea = new Area(minX, maxX, minY, maxY, minZ, maxZ);

			prepareBlockSet(blockSet, world, rangeSq, xOrig, yOrig, zOrig, minX, minY, minZ, maxX, maxY, maxZ);
			prepareClusterBombs(blockSet, world, changedRange+4, origin, clusterBombLevel);

			if (blockSet.isEmpty()) return;

			bukkitctx.runSync(() -> {
				ThreadLocalRandom localRng = ThreadLocalRandom.current();
				Map<ItemStack, Long> dropCache = new HashMap();
				int totalBlocks = 0;

				Iterator<Vector> blockSetItr = blockSet.iterator();
				Vector vector = null;
				while (blockSetItr.hasNext()) {
					vector = blockSetItr.next();
					if (vector == null) break;

					int vecX = vector.getBlockX(), vecY = vector.getBlockY(), vecZ = vector.getBlockZ();
					BlockData blockData = fastctx.blockGet(world, vecX, vecY, vecZ);
					if (Main.getInstance().isIgnored(blockData.getId())) continue;

					if (localRng.nextInt(chanceOfExecution) <= level ||
							localRng.nextInt(1, 100) <= Math.ceil((armageddonExplosionMult + readyShockwave) * 100) ||
							boomArea.inside(vecX, vecY, vecZ)) {
						totalBlocks++;

						Location relLoc = BukkitUtil.toLocation(world, vector);
						Block relBlock = relLoc.getBlock();
						if (localRng.nextFloat() <= chanceOfParticles) {
							int amount = localRng.nextInt(2, 5);
							Player[] playerArr = PlayerUtils.getNearbyPlayers(relLoc, 8);
							for (int i = 0; i < amount; i++) create(relLoc, playerArr);
						}

						List<ItemStack> drops = InventoryUtils.getInstance().executeAutoInv(p, relBlock);
						//InventoryUtils.getInstance().checkForAndRemoveData(relBlock);
						fastctx.blockSet(relBlock, 0, 0);

						if (drops != null && !drops.isEmpty()) {
							drops.forEach(item -> {
								long amount = item.getAmount();
								item.setAmount(1);
								dropCache.compute(item, (key, value) -> {
									if (value != null) {
										return value + amount;
									}
									return amount;
								});
							});
						}
					}
				}
				List<ItemStack> optimizeDrops = new ArrayList(dropCache.size());
				dropCache.forEach((item, amount) -> {
					int amountRemainder = Math.max((int) Math.ceil(amount / Integer.MAX_VALUE), 0);
					if (amountRemainder <= 1) {
						item.setAmount(amount.intValue());
						optimizeDrops.add(item);
						return;
					}
					while (amountRemainder-- > 0) {
						item.setAmount(amount.intValue());
						optimizeDrops.add(item);
						amount -= item.getAmount();
						if (amount <= 0) break;
					}
				});
				EnchantToInvEvent toEvent = new EnchantToInvEvent(p, optimizeDrops);
				Bukkit.getPluginManager().callEvent(toEvent);
				if (!toEvent.isCancelled() && !toEvent.getDrops().isEmpty()) toEvent.getDrops().forEach(p.getInventory()::addItem);

				if (totalBlocks > 0) {
					EnchantBlockBreakEvent breakEvent = new EnchantBlockBreakEvent(p, totalBlocks);
					Bukkit.getPluginManager().callEvent(breakEvent);
					if (Main.getInstance().isMrlEnabled()) MRLUtils.countBreak(mine, totalBlocks);
				}
			});
		});
	}

	private boolean insideBaddaBoom(Vector blockPointIn,
									int minX, int maxX,
									int minY, int maxY,
									int minZ, int maxZ) {
		return(blockPointIn.getBlockX() >= minX &&
				blockPointIn.getBlockX() <= maxX &&
				blockPointIn.getBlockY() >= minY &&
				blockPointIn.getBlockY() <= maxY &&
				blockPointIn.getBlockZ() >= minZ &&
				blockPointIn.getBlockZ() <= maxZ);
	}

	// --------------------------------------------------------
	private void executeExplosion(Player p, Mine mine, Location origin, int range, int chanceOfExecution,
								  int level, float chanceOfParticles,
								  float armageddonExplosionMult, int clusterBombLevel,
								  boolean hasShockwave, boolean hasCrushing) {
		World world = origin.getWorld();
		int xOrig = origin.getBlockX(), yOrig = origin.getBlockY(), zOrig = origin.getBlockZ(), rangeSq = range * range;

		double hasShockwaveAmount = 0;
		if(hasShockwave) hasShockwaveAmount += 0.05;
		if(hasCrushing) hasShockwaveAmount += 0.05;
		final double readyShockwave = hasShockwaveAmount;
		DIMMPlugin dimms = Main.getInstance().getDimmPlugin();
		Fast fastctx = dimms.getLibUtil().getFast().withFlags(true, true);
		ninja.coelho.arkjs.extern.bukkit.Bukkit bukkitctx = dimms.getBukkit();
		bukkitctx.runAsync(() -> {
			BlockVectorSet blockSet = new BlockVectorSet();

			int minX = xOrig - range, maxX = xOrig + range;
			int minY = yOrig - range, maxY = yOrig + range;
			int minZ = zOrig - range, maxZ = zOrig + range;

			Area fullArea = new Area(minX, maxX, minY, maxY, minZ, maxZ);

			prepareBlockSet(blockSet, world, rangeSq, xOrig, yOrig, zOrig, minX, minY, minZ, maxX, maxY, maxZ);
			prepareClusterBombs(blockSet, world, range+3, origin, (int)clusterBombLevel);


			if (blockSet.isEmpty()) return;

			bukkitctx.runSync(() -> {
				ThreadLocalRandom localRng = ThreadLocalRandom.current();
				Map<ItemStack, Long> dropCache = new HashMap();
				int totalBlocks = 0;

				Iterator<Vector> blockSetItr = blockSet.iterator();
				Vector vector = null;
				while (blockSetItr.hasNext()) {
					vector = blockSetItr.next();
					if (vector == null) break;

					int vecX = vector.getBlockX(), vecY = vector.getBlockY(), vecZ = vector.getBlockZ();
					BlockData blockData = fastctx.blockGet(world, vecX, vecY, vecZ);
					if (Main.getInstance().isIgnored(blockData.getId())) continue;

					if (localRng.nextInt(chanceOfExecution) <= level ||
							localRng.nextInt(0, 100) <= Math.ceil((armageddonExplosionMult + readyShockwave) * 100)) {
						totalBlocks++;

						Location relLoc = BukkitUtil.toLocation(world, vector);
						Block relBlock = relLoc.getBlock();
						if (localRng.nextFloat() <= chanceOfParticles) {
							int amount = localRng.nextInt(2, 5);
							Player[] playerArr = PlayerUtils.getNearbyPlayers(relLoc, 8);
							for (int i = 0; i < amount; i++) create(relLoc, playerArr);
						}

						List<ItemStack> drops = InventoryUtils.getInstance().executeAutoInv(p, relBlock);
						//InventoryUtils.getInstance().checkForAndRemoveData(relBlock);
						fastctx.blockSet(relBlock, 0, 0);

						if (drops != null && !drops.isEmpty()) {
							drops.forEach(item -> {
								long amount = item.getAmount();
								item.setAmount(1);
								dropCache.compute(item, (key, value) -> {
									if (value != null) {
										return value + amount;
									}
									return amount;
								});
							});
						}
					}
				}
				List<ItemStack> optimizeDrops = new ArrayList(dropCache.size());
				dropCache.forEach((item, amount) -> {
					int amountRemainder = Math.max((int) Math.ceil(amount / Integer.MAX_VALUE), 0);
					if (amountRemainder <= 1) {
						item.setAmount(amount.intValue());
						optimizeDrops.add(item);
						return;
					}
					while (amountRemainder-- > 0) {
						item.setAmount(amount.intValue());
						optimizeDrops.add(item);
						amount -= item.getAmount();
						if (amount <= 0) break;
					}
				});
				EnchantToInvEvent toEvent = new EnchantToInvEvent(p, optimizeDrops);
				Bukkit.getPluginManager().callEvent(toEvent);
				if (!toEvent.isCancelled() && !toEvent.getDrops().isEmpty()) toEvent.getDrops().forEach(p.getInventory()::addItem);

				if (totalBlocks > 0) {
					EnchantBlockBreakEvent breakEvent = new EnchantBlockBreakEvent(p, totalBlocks);
					Bukkit.getPluginManager().callEvent(breakEvent);
					if (Main.getInstance().isMrlEnabled()) MRLUtils.countBreak(mine, totalBlocks);
				}
			});
		});
	}
	
	public boolean hasExplosive(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR) return false;
		if (!(stack.hasItemMeta()) || !(stack.getItemMeta().hasLore())) return false;
		
		for (String s : stack.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("Explosive ")) return true;
		}
		return false;
	}
}
