package org.pvpingmc.tokens.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import lombok.Setter;

public class EnchantBlockBreakEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	
	@Getter @Setter private int blocksBroken;

	public EnchantBlockBreakEvent(Player who, int blocksBroken) {
		super(who);
		setBlocksBroken(blocksBroken);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
