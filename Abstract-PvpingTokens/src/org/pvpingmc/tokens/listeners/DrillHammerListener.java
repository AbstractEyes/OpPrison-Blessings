package org.pvpingmc.tokens.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.abstractphil.enchantments.AbstractEnchantments;
import com.abstractphil.enchantments.cfg.ImbuedConfig;
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
import org.pvpingmc.tokens.nms.reflection.ParticleEffect;
import org.pvpingmc.tokens.utils.InventoryUtils;
import org.pvpingmc.tokens.utils.MRLUtils;
import org.pvpingmc.tokens.utils.PlayerUtils;
import org.pvpingmc.tokens.utils.Sounds;

import com.boydti.fawe.object.collection.BlockVectorSet;
import com.koletar.jj.mineresetlite.Mine;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;

import ninja.coelho.dimm.DIMMPlugin;
import ninja.coelho.dimm.libutil.BlockData;
import ninja.coelho.dimm.libutil.Fast;

public class DrillHammerListener implements Listener {
	OPConfigController utils;
	OPEnchantData data;
	ImbuedData megaDrill;
	PassivesData efficientDrilling;
	PassivesData drillDozer;

	public DrillHammerListener(OPConfigController utilsIn, OPEnchantData dataIn) {
		super();
		utils = utilsIn;
		data = dataIn;
	}

	public void create(Location loc, Player... players) {
		if (players == null || players.length == 0) return;

		ParticleEffect.WITCH_SPELL.display(0.5F, 0.5F, 0.5F, 0.15F, 1, loc, players);
		ParticleEffect.SMOKE_NORMAL.display(0.5F, 0.5F, 0.5F, 0.15F, 1, loc, players);

		if (ThreadLocalRandom.current().nextInt(75) == 1) for (Player player : players) player.playSound(loc, Sounds.DIG_STONE.bukkitSound(), 5.0F, 1.5F);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDrillHammer(BlockBreakEvent e) {
		Block b = e.getBlock();
		String worldName = b.getWorld().getName().toLowerCase();
		if (Main.getInstance().getDisableWorlds().contains(worldName)) return;

		Location loc = b.getLocation();
		Player p = e.getPlayer();
		if (p.getGameMode() != GameMode.SURVIVAL || p.getInventory().firstEmpty() == -1) return;

		ItemStack item = p.getItemInHand();
		if (!hasDrillHammer(item)) return;

		Mine mineAt = MRLUtils.getMine(b);
		if (mineAt == null) return;

		int levelYMax = Math.abs(mineAt.getMaxY() - mineAt.getMinY());
		for (String s : item.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("DrillHammer ")) {
				int level = 0;
				try {
					level = Integer.parseInt(stripStr.split(" ")[1]);
					if (Main.MAYHEM_ENCHS) {
						level = (int) (level * 2.5);
					}
				} catch (NumberFormatException ignore) {
					break;
				}
				if (level < 1) {
					break;
				}
				int levelYLayers = Math.min((int) randomWithRange(1, Math.ceil(level * .01)), levelYMax);
				double levelChance = level * .1;

				PassivesController passives = AbstractEnchantments.getInstance().passivesController();
				if(megaDrill == null) megaDrill = utils.getUtils().getImbuedConfig(item, "megadrill");
				if(drillDozer == null) drillDozer = passives.getPassiveData("drilldozer");
				if(efficientDrilling == null) efficientDrilling = passives.getPassiveData("armageddon");
				ItemStack passiveItem = passives.getPassiveItem(p);
				float armageddonChance = 0;
				int drillDozerLevel = 0;
				boolean hasMegaDrill = false;
				boolean hasCrushingBoots = false;
				boolean hasShockwaveChest = false;
				if(passiveItem != null) {
					armageddonChance = (float)efficientDrilling.getChancePerLevel() *
							passives.getPassiveLevel(passiveItem, "armageddon");
					drillDozerLevel = passives.getPassiveLevel(passiveItem, "drilldozer");
				}
				hasMegaDrill = utils.getUtils().checkImbueName(item, "megadrill");
				hasShockwaveChest = utils.getUtils().checkImbueName(item, "shockwaveburst");
				hasCrushingBoots = AbstractEnchantments.getInstance().imbuedController().isWearingImbue(p, "crushingboots");
				executeExplosion(p, loc, mineAt, levelYLayers, levelChance, armageddonChance, drillDozerLevel, hasMegaDrill, hasCrushingBoots, hasShockwaveChest);
				break;
			}
		}
	}
	
	private double randomWithRange(double min, double max) {
		double range = Math.abs(max - min);
		return (Math.random() * range) + Math.min(min, max);
	}

