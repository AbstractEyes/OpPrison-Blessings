package org.pvpingmc.tokens.nms.v1_8_R3;

import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.pvpingmc.tokens.nms.BreakHandler;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutExplosion;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;

public class BreakHandler_1_8_R3 extends BreakHandler {

	@Override
	public void breakBlock(Player player, Block block) {
		EntityPlayer playerHandle = ((CraftPlayer) player).getHandle();
		PlayerInteractManager pim = playerHandle.playerInteractManager;
		
		pim.breakBlock(new BlockPosition(block.getX(), block.getY(), block.getZ()));
	}
	
	@Override
	public void sendExplosionPacket(Player player, Location loc) {
		EntityPlayer playerHandle = ((CraftPlayer) player).getHandle();
		PacketPlayOutExplosion explosionPacket = new PacketPlayOutExplosion(loc.getBlockX() + (Math.random() * 1.5), loc.getBlockY() + + (Math.random() * 1.5), loc.getBlockZ() + + (Math.random() * 1.5), 0.0F, Collections.EMPTY_LIST, null);
		
		playerHandle.playerConnection.sendPacket(explosionPacket);
	}

	@Override
	public void register() {}

	@Override
	public void unregister() {}
}
