package org.pvpingmc.tokens.listeners;

import com.abstractphil.enchantments.AbstractEnchantments;
import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.cfg.PassivesData;
import com.abstractphil.enchantments.controller.ImbuedController;
import com.abstractphil.enchantments.controller.PassivesController;
import com.redmancometh.reditems.RedItems;
import com.redmancometh.reditems.mediator.EnchantManager;
import com.redmancometh.reditems.storage.EnchantData;
import net.minecraft.server.v1_8_R3.Tuple;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.event.TokenEarnEvent;

import java.text.NumberFormat;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.pvpingmc.tokens.Main;
import org.pvpingmc.tokens.tokens.TokenStorage;
import org.pvpingmc.tokens.utils.MRLUtils;
import org.pvpingmc.tokens.utils.Sounds;

import com.koletar.jj.mineresetlite.Mine;

import javax.annotation.Nullable;

public class LooterListener implements Listener {
	OPConfigController utils;
	OPEnchantData data;

	public LooterListener(OPConfigController utilsIn, OPEnchantData dataIn) {
		super();
		utils = utilsIn;
		data = dataIn;
	}

	private static final String DATA_KEY = "LOOTER-COOLDOWN";

	private double getMultiplier(String key)
	{
		return data.getMultipliers().get(key);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onLooter(BlockBreakEvent e) {
		Block b = e.getBlock();
		String worldName = b.getWorld().getName().toLowerCase();
		if (Main.getInstance().getDisableWorlds().contains(worldName)) return;
		
		Player p = e.getPlayer();
		if (p.hasMetadata(DATA_KEY) && !(p.getMetadata(DATA_KEY).isEmpty()) && p.getMetadata(DATA_KEY).get(0).asLong() > System.currentTimeMillis()) return;
		
		Mine mine = MRLUtils.getMine(b);
		if (mine == null) return;
		
		Location loc = b.getLocation();
		ItemStack item = p.getItemInHand();
		if (p.getGameMode() == GameMode.CREATIVE || p.getInventory().firstEmpty() == -1) return;

		int level = 0;
		if(!hasLooter(item) && (
				item.getType() == Material.DIAMOND_PICKAXE ||
				item.getType() == Material.DIAMOND_SPADE ||
				item.getType() == Material.DIAMOND_AXE ||
				item.getType() == Material.DIAMOND_HOE))
		{
			// If looter doesn't exist, give looter level 1.
			executeLooter(p, 1);
		} else {
			for (String s : item.getItemMeta().getLore()) {
				String stripStr = ChatColor.stripColor(s);
				if (stripStr.startsWith("Looter ")) {
					try {
						level = Integer.parseInt(stripStr.split(" ")[1]);
						if (Main.MAYHEM_ENCHS) {
							level *= 4;
						}
					} catch (NumberFormatException ignore) {
						break;
					}
					if (level < 1) {
						break;
					}
					executeLooter(p, level);
					break;
				}
			}
		}
		p.setMetadata(DATA_KEY, new FixedMetadataValue(Main.getInstance(), System.currentTimeMillis() + 500L));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDisable(PluginDisableEvent e) {
		Plugin plugin = e.getPlugin();
		if (plugin.getDescription().getName().equalsIgnoreCase("PvPingTokens")) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasMetadata(DATA_KEY)) p.removeMetadata(DATA_KEY, Main.getInstance());
			}
		}		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		
		if (p.hasMetadata(DATA_KEY)) p.removeMetadata(DATA_KEY, Main.getInstance());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		if (p.hasMetadata(DATA_KEY)) p.removeMetadata(DATA_KEY, Main.getInstance());
	}

	@Nullable
	private Tuple<Integer, PassivesData> getPassiveLevel(Player player, String passiveName) {
		PassivesController passives = AbstractEnchantments.getInstance().passivesController();
		ItemStack passiveItem = passives.getPassiveItem(player);
		if(passiveItem == null) return null;
		int level = passives.getPassiveLevel(passiveItem, passiveName);
		return new Tuple<>(level, passives.getPassiveData(passiveName));
	}

	@Nullable
	private EnchantData getEquippedImbue(Player player, String imbueName) {
		ImbuedController imbued = AbstractEnchantments.getInstance().imbuedController();
		return imbued.getEffectData(player, imbueName);
	}

	private int getAmountOfTokens(Player player, int level) {
		double levelChanceLevelComparator = getMultiplier("level-chance-level-comparator");
		double levelChanceMaxComparator = getMultiplier("level-chance-max-comparator");
		double levelChanceMinComparator = getMultiplier("level-chance-min-comparator");
		double levelChance = Math.min(Math.max(level * levelChanceLevelComparator, levelChanceMaxComparator), levelChanceMinComparator);
		if (Math.random() > levelChance) {
			return 0;
		}
		double minimumBonus = 0;
		double maximumBonus = 0;
		EnchantData kickdown = getEquippedImbue(player, "kickdown");
		EnchantData trinket = getEquippedImbue(player, "trinket");
		if(kickdown != null) minimumBonus = 0.10;
		if(trinket != null) maximumBonus = 0.10;
		double levelAmountMin = getMultiplier("level-amount-min") + minimumBonus;
		double levelAmountMinAdd = getMultiplier("level-amount-min-add") + minimumBonus * 20;
		double levelAmountMax = getMultiplier("level-amount-max") + maximumBonus;
		double levelAmountMaxAdd = getMultiplier("level-amount-max-add") + maximumBonus * 20;
		double tokenBaronMult = 1;
		Tuple<Integer, PassivesData> baron = getPassiveLevel(player, "tokenbaron");
		if(baron != null) tokenBaronMult = 1 + baron.a() * baron.b().getChancePerLevel();
		int levelMin = (int) Math.ceil((level * levelAmountMin * tokenBaronMult) + levelAmountMinAdd);
		int levelMax = (int) Math.ceil((level * levelAmountMax * tokenBaronMult) + levelAmountMaxAdd);
		return ThreadLocalRandom.current().nextInt(levelMin, levelMax + 1);
	}
	
	private void executeLooter(Player p, int level) {
		int random = getAmountOfTokens(p, level);
		if (random <= 0) return;

		Bukkit.getServer().getPluginManager().callEvent(new TokenEarnEvent(p, random));

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l+" + NumberFormat.getInstance().format(random) + " " + (random == 1 ? "TOKEN" : "TOKENS")));
		p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1.0F, 1.5F);

		TokenStorage.getInstance().giveTokens(p, random);
	}

	public boolean hasLooter(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR) return false;
		if (!(stack.hasItemMeta()) || !(stack.getItemMeta().hasLore())) return false;


		for (String s : stack.getItemMeta().getLore()) {
			String stripStr = ChatColor.stripColor(s);
			if (stripStr.startsWith("Looter ")) return true;
		}
		return false;
	}
}
