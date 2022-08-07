package org.pvpingmc.tokens.nms;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class BreakHandler {
	
	public abstract void breakBlock(Player player, Block block);
	
	public abstract void sendExplosionPacket(Player player, Location loc);

	public abstract void register();
	
	public abstract void unregister();
}
