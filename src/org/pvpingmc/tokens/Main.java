package org.pvpingmc.tokens;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.listeners.AntiCobbleListener;
import org.pvpingmc.tokens.listeners.AutoMineListener;
import org.pvpingmc.tokens.listeners.DrillHammerListener;
import org.pvpingmc.tokens.listeners.ExperienceListener;
import org.pvpingmc.tokens.listeners.ExplosiveListener;
import org.pvpingmc.tokens.listeners.LooterListener;
import org.pvpingmc.tokens.listeners.OmniToolListener;
import org.pvpingmc.tokens.listeners.PotionEffectsListener;
import org.pvpingmc.tokens.listeners.UnbreakableListener;
import org.pvpingmc.tokens.listeners.WithdrawalListener;
import org.pvpingmc.tokens.menu.Menu;
import org.pvpingmc.tokens.menu.MenuAPI;
import org.pvpingmc.tokens.nms.BreakHandler;
import org.pvpingmc.tokens.nms.reflection.BreakHandler_Reflection;
import org.pvpingmc.tokens.nms.v1_8_R3.BreakHandler_1_8_R3;
import org.pvpingmc.tokens.tasks.ExplosiveParticleTask;
import org.pvpingmc.tokens.tokens.GUI;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import lombok.Getter;
import lombok.Setter;
import ninja.coelho.dimm.DIMMPlugin;

public class Main extends JavaPlugin {

	private static Main finalInstance;

	public static boolean MAYHEM_ENCHS = false;

	@Getter @Setter private WorldGuardPlugin wgPlugin;
	@Getter @Setter private DIMMPlugin dimmPlugin;
	@Getter @Setter private BreakHandler breakHandler;
	@Getter @Setter private ExplosiveParticleTask particleTask;
	@Getter @Setter private Set<String> disableWorlds;
	@Getter @Setter private Set<Integer> ignoredMats;
	@Getter @Setter private boolean mrlEnabled;
	@Getter @Setter private OPConfigController cfgController;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		finalInstance = this;

		setDisableWorlds(new HashSet());
		getDisableWorlds().addAll(getConfig().getStringList("options.disabled-worlds"));
		setIgnoredMats(new HashSet());
		getIgnoredMats().addAll(Arrays.asList(Material.AIR.getId(), Material.DISPENSER.getId(),
				Material.BURNING_FURNACE.getId(), Material.FURNACE.getId(), Material.CHEST.getId(),
				Material.TRAPPED_CHEST.getId(), Material.ENDER_CHEST.getId(), Material.BEACON.getId(),
				Material.MOB_SPAWNER.getId(), Material.ANVIL.getId(), Material.HOPPER.getId(), Material.DROPPER.getId(),
				Material.SIGN.getId(), Material.SIGN_POST.getId(), Material.WALL_SIGN.getId(),
				Material.IRON_DOOR_BLOCK.getId(), Material.TRAP_DOOR.getId(), Material.BED_BLOCK.getId(),
				Material.BREWING_STAND.getId(), Material.SKULL.getId(), Material.SOIL.getId(), Material.CROPS.getId(),
				Material.POTATO.getId(), Material.CARROT.getId(), Material.SUGAR_CANE_BLOCK.getId()));

