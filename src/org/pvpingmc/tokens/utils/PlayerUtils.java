package org.pvpingmc.tokens.utils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayerUtils {

	public static Player[] getNearbyPlayers(Location location, int radius) {
		World world = location.getWorld();
		
		List<Player> nearbyPlayers = world.getNearbyEntities(location, radius, radius, radius).stream().filter(Player.class::isInstance).map(Player.class::cast).collect(Collectors.toList());
		return nearbyPlayers.toArray(new Player[nearbyPlayers.size()]); 
	}
	
	public static UUID[] getNearbyUUIDs(Location location, int radius) {
		World world = location.getWorld();
		
		List<UUID> nearbyPlayers = world.getNearbyEntities(location, radius, radius, radius).stream().filter(Player.class::isInstance).map(Player.class::cast).map(Player::getUniqueId).collect(Collectors.toList());
		return nearbyPlayers.toArray(new UUID[nearbyPlayers.size()]); 
	}
}
