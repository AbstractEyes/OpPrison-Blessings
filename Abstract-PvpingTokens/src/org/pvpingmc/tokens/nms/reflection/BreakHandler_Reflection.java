package org.pvpingmc.tokens.nms.reflection;

import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.pvpingmc.tokens.Main;
import org.pvpingmc.tokens.nms.BreakHandler;
import org.pvpingmc.tokens.nms.packets.WrapperPlayServerExplosion;
import org.pvpingmc.tokens.utils.ReflectionUtils;
import org.pvpingmc.tokens.utils.ReflectionUtils.RefClass;
import org.pvpingmc.tokens.utils.ReflectionUtils.RefConstructor;
import org.pvpingmc.tokens.utils.ReflectionUtils.RefField;
import org.pvpingmc.tokens.utils.ReflectionUtils.RefMethod;

import lombok.Getter;
import lombok.Setter;

public class BreakHandler_Reflection extends BreakHandler {
	
	@Getter @Setter private RefClass craftPlayer;
	@Getter @Setter private RefConstructor blockPosition;
	@Getter @Setter private RefMethod playerHandle, breakBlock;
	@Getter @Setter private RefField playerInteract;
	@Getter @Setter private boolean oldMethod;
	
	@Override
	public void breakBlock(Player player, Block block) {
		Object playerHandle = getPlayerHandle().of(player).call();
		Object pim = getPlayerInteract().of(playerHandle).get();
		if (isOldMethod()) {
			getBreakBlock().of(pim).call(block.getX(), block.getY(), block.getZ());
		} else {
			getBreakBlock().of(pim).call(getBlockPosition().create(block.getX(), block.getY(), block.getZ()));
		}
	}
	
	@Override
	public void sendExplosionPacket(Player player, Location loc) {
		WrapperPlayServerExplosion explosionPacket = new WrapperPlayServerExplosion();
		explosionPacket.setRadius(0.0F);
		explosionPacket.setX(loc.getBlockX() + (Math.random() * 1.5));
		explosionPacket.setY(loc.getBlockY() + (Math.random() * 1.5));
		explosionPacket.setZ(loc.getBlockZ() + (Math.random() * 1.5));
		explosionPacket.setRecords(Collections.EMPTY_LIST);
		explosionPacket.setPlayerVelocityX(0.0F);
		explosionPacket.setPlayerVelocityY(0.0F);
		explosionPacket.setPlayerVelocityZ(0.0F);
		
		explosionPacket.sendPacket(player);
	}
	
	@Override
	public void register() {
		setCraftPlayer(ReflectionUtils.getRefClass("{cb}.entity.CraftPlayer"));
		setBlockPosition(ReflectionUtils.getRefClass("{nms}.BlockPosition").getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE));

		setPlayerHandle(getCraftPlayer().getMethod("getHandle"));
		setPlayerInteract(getPlayerHandle().getReturnRefClass().findField(ReflectionUtils.getRefClass("{nms}.PlayerInteractManager")));

		try {
			ReflectionUtils.getRefClass("{nms}.BlockPosition");
			setOldMethod(false);
			Main.getInstance().getLogger().info("Succesfully found BlockPosition class, using >= 1.8 Method.");
		} catch (Exception ignore) {
			setOldMethod(true);
			Main.getInstance().getLogger().info("Couldn't find BlockPosition class, using Legacy Method.");
		}

		if (isOldMethod()) {
			setBreakBlock(getPlayerInteract().getFieldRefClass().getMethod("breakBlock", Integer.TYPE, Integer.TYPE, Integer.TYPE));
		} else {
			setBreakBlock(getPlayerInteract().getFieldRefClass().getMethod("breakBlock", getBlockPosition().getRefClass().getRealClass()));
		}
	}

	@Override
	public void unregister() {
		setCraftPlayer(null);
		
		setBlockPosition(null);
		
		setPlayerHandle(null);
		setBreakBlock(null);
		
		setPlayerInteract(null);
		
		setOldMethod(false);
	}
}
