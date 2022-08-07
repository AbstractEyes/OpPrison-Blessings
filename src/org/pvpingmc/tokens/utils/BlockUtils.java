package org.pvpingmc.tokens.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.pvpingmc.tokens.Main;

public class BlockUtils {

	private static BlockUtils instance;

	public static BlockUtils getInstance() {
		if (instance == null) {
			synchronized (BlockUtils.class) {
				if (instance == null) {
					instance = new BlockUtils();
				}
			}
		}

		return instance;
	}

	public boolean canBuild(Player player, Block block) {
		return canBuild(player, block.getLocation());
	}

	public boolean canBuild(Player player, Location location) {
		return Main.getInstance().getWgPlugin().canBuild(player, location);
	}

	public void cacheExp(Player player, Block block, boolean checkCanBuild) {
		cacheExp(player, block.getLocation(), block.getType(), checkCanBuild);
	}

	public void cacheExp(Player player, Location location, Material blockType, boolean checkCanBuild) {
		if (checkCanBuild && !(canBuild(player, location))) {
			return;
		}

		int exp = getExpDrop(player, blockType);
		if (exp > 0) {
			player.giveExp(exp);
		}
	}

	public int getExpDrop(Player player, Material blockType) {
		ItemStack handStack = player.getItemInHand();
		if (handStack != null && handStack.containsEnchantment(Enchantment.SILK_TOUCH)) {
			return 0;
		}

		int amount = 0;
		ThreadLocalRandom random = ThreadLocalRandom.current();

		if (blockType == Material.MOB_SPAWNER) {
			amount = 15 + random.nextInt(15) + random.nextInt(15);
		} else if (blockType == Material.REDSTONE_ORE || blockType == Material.GLOWING_REDSTONE_ORE) {
			amount = 1 + random.nextInt(5);
		} else if (blockType == Material.COAL_ORE) {
			amount = random.nextInt(0, 2);
		} else if (blockType == Material.DIAMOND_ORE) {
			amount = random.nextInt(3, 7);
		} else if (blockType == Material.EMERALD_ORE) {
			amount = random.nextInt(3, 7);
		} else if (blockType == Material.LAPIS_ORE) {
			amount = random.nextInt(2, 5);
		} else if (blockType == Material.QUARTZ_ORE) {
			amount = random.nextInt(2, 5);
		}

		return Math.max(0, amount);
	}

	public void cacheCall(Player player, Block block) {
		if (!(canBuild(player, block))) {
			return;
		}

		Main.getInstance().getBreakHandler().breakBlock(player, block);
	}
}
