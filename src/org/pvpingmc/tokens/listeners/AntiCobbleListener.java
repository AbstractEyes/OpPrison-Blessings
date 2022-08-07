package org.pvpingmc.tokens.listeners;

import com.redmancometh.configcore.config.ConfigManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.pvpingmc.tokens.config.OPConfigController;
import org.pvpingmc.tokens.configobjects.OPEnchantConfig;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.utils.RedItemsUtils;

public class AntiCobbleListener implements Listener {
	OPConfigController utils;
	OPEnchantData data;

	public AntiCobbleListener(OPConfigController utilsIn, OPEnchantData dataIn) {
		super();
		utils = utilsIn;
		data = dataIn;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFromTo(BlockFromToEvent event) {
		int id = event.getBlock().getTypeId();
		if (id >= 8 && id <= 11) {
			Block b = event.getToBlock();
			int toid = b.getTypeId();
			event.setCancelled(toid == 0 && generatesCobble(id, b));
		}
	}

	private final BlockFace[] faces = { BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

	public boolean generatesCobble(int id, Block b) {
		int mirrorID1 = id == 8 || id == 9 ? 10 : 8;
		int mirrorID2 = id == 8 || id == 9 ? 11 : 9;

		for (BlockFace face : faces) {
			Block r = b.getRelative(face, 1);
			if (r.getTypeId() == mirrorID1 || r.getTypeId() == mirrorID2) {
				return true;
			}
		}

		return false;
	}
}
