package org.pvpingmc.tokens.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.utils.BlockUtils;

public class OmniToolListener implements Listener {

	private final Set<Material> pickaxeMats = new HashSet();
	private final Set<Material> axeMats = new HashSet();
	private final Set<Material> shovelMats = new HashSet();
	private final Set<Material> toolMats = new HashSet();
	OPConfigController utils;
	OPEnchantData data;

	public OmniToolListener(OPConfigController utilsIn, OPEnchantData dataIn) {
		super();
		utils = utilsIn;
		data = dataIn;
	}

	public OmniToolListener() {
		for (Material material : Material.values()) {
			String name = material.name();
			if (name.endsWith("_PICKAXE")) {
				this.pickaxeMats.add(material);
			} else if (name.endsWith("_AXE")) {
				this.axeMats.add(material);
			} else if (name.endsWith("_SPADE")) {
				this.shovelMats.add(material);
			}
		}
		
		this.toolMats.addAll(this.pickaxeMats);
		this.toolMats.addAll(this.axeMats);
		this.toolMats.addAll(this.shovelMats);
		this.toolMats.add(Material.SHEARS);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onOmniTool(BlockDamageEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		if (player.getGameMode() != GameMode.SURVIVAL || !BlockUtils.getInstance().canBuild(player, block)) return;

		ItemStack item = player.getItemInHand();
		if (!hasOmniTool(item)) return;
		
		Material itemType = item.getType();
		if (!this.toolMats.contains(itemType)) return;
		
		ItemMeta itemMeta = item.getItemMeta();
		List<String> itemLore = itemMeta != null && itemMeta.hasLore() ? itemMeta.getLore() : null;

		boolean isShear = itemType == Material.SHEARS;
		String shearTypeLore = null;
		String toolName = itemType.name(), toolType = toolName.split("_")[0];
		if (isShear && itemLore != null) {
			String typePrefix = ChatColor.COLOR_CHAR + "8@type: ";
			shearTypeLore = itemLore.stream().filter(str -> str.startsWith(typePrefix)).findFirst().orElse(null);
			if (shearTypeLore == null) return;

			toolType = StringUtils.substringAfter(shearTypeLore, typePrefix);
		}
		
		Material blockType = block.getType();
		boolean changed = false;
		switch (blockType) {
			case LOG:
			case LOG_2:
			case WOOD_DOUBLE_STEP:
			case WOOD:
			case WOOD_DOOR:
			case WOOD_STAIRS:
			case WOOD_STEP:
			case WALL_SIGN:
			case SIGN_POST:
			case SIGN:
			case LADDER:
			case HUGE_MUSHROOM_1:
			case HUGE_MUSHROOM_2:
			case TRAPPED_CHEST:
			case CHEST:
			case TRAP_DOOR:
			case WORKBENCH:
			case FENCE_GATE:
			case FENCE:
			case MELON_BLOCK:
			case PUMPKIN:
			case JACK_O_LANTERN:
			case VINE: { 
				if (!this.axeMats.contains(itemType)) {
					item.setType(Material.matchMaterial(toolType + "_AXE"));
					changed = true;
				}
				break;
			}
			case GRAVEL:
			case DIRT:
			case GRASS:
			case SAND:
			case CLAY:
			case SNOW:
			case SNOW_BLOCK:
			case MYCEL:
			case SOUL_SAND:
			case LONG_GRASS:
			case RED_ROSE:
			case YELLOW_FLOWER:
			case DOUBLE_PLANT: { 
				if (!this.shovelMats.contains(itemType)) {
					item.setType(Material.matchMaterial(toolType + "_SPADE"));
					changed = true;
				}
				break;
			}
			case LEAVES:
			case LEAVES_2:
			case WEB:
			case CARPET:
			case WOOL: { 
				if (!isShear) {
					if (itemLore == null) {
						itemLore = new ArrayList();
					}
					itemLore.add(ChatColor.COLOR_CHAR + "8@type: " + toolType);
					itemMeta.setLore(itemLore);
					item.setItemMeta(itemMeta);
					item.setType(Material.SHEARS);
					changed = true;
				}
				break;
			}
			default: {
				if (blockType != Material.AIR && !this.pickaxeMats.contains(itemType)) {
					item.setType(Material.matchMaterial(toolType + "_PICKAXE"));
					changed = true;
				}
				break;
			}
		}
		if (changed) {
			if (shearTypeLore != null) {
				itemLore.remove(shearTypeLore);
				itemMeta.setLore(itemLore);
				item.setItemMeta(itemMeta);
			}
			if (itemMeta.getEnchantLevel(Enchantment.DIG_SPEED) > 5) {
				displayBlockEffect(block.getLocation(), blockType.getId(), block.getData());
				e.setInstaBreak(true);
			}
		}
	}
	
	private void displayBlockEffect(Location location, int... data) {
		location = location.clone();
		location.setX(location.getBlockX() + .5);
		location.setY(location.getBlockY() + .5);
		location.setZ(location.getBlockZ() + .5);
		location.getWorld().spigot().playEffect(location, Effect.TILE_BREAK, data[0], data[1], .35F, .2F, .35F, .5F, 50, 8);
	}

	public boolean hasOmniTool(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR) return false;
		if (!stack.hasItemMeta() || !stack.getItemMeta().hasLore()) return false;

		for (String s : stack.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("OmniTool ")) return true;
		}
		return false;
	}
}
