package org.pvpingmc.tokens.event;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class EnchantToInvEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	
	@Getter @Setter private List<ItemStack> drops;
	@Getter @Setter private boolean cancelled;

	public EnchantToInvEvent(Player who, List<ItemStack> drops) {
		super(who);
		setDrops(drops);
		setCancelled(false);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
