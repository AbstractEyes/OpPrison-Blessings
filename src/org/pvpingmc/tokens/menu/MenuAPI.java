package org.pvpingmc.tokens.menu;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class MenuAPI implements Listener {
	private static MenuAPI i;
	
	public static MenuAPI getMenuAPI() {
		return (MenuAPI.i == null) ? (MenuAPI.i = new MenuAPI()) : MenuAPI.i;
	}
	
	public Menu createMenu(final String title, final int rows) {
		return new Menu(title, rows);
	}
	
	public Menu cloneMenu(final Menu menu) {
		return menu.clone();
	}
	
	public void removeMenu(final Menu menu) {
		for (final HumanEntity viewer : menu.getInventory().getViewers()) {
			if (viewer instanceof Player) {
				menu.closeMenu((Player)viewer);
			}
			else {
				viewer.closeInventory();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onMenuItemClicked(final InventoryClickEvent event) {
		final Inventory inventory = event.getInventory();
		if (inventory.getHolder() instanceof Menu) {
			event.setCancelled(true);
			((Player)event.getWhoClicked()).updateInventory();
			switch (event.getAction()) {
				case DROP_ALL_CURSOR:
				case DROP_ALL_SLOT:
				case DROP_ONE_CURSOR:
				case DROP_ONE_SLOT:
				case PLACE_ALL:
				case PLACE_ONE:
				case PLACE_SOME:
				case COLLECT_TO_CURSOR:
				case UNKNOWN: {}
				default: {
					final Menu menu = (Menu)inventory.getHolder();
					if (!(event.getWhoClicked() instanceof Player)) {
						break;
					}
					final Player player = (Player)event.getWhoClicked();
					if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) {
						if (menu.exitOnClickOutside()) {
							menu.closeMenu(player);
							break;
						}
						break;
					}
					else {
						final int index = event.getRawSlot();
						if (index < inventory.getSize()) {
							if (event.getAction() != InventoryAction.NOTHING) {
								menu.selectMenuItem(player, index, InventoryClickType.fromInventoryAction(event.getAction()));
								break;
							}
							break;
						}
						else {
							if (menu.exitOnClickOutside()) {
								menu.closeMenu(player);
								break;
							}
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMenuClosed(final InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			final Inventory inventory = event.getInventory();
			if (inventory.getHolder() instanceof Menu) {
				final Menu menu = (Menu)inventory.getHolder();
				final MenuCloseBehaviour menuCloseBehaviour = menu.getMenuCloseBehaviour();
				if (menuCloseBehaviour != null) {
					menuCloseBehaviour.onClose((Player)event.getPlayer(), menu, menu.bypassMenuCloseBehaviour());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLogoutCloseMenu(final PlayerQuitEvent event) {
		if (event.getPlayer().getOpenInventory() == null || !(event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof Menu)) {
			return;
		}
		final Menu menu = (Menu)event.getPlayer().getOpenInventory().getTopInventory().getHolder();
		menu.setBypassMenuCloseBehaviour(true);
		menu.setMenuCloseBehaviour(null);
		event.getPlayer().closeInventory();
	}
	
	static {
		MenuAPI.i = null;
	}
	
	public interface MenuCloseBehaviour
	{
		void onClose(Player p0, Menu p1, boolean p2);
	}
}
