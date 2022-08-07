package org.pvpingmc.tokens.tokens;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.pvpingmc.tokens.Main;
import org.pvpingmc.tokens.menu.InventoryClickType;
import org.pvpingmc.tokens.menu.Menu;
import org.pvpingmc.tokens.menu.MenuAPI;
import org.pvpingmc.tokens.menu.MenuItem;
import org.pvpingmc.tokens.utils.BackpackUtils;
import org.pvpingmc.tokens.utils.CleanItem;
import org.pvpingmc.tokens.utils.Sounds;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

public class GUI {
	
	private static GUI instance;

	public static GUI getInstance() {
		if (instance == null) {
			synchronized (GUI.class) {
				if (instance == null) {
					instance = new GUI();
				}
			}
		}

		return instance;
	}
	
	@Getter @Setter private MenuItem cacheSpacerYellow, cacheSpacerGold;
	
	private GUI() {
		setCacheSpacerYellow(new MenuItem.UnclickableMenuItem(new CleanItem(Material.STAINED_GLASS_PANE).withDurability((short) 4).withName(" ").toItemStack()));
		setCacheSpacerGold(new MenuItem.UnclickableMenuItem(new CleanItem(Material.STAINED_GLASS_PANE).withDurability((short) 1).withName(" ").toItemStack()));
	}
	