		setMrlEnabled(getServer().getPluginManager().isPluginEnabled("MineResetLite"));
		setWgPlugin((WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard"));

		setDimmPlugin((DIMMPlugin) getServer().getPluginManager().getPlugin("DIMM"));
		getDimmPlugin().init();

		setParticleTask(new ExplosiveParticleTask());
		getParticleTask().start();

		if (!(setupBreakHandler())) {
			getLogger().warning("!!! Server version isn't compatible with this plugin. !!!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			getLogger().info("Server version is compatible with this plugin. Using " + getBreakHandler().getClass().getSimpleName());
		}
		getBreakHandler().register();

		this.cfgController = new OPConfigController();
		this.cfgController.init();

		getServer().getPluginManager().registerEvents(new ExplosiveListener(cfgController, ged(cfgController, "explosive")),this);
		getServer().getPluginManager().registerEvents(new DrillHammerListener(cfgController, ged(cfgController, "drillhammer")), this);
		getServer().getPluginManager().registerEvents(new WithdrawalListener(cfgController, ged(cfgController, "withdrawl")), this);
		getServer().getPluginManager().registerEvents(new LooterListener(cfgController, ged(cfgController, "looter")), this);
		getServer().getPluginManager().registerEvents(new ExperienceListener(cfgController, ged(cfgController, "experience")), this);
		getServer().getPluginManager().registerEvents(new OmniToolListener(cfgController, ged(cfgController, "omnitool")), this);
		getServer().getPluginManager().registerEvents(new UnbreakableListener(cfgController, ged(cfgController, "unbreakable")), this);
		getServer().getPluginManager().registerEvents(new AntiCobbleListener(cfgController, ged(cfgController, "anticobble")), this);
		getServer().getPluginManager().registerEvents(PotionEffectsListener.getInstance(cfgController, ged(cfgController, "potioneffects")), this);
		getServer().getPluginManager().registerEvents(AutoMineListener.getInstance(cfgController, ged(cfgController, "automine")), this);
		getServer().getPluginManager().registerEvents(MenuAPI.getMenuAPI(), this);

		for (Player p : getServer().getOnlinePlayers()) {
			if (p.getItemInHand() != null && GUI.getInstance().isTool(p.getItemInHand().getType())) {
				PlayerItemHeldEvent event = new PlayerItemHeldEvent(p, p.getInventory().getHeldItemSlot(), p.getInventory().getHeldItemSlot());
				getServer().getPluginManager().callEvent(event);
			}
		}

	}

	public OPEnchantData ged(OPConfigController controllerIn, String name) {
		if(controllerIn.getEnchantConfigs() != null &&
			controllerIn.getEnchantConfigs().getConfig() != null &&
			controllerIn.getEnchantConfigs().getConfig().getOpenchants() != null)
			return controllerIn.getEnchantConfigs().getConfig().getOpenchants().get(name);
		return null;
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);

		for (Player online : getServer().getOnlinePlayers()) {
			if (online.getOpenInventory() != null && online.getOpenInventory().getTopInventory().getHolder() instanceof Menu) {
				online.closeInventory();
			}
		}

		getParticleTask().stop();
		setParticleTask(null);

		getBreakHandler().unregister();
		setBreakHandler(null);

		getDisableWorlds().clear();
		setDisableWorlds(null);

		getIgnoredMats().clear();
		setIgnoredMats(null);

		setWgPlugin(null);
		setDimmPlugin(null);

		finalInstance = null;
	}

	public String getPrefix() {
		return ChatColor.translateAlternateColorCodes('&', getConfig().getString("message.prefix"));
	}

	public String getPrimary() {
		return ChatColor.translateAlternateColorCodes('&', getConfig().getString("message.primary"));
	}

	public String getSecondary() {
		return ChatColor.translateAlternateColorCodes('&', getConfig().getString("message.secondary"));
	}

	public boolean isIgnored(Material material) {
		return isIgnored(material.getId());
	}

	public boolean isIgnored(int typeId) {
		return this.ignoredMats.contains(typeId);
	}

	private boolean setupBreakHandler() {
		String version = "";

		try {
			version = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException ignore) {
			return false;
		}

		getLogger().info("Server running version " + version + " checking if BreakHandler is compatible with your version.");

		if (version.equalsIgnoreCase("v1_8_R3")) {
			setBreakHandler(new BreakHandler_1_8_R3());
		} else {
			getLogger().warning("Server version wasn't compatible! Enabling Reflection / ProtocolLib support. (This could lead to TPS Drops but is highly unlikely.)");
			setBreakHandler(new BreakHandler_Reflection());
		}

		return getBreakHandler() != null;
	}

	public static Main getInstance() {
		return finalInstance;
	}
}
