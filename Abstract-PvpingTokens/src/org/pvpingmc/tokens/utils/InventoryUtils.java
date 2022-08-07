package org.pvpingmc.tokens.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class InventoryUtils {
	
	private static InventoryUtils instance;

	public static InventoryUtils getInstance() {
		if (instance == null) {
			synchronized (InventoryUtils.class) {
				if (instance == null) {
					instance = new InventoryUtils();
				}
			}
		}

		return instance;
	}
	
	public Predicate<Block> blockMarkerPtr = block -> true;
	public DropIntercept dropIntercept = (block, player) -> {
		Material type = block.getType();
		ItemStack handStack = player.getItemInHand();
		Map<Enchantment, Integer> handEnchants = handStack != null ? handStack.getEnchantments() : null;

		int fortuneLvl = handEnchants != null ? handEnchants.getOrDefault(Enchantment.LOOT_BONUS_BLOCKS, 0) : 0;
		int returnAmount = fortuneLvl > 0 && this.blockMarkerPtr.test(block) ? getDropCount(type, fortuneLvl) : count(type);

		if (handEnchants.containsKey(Enchantment.SILK_TOUCH) || type == Material.STONE
				|| type == Material.COBBLESTONE || type == Material.STAINED_CLAY || type == Material.BEACON
				|| type == Material.DOUBLE_STEP) {
			return Collections.singletonList(new ItemStack(type, 1, block.getData()));
		} else if (isOre(type)) {
			MaterialData data = getOre(type);
			return Collections.singletonList(new ItemStack(data.getItemType(), returnAmount, data.getData()));
		} else if (!(block.getDrops().isEmpty())) {
			ItemStack dropItem = block.getDrops().iterator().next();
			return Collections.singletonList(new ItemStack(dropItem.getType(), returnAmount, dropItem.getDurability()));
		}
		return null;
	};
	
    public interface DropIntercept {
    	Object accept(Block block, Player player);
    }
	
	private final String blockPlaceKey = "BLOCK_PLACE_DATA";
	private final Set<Material> oreCache = new HashSet();
	
	private InventoryUtils() {
		for (Material material : Material.values()) {
			if (material.name().endsWith("_ORE")) this.oreCache.add(material);
		}
	}
	
	public int getDropCount(Material blockType, int i) {
		if (blockType == Material.SPONGE || blockType == Material.BEACON || blockType == Material.DRAGON_EGG) {
			return 1;
		}
		if (i > 0) {
			int round = (i <= 6 ? i : (int) (i * 0.2));
			
			int j = (ThreadLocalRandom.current().nextInt(round + 2) - 1);
			if (j < 0) {
				j = 0;
			}
			return count(blockType) * (j + 1);
		}
		return count(blockType);
	}

	public int count(Material blockType) {
		return blockType == Material.LAPIS_ORE || blockType == Material.REDSTONE_ORE || blockType == Material.GLOWING_REDSTONE_ORE ? 4 + ThreadLocalRandom.current().nextInt(5) : 1;
	}
	
	@SuppressWarnings("deprecation")
	public List<ItemStack> executeAutoInv(Player player, Block block) {
		if (!(BlockUtils.getInstance().canBuild(player, block))) {
			return null;
		}
		if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
			Material type = block.getType();
			try {
				if(type == Material.BEDROCK) type = Material.EMERALD_BLOCK;
				player.incrementStatistic(Statistic.MINE_BLOCK, type);
				BlockUtils.getInstance().cacheExp(player, block, false);
			} finally {
				Object toParse = this.dropIntercept.accept(block, player);
				if (toParse == null) {
					return null;
				} else if (toParse instanceof List) {
					return (List<ItemStack>) toParse;
				} else if (toParse.getClass().isArray()) {
					return Arrays.asList((ItemStack[]) toParse);
				} else if (toParse instanceof ItemStack) {
					return Collections.singletonList((ItemStack) toParse);
				}
			}
		}
		return null;
	}

	public boolean isOre(Material data) {
		return !this.oreCache.isEmpty() && this.oreCache.contains(data);
	}
	
	@SuppressWarnings("deprecation")
	public MaterialData getOre(Material data) {
		if (data == Material.COAL_ORE) {
			return new MaterialData(Material.COAL);
		} else if (data == Material.DIAMOND_ORE) {
			return new MaterialData(Material.DIAMOND);
		} else if (data == Material.GLOWING_REDSTONE_ORE || data == Material.REDSTONE_ORE) {
			return new MaterialData(Material.REDSTONE);
		} else if (data == Material.GOLD_ORE) {
			return new MaterialData(Material.GOLD_INGOT);
		} else if (data == Material.IRON_ORE) {
			return new MaterialData(Material.IRON_INGOT);
		} else if (data == Material.LAPIS_ORE) {
			return new MaterialData(Material.INK_SACK, (byte) 4);
		} else if (data == Material.QUARTZ_ORE) {
			return new MaterialData(Material.QUARTZ);
		} else if (data == Material.EMERALD_ORE) {
			return new MaterialData(Material.EMERALD);
		} else {
			return null;
		}
	}
}