	private void prepareCubeSet(BlockVectorSet blockSet, World world, int rangeSq,
								int xOrig, int yOrig, int zOrig,
								int minX, int minY, int minZ,
								int maxX, int maxY, int maxZ) {
		if(rangeSq < 0) {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					for (int y = minY; y <= maxY; y++) {
						if (x == xOrig && y == yOrig && z == zOrig) continue;
						Vector vector = Vector.toBlockPoint(x, y, z);
						Location relLoc = BukkitUtil.toLocation(world, vector);
						Mine mineAt = MRLUtils.getMine(relLoc);
						if (mineAt != null) {
							blockSet.add(vector);
						}
					}
				}
			}
		} else {
			for (int x = minX; x <= maxX; x++) {
				for (int z = minZ; z <= maxZ; z++) {
					for (int y = minY; y <= maxY; y++) {
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
	}


	private void executeExplosion(Player p, Location location, Mine mine,
								  int yOffset, double chanceOfExecution, float armageddonLevel,
								  int drillDozerLevel, boolean hasMegaDrill,
								  boolean hasCrushingBoots, boolean hasShockwaveChest) {
		int reducedOdds = 0; if(hasShockwaveChest) reducedOdds = 500;
		double randomChance = randomWithRange(0, (3000 - reducedOdds) - (3000 * (armageddonLevel * 0.000025)));
		if (randomChance <= chanceOfExecution) {
			location.getWorld().playSound(location, Sounds.ENDERDRAGON_HIT.bukkitSound(), 10.0F, 1.0F);

			Block block = location.getBlock();
			World world = location.getWorld();

			int xOrig = block.getX(), yOrig = block.getY(), zOrig = block.getZ();
			int maxX = mine.getMaxX(), minX = mine.getMinX();
			int maxY = yOrig, minY = yOrig - (yOffset > 1 ? yOffset - 1 : 0);
			int maxZ = mine.getMaxZ(), minZ = mine.getMinZ();


			DIMMPlugin dimms = Main.getInstance().getDimmPlugin();
			Fast fastctx = dimms.getLibUtil().getFast().withFlags(true, true);
			ninja.coelho.arkjs.extern.bukkit.Bukkit bukkitctx = dimms.getBukkit();
			bukkitctx.runAsync(() -> {
				BlockVectorSet blockSet = new BlockVectorSet();

				// Prepare row.
				prepareCubeSet(blockSet, world, -1, xOrig, yOrig, zOrig,
						minX, minY, minZ, maxX, maxY, maxZ);

				int drillDozerCount = drillDozerLevel;
				if(hasMegaDrill) drillDozerCount += 1;
				if(hasCrushingBoots) drillDozerCount += 1;
				if(hasShockwaveChest) drillDozerCount += 1;
				if(drillDozerCount > 0) {
					for(int i = 0; i < drillDozerCount; i++){
						prepareCubeSet(blockSet, world, -1, xOrig, yOrig, zOrig,
								minX, minY+i, minZ, maxX, maxY+i, maxZ);
					}
				}
				if(hasMegaDrill) {
					int maxXDrill = mine.getMaxX(), minXDrill = mine.getMinX();
					int maxYDrill = yOrig, minYDrill = mine.getMinY();
					int maxZDrill = block.getZ(), minZDrill = block.getZ();

					int maxXDrill2 = block.getX(), minXDrill2 = block.getX();
					int maxYDrill2 = yOrig, minYDrill2 = mine.getMinY();
					int maxZDrill2 = mine.getMaxZ(), minZDrill2 = mine.getMinZ();
					prepareCubeSet(blockSet, world, -1, xOrig, yOrig, zOrig,
							minXDrill, minYDrill, minZDrill, maxXDrill, maxYDrill, maxZDrill);
					prepareCubeSet(blockSet, world, -1, xOrig, yOrig, zOrig,
							minXDrill2, minYDrill2, minZDrill2, maxXDrill2, maxYDrill2, maxZDrill2);
				}
				/*
				for (int x = minX; x <= maxX; x++) {
					for (int z = minZ; z <= maxZ; z++) {
						for (int y = minY; y <= maxY; y++) {
							if (x == xOrig && y == yOrig && z == zOrig) continue;

							Vector vector = Vector.toBlockPoint(x, y, z);
							Location relLoc = BukkitUtil.toLocation(world, vector);
							Mine mineAt = MRLUtils.getMine(relLoc);
							if (mineAt != null) blockSet.add(vector);
						}
					}
				}*/
				
				if (blockSet.isEmpty())return;

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

						totalBlocks++;

						Location relLoc = BukkitUtil.toLocation(world, vector);
						Block relBlock = relLoc.getBlock();
						if (localRng.nextBoolean())
							create(relLoc, PlayerUtils.getNearbyPlayers(relLoc, 8));

						List<ItemStack> drops = InventoryUtils.getInstance().executeAutoInv(p, relBlock);
						//InventoryUtils.getInstance().checkForAndRemoveData(relBlock);
						fastctx.blockSet(relBlock, 0, 0);

						if (drops != null && !drops.isEmpty()) {
							for (ItemStack item : drops) {
								long amount = item.getAmount();
								item.setAmount(1);
								dropCache.compute(item, (key, value) -> {
									if (value != null) {
										return value + amount;
									}
									return amount;
								});
							}
						}
					}
					List<ItemStack> optimizeDrops = new ArrayList(dropCache.size());
					for (Map.Entry<ItemStack, Long> entry : dropCache.entrySet()) {
						ItemStack item = entry.getKey();
						Long amount = entry.getValue();
						int amountRemainder = Math.max((int) Math.ceil(amount / Integer.MAX_VALUE), 0);
						if (amountRemainder <= 1) {
							item.setAmount(amount.intValue());
							optimizeDrops.add(item);
							continue;
						}
						while (amountRemainder-- > 0) {
							item.setAmount(amount.intValue());
							optimizeDrops.add(item);
							amount -= item.getAmount();
							if (amount <= 0) break;
						}
					}
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
	}
	
	public boolean hasDrillHammer(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR) return false;
		if (!(stack.hasItemMeta()) || !(stack.getItemMeta().hasLore())) return false;

		for (String s : stack.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("DrillHammer ")) return true;
		}
		return false;
	}

	private int getRange(String rangeKey)
	{
		if(data == null && !rangeKey.equals("animationRange")) return 3;
		if(data == null) return 8;
		return Integer.parseInt(String.valueOf(Math.round(data.getRanges().get(rangeKey))));
	}

	private long getTimer(String key) {
		return Long.parseLong(String.valueOf(data.getRanges().get(key)));
	}


}