	public Menu downgradeToolMenu(ItemStack stack) {		
		Map<String, Integer> toolEnchants = Maps.newHashMap();
		if (stack != null && stack.hasItemMeta()) {
			ItemMeta meta = stack.getItemMeta();
			if (meta.hasEnchants()) {
				for (Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
					toolEnchants.put(entry.getKey().getName().toLowerCase(), entry.getValue());
				}
			}
			
			if (meta.hasLore()) {
				for (String lore : meta.getLore()) {
					if (lore.startsWith(ChatColor.COLOR_CHAR + "7") && lore.contains(" ")) {
						lore = ChatColor.stripColor(lore);
						
						String[] parts = lore.split(" ");
						if (parts.length == 2) {
							try {
								String name = parts[0];
								int level = Integer.parseInt(parts[1]);

								toolEnchants.put(name.toLowerCase(), level);
							} catch (Exception ignore) {
								continue;
							}
						}
					}
				}
			}
		}
		
		if (toolEnchants == null || toolEnchants.isEmpty()) {
			return null;
		}

		Menu menu = MenuAPI.getMenuAPI().createMenu("§8§lDOWNGRADE TOOL", 4);
		AtomicInteger atomicClickedSlot = new AtomicInteger(-1);

		MenuItem haste = new MenuItem(new CleanItem((toolEnchants.containsKey("haste") ? Material.REDSTONE : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Haste").withLores("&7&o\"Allows you to swing your pickaxe faster while mining.\"", " ", (toolEnchants.containsKey("haste") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 10) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Haste Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(10);
					return;
				}
				
				if (toolEnchants.containsKey("haste")) {
					p.closeInventory();
					
					removeEnchant(p, "Haste", true);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
					

				}
			}
		};
		menu.addMenuItem(haste, 10);
				
		MenuItem speed = new MenuItem(new CleanItem((toolEnchants.containsKey("speed") ? Material.SUGAR : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Speed").withLores("&7&o\"Allows you to move faster while mining.\"", " ", (toolEnchants.containsKey("speed") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 11) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Speed Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(11);
					return;
				}
				
				if (toolEnchants.containsKey("speed")) {
					p.closeInventory();
					
					removeEnchant(p, "Speed", true);

					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(speed, 11);
		
		MenuItem nightvision = new MenuItem(new CleanItem((toolEnchants.containsKey("nightvision") ? Material.EYE_OF_ENDER : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Night Vision").withLores("&7&o\"Allows you to see in the dark while mining.\"", " ", (toolEnchants.containsKey("nightvision") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 12) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Night Vision Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(12);
					return;
				}
				
				if (toolEnchants.containsKey("nightvision")) {
					p.closeInventory();
					
					removeEnchant(p, "NightVision", true);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(nightvision, 12);
				
		MenuItem explosive = new MenuItem(new CleanItem((toolEnchants.containsKey("explosive") ? Material.TNT : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Explosive").withLores("&7&o\"Explodes nearby blocks while mining.\"", " ", (toolEnchants.containsKey("explosive") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 13) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Explosive Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(13);
					return;
				}
				
				if (toolEnchants.containsKey("explosive")) {
					p.closeInventory();
					
					removeEnchant(p, "Explosive", false);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(explosive, 13);

		MenuItem efficiency = new MenuItem(new CleanItem((toolEnchants.containsKey("dig_speed") ? Material.DIAMOND_PICKAXE : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Efficiency").withLores("&7&o\"Allows you to mine faster.\"", " ", (toolEnchants.containsKey("dig_speed") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 14) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Efficiency Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(14);
					return;
				}
				
				if (toolEnchants.containsKey("dig_speed")) {
					p.closeInventory();
										
					if (p.getItemInHand().containsEnchantment(Enchantment.DIG_SPEED)) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l- LVL " + p.getItemInHand().getEnchantmentLevel(Enchantment.DIG_SPEED) + " EFFICIENCY"));
						p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1.0F, 1.5F);
						
						p.getItemInHand().removeEnchantment(Enchantment.DIG_SPEED);
					}
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(efficiency, 14);
				
		MenuItem fortune = new MenuItem(new CleanItem((toolEnchants.containsKey("loot_bonus_blocks") ? Material.DIAMOND : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Fortune").withLores("&7&o\"Allows you to receive more blocks while mining.\"", " ", (toolEnchants.containsKey("loot_bonus_blocks") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 15) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Fortune Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(15);
					return;
				}
				
				if (toolEnchants.containsKey("loot_bonus_blocks")) {
					p.closeInventory();
										
					if (p.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l- LVL " + p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) + " FORTUNE"));
						p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1.0F, 1.5F);
						
						p.getItemInHand().removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
					}
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(fortune, 15);
				
		MenuItem autoMine = new MenuItem(new CleanItem((toolEnchants.containsKey("automine") ? Material.MINECART : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Auto Mine").withLores("&7&o\"Automatically mines nearby blocks while idle or mining.\"", " ", (toolEnchants.containsKey("automine") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 16) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Auto Mine Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(16);
					return;
				}
				
				if (toolEnchants.containsKey("automine")) {
					p.closeInventory();
					
					removeEnchant(p, "AutoMine", true);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(autoMine, 16);
		
		MenuItem drillHammer = new MenuItem(new CleanItem((toolEnchants.containsKey("drillhammer") ? Material.HOPPER : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Drill Hammer").withLores("&7&o\"Chance to break a full layer of blocks in a mine.\"", " ", (toolEnchants.containsKey("drillhammer") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 19) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Drill Hammer Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(19);
					return;
				}
				
				if (toolEnchants.containsKey("drillhammer")) {
					p.closeInventory();
					
					removeEnchant(p, "DrillHammer", false);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(drillHammer, 19);
				
		MenuItem looter = new MenuItem(new CleanItem((toolEnchants.containsKey("looter") ? Material.DEAD_BUSH : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Looter").withLores("&7&o\"Chance to find tokens while mining blocks.\"", " ", (toolEnchants.containsKey("looter") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 20) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Looter Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(20);
					return;
				}
				
				if (toolEnchants.containsKey("looter")) {
					p.closeInventory();
					
					removeEnchant(p, "Looter", false);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(looter, 20);
		
		MenuItem omnitool = new MenuItem(new CleanItem((toolEnchants.containsKey("omnitool") ? Material.PISTON_BASE : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove OmniTool").withLores("&7&o\"Automatically switches your tool to the more suitable tool for the job.\"", " ", (toolEnchants.containsKey("omnitool") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 21) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "OmniTool Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(21);
					return;
				}
				
				if (toolEnchants.containsKey("omnitool")) {
					p.closeInventory();
					
					removeEnchant(p, "OmniTool", false);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(omnitool, 21);
				
		MenuItem experience = new MenuItem(new CleanItem((toolEnchants.containsKey("experience") ? Material.EXP_BOTTLE : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Experience").withLores("&7&o\"Boosts Experience from Mobs & Blocks.\"", " ", (toolEnchants.containsKey("experience") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!isTool(p.getItemInHand().getType()) && !isWeapon(p.getItemInHand().getType())) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool / sword in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 22) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Experience Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(22);
					return;
				}
				
				if (toolEnchants.containsKey("experience")) {
					p.closeInventory();
					
					removeEnchant(p, "Experience", false);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(experience, 22);

		MenuItem luckyDuck = new MenuItem(new CleanItem((toolEnchants.containsKey("luckyduck") ? Material.BOOK : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Lucky Duck").withLores("&7&o\"Increases your chance to find a Lucky Chest\"", " ", (toolEnchants.containsKey("luckyduck") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 23) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Lucky Duck Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(23);
					return;
				}
				
				if (toolEnchants.containsKey("luckyduck")) {
					p.closeInventory();
					
					removeEnchant(p, "LuckyDuck", false);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(luckyDuck, 23);

		MenuItem doubleTrouble = new MenuItem(new CleanItem((toolEnchants.containsKey("doubletrouble") ? Material.DIAMOND_BLOCK : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Double Trouble").withLores("&7&o\"Chance to double your blocks sold\"", " ", (toolEnchants.containsKey("doubletrouble") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 24) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Double Trouble Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(24);
					return;
				}
				
				if (toolEnchants.containsKey("doubletrouble")) {
					p.closeInventory();
					
					removeEnchant(p, "DoubleTrouble", false);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(doubleTrouble, 24);

		MenuItem beaconBlast = new MenuItem(new CleanItem((toolEnchants.containsKey("beaconblast") ? Material.BEACON : Material.BARRIER)).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Remove Beacon Blast").withLores("&7&o\"Chance to spawn a beacon in your inventory\"", " ", (toolEnchants.containsKey("beaconblast") ? "§a§lCLICK TO REMOVE ENCHANT" : "§c§lUNAVAILABLE")).toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (getIcon() != null && getIcon().getType() == Material.BARRIER) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Your item doesn't contain this enchant currently...");
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold something in hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (!(isTool(p.getItemInHand().getType()))) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must have a tool in hand!");
					p.closeInventory();
					return;
				}
				
				int lastSlot = atomicClickedSlot.get();
				if (lastSlot == -1 || lastSlot != 25) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You must click " + Main.getInstance().getSecondary() + "once more" + Main.getInstance().getPrimary() + " to confirm removing your " + Main.getInstance().getSecondary() + "Beacon Blast Enchant" + Main.getInstance().getPrimary() + "!");
					p.playSound(p.getLocation(), Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.5F);
					
					atomicClickedSlot.set(25);
					return;
				}
				
				if (toolEnchants.containsKey("beaconblast")) {
					p.closeInventory();
					
					removeEnchant(p, "BeaconBlast", false);
					
					Menu downgradeMenu = downgradeToolMenu(p.getItemInHand());
					if (downgradeMenu != null) {
						downgradeMenu.openMenu(p);
					} else {
						Bukkit.dispatchCommand(p, "tokenshop enchants");
					}
				}
			}
		};
		menu.addMenuItem(beaconBlast, 25);

		for (int i = 0; i < menu.getInventory().getSize(); i++) {
			if (menu.getInventory().getItem(i) == null) {
				menu.addMenuItem(getCacheSpacerYellow(), i);
			}
		}
		
		return menu;
	}
	
	public Menu backpackMenu(Player p) {
		Menu menu = MenuAPI.getMenuAPI().createMenu("§8§lBACKPACKS", 3);
		
		MenuItem newBackpack = new MenuItem(new CleanItem(Material.CHEST).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Purchase Backpack").withLores("&7&o\"Extra inventory inside of a backpack.\"", " ", "  " + Main.getInstance().getSecondary() + "&l* " + Main.getInstance().getPrimary() + "Cost: " + Main.getInstance().getSecondary() + "3250 Token(s)", "  " + Main.getInstance().getSecondary() + "&l* " + Main.getInstance().getPrimary() + "Capacity: " + Main.getInstance().getSecondary() + "500 Item(s)").toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (TokenStorage.getInstance().getTokens(p) < 3250) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You need at least " + Main.getInstance().getSecondary() + "3250 Token(s)" + Main.getInstance().getPrimary() + " to buy this.");
					p.closeInventory();
					return;
				}
				
				if (Bukkit.getPluginManager().isPluginEnabled("PvPingBackpacks")) {
					TokenStorage.getInstance().takeTokens(p, 3250);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l-" + NumberFormat.getInstance().format(3250) + " TOKENS"));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l+1 (x500 Capacity) Backpack"));
					p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1.0F, 1.5F);
					
					p.getInventory().addItem(BackpackUtils.getNewBackpack(500));
				} else {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Can't find the backpacks plugin, please report this to staff...");
				}
			}
		};
		menu.addMenuItem(newBackpack, 12);
		
		MenuItem upgradeBackpack = new MenuItem(new CleanItem(Material.ENDER_CHEST).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Upgrade Backpack").withLores("&7&o\"Upgrade capacity of your current backpack.\"", " ", "  " + Main.getInstance().getSecondary() + "&l* " + Main.getInstance().getPrimary() + "Cost: " + Main.getInstance().getSecondary() + "525 Token(s)", "  " + Main.getInstance().getSecondary() + "&l* " + Main.getInstance().getPrimary() + "Capacity: " + Main.getInstance().getSecondary() + "+75 Item(s)").toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (TokenStorage.getInstance().getTokens(p) < 525) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You need at least " + Main.getInstance().getSecondary() + "525 Token(s)" + Main.getInstance().getPrimary() + " to buy this.");
					p.closeInventory();
					return;
				}
				
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold a backpack in your hand to upgrade!");
					p.closeInventory();
					return;
				}
				
				if (Bukkit.getPluginManager().isPluginEnabled("PvPingBackpacks")) {
					ItemStack stack = p.getItemInHand();
					if (!(BackpackUtils.isBackpack(stack))) {
						p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Must hold a backpack in your hand to upgrade!");
						p.closeInventory();
						return;
					}
					
					TokenStorage.getInstance().takeTokens(p, 525);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l-" + NumberFormat.getInstance().format(525) + " TOKENS"));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l+75 Item(s) Capacity on Backpack"));
					p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1.0F, 1.5F);
					
					p.setItemInHand(BackpackUtils.upgradeBackpack(stack, 75));
					p.updateInventory();
				} else {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "Can't find the backpacks plugin, please report this to staff...");
				}
			}
		};
		menu.addMenuItem(upgradeBackpack, 14);

		for (int i = 0; i < menu.getInventory().getSize(); i++) {
			if (menu.getInventory().getItem(i) == null) {
				menu.addMenuItem(getCacheSpacerYellow(), i);
			}
		}
		
		return menu;
	}
	
	public Menu explosives() {
		Menu menu = MenuAPI.getMenuAPI().createMenu("§8§lEXPLOSIVES", 3);

		menu.addMenuItem(explosiveItem(1, 5), 10);
		menu.addMenuItem(explosiveItem(2, 10), 11);
		menu.addMenuItem(explosiveItem(3, 15), 12);
		menu.addMenuItem(explosiveItem(4, 20), 13);
		menu.addMenuItem(explosiveItem(5, 25), 14);
		menu.addMenuItem(explosiveItem(6, 30), 15);
		menu.addMenuItem(explosiveItem(7, 35), 16);

		for (int i = 0; i < menu.getInventory().getSize(); i++) {
			if (menu.getInventory().getItem(i) == null) {
				menu.addMenuItem(getCacheSpacerYellow(), i);
			}
		}
		
		return menu;
	}
	
	private MenuItem explosiveItem(final int level, final int cost) {
		MenuItem explosiveItem = new MenuItem(new CleanItem(Material.EXPLOSIVE_MINECART).glow().withName(Main.getInstance().getPrefix() + Main.getInstance().getSecondary() + "Explosive " + Main.getInstance().getSecondary() + "§l[LVL " + level + "]").withLores("&7&o\"Spawns a TNT that explodes nearby blocks.\"", " ", "  " + Main.getInstance().getSecondary() + "&l* " + Main.getInstance().getPrimary() + "Cost: " + Main.getInstance().getSecondary() + Integer.toString(cost) + " Token(s)").toItemStack()) {

			@Override
			public void onClick(Player p, InventoryClickType click) {
				if (TokenStorage.getInstance().getTokens(p) < cost) {
					p.sendMessage(Main.getInstance().getPrefix() + Main.getInstance().getPrimary() + "You need at least " + Main.getInstance().getSecondary() + cost + " Token(s)" + Main.getInstance().getPrimary() + " to buy this.");
					p.closeInventory();
					return;
				}

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pexp give " + p.getName() + " " + level + " 1");
				TokenStorage.getInstance().takeTokens(p, cost);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l-" + NumberFormat.getInstance().format(cost) + " TOKENS"));
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l+1 EXPLOSIVE [LVL " + level + "]"));
				p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1.0F, 1.5F);
			}
		};

		return explosiveItem;
	}
	
	private void removeEnchant(Player p, String enchant, boolean update) {
		ItemStack i = p.getItemInHand();
		if (i == null) {
			return;
		}
		
		ItemMeta im = i.getItemMeta();
		if (i.hasItemMeta() && im.hasLore()) {
			List<String> lore = im.getLore();
			boolean contains = false;
			String found = "";
			int index = 0;

			for (int l = 0; l < lore.size(); l++) {
				String line = lore.get(l);
				String stripLine = ChatColor.stripColor(line);
				if (stripLine.startsWith(enchant + " ")) {
					contains = true;
					found = line;
					index = l;
					break;
				}
			}
			
			if (contains && found != null) {
				String stripFound = ChatColor.stripColor(found);
				String levelStr = stripFound.split(" ")[1];
				if (!(isInt(levelStr))) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l- LVL 1 " + enchant.toUpperCase()));
					p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1.0F, 1.5F);
					
					lore.remove(index);
					im.setLore((lore.isEmpty() ? null : lore));
					i.setItemMeta(im);

					if (update) {
						PlayerItemHeldEvent event = new PlayerItemHeldEvent(p, p.getInventory().getHeldItemSlot(), p.getInventory().getHeldItemSlot());
						Bukkit.getPluginManager().callEvent(event);
					}
					return;
				}
				
				int updated = Integer.parseInt(levelStr);
				
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l- LVL " + updated + " " + enchant.toUpperCase()));
				p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1.0F, 1.5F);
				
				lore.remove(index);
				im.setLore((lore.isEmpty() ? null : lore));
				i.setItemMeta(im);
				
				if (update) {
					PlayerItemHeldEvent event = new PlayerItemHeldEvent(p, p.getInventory().getHeldItemSlot(), p.getInventory().getHeldItemSlot());
					Bukkit.getPluginManager().callEvent(event);
				}
				return;
			}
		}
	}

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean isTool(Material m) {
		String name = m.name();
		return name.contains("_PICKAXE") || name.contains("_AXE") || name.contains("_SPADE");
	}
	
	public boolean isWeapon(Material m) {
		return m.name().contains("_SWORD");
	}
}
