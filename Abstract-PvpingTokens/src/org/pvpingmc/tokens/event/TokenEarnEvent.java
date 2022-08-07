package org.pvpingmc.tokens.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;
import lombok.Setter;

public class TokenEarnEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();
	
	@Getter @Setter private int tokens;

	public TokenEarnEvent(Player who, int tokens) {
		super(who);
		this.tokens = tokens;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
