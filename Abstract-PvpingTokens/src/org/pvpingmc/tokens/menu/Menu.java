package org.pvpingmc.tokens.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Menu implements InventoryHolder {
	protected Map<Integer, MenuItem> items;
	private Inventory inventory;
	protected String title;
	protected int rows;
	protected boolean exitOnClickOutside;
	protected MenuAPI.MenuCloseBehaviour menuCloseBehaviour;
	protected boolean bypassMenuCloseBehaviour;
	protected Menu parentMenu;
	
	public Menu(final String title, final int rows) {
		this(title, rows, null);
	}
	
	public Menu(final String title, final int rows, final Menu parentMenu) {
		this.items = new HashMap<Integer, MenuItem>();
		this.exitOnClickOutside = false;
		this.bypassMenuCloseBehaviour = false;
		this.title = title;
		this.rows = rows;
		this.parentMenu = parentMenu;
	}
	
	public void setMenuCloseBehaviour(final MenuAPI.MenuCloseBehaviour menuCloseBehaviour) {
		this.menuCloseBehaviour = menuCloseBehaviour;
	}
	
	public MenuAPI.MenuCloseBehaviour getMenuCloseBehaviour() {
		return this.menuCloseBehaviour;
	}
	
	public void setBypassMenuCloseBehaviour(final boolean bypassMenuCloseBehaviour) {
		this.bypassMenuCloseBehaviour = bypassMenuCloseBehaviour;
	}
	
	public boolean bypassMenuCloseBehaviour() {
		return this.bypassMenuCloseBehaviour;
	}
	
	public void setExitOnClickOutside(final boolean exit) {
		this.exitOnClickOutside = exit;
	}
	
	public Map<Integer, MenuItem> getMenuItems() {
		return this.items;
	}
	
	public boolean addMenuItem(final MenuItem item, final int x, final int y) {
		return this.addMenuItem(item, y * 9 + x);
	}
	
	public boolean addMenuItem(final MenuItem item, final int index) {
		final ItemStack slot = this.getInventory().getItem(index);
		if (slot != null && slot.getType() != Material.AIR) {
			this.removeMenuItem(index);
		}
		item.setSlot(index);
		this.getInventory().setItem(index, item.getItemStack());
		this.items.put(index, item);
		item.addToMenu(this);
		return true;
	}
	
	public boolean removeMenuItem(final int x, final int y) {
		return this.removeMenuItem(y * 9 + x);
	}
	
	public boolean removeMenuItem(final int index) {
		final ItemStack slot = this.getInventory().getItem(index);
		if (slot == null || slot.getType().equals((Object)Material.AIR)) {
			return false;
		}
		this.getInventory().clear(index);
		this.items.remove(index).removeFromMenu(this);
		return true;
	}
	
	protected void selectMenuItem(final Player player, final int index, final InventoryClickType clickType) {
		if (this.items.containsKey(index)) {
			final MenuItem item = this.items.get(index);
			item.onClick(player, clickType);
		}
	}
	
	public void openMenu(final Player player) {
		if (!this.getInventory().getViewers().contains(player)) {
			player.openInventory(this.getInventory());
		}
	}
	
	public void closeMenu(final Player player) {
		if (this.getInventory().getViewers().contains(player)) {
			this.getInventory().getViewers().remove(player);
			player.closeInventory();
		}
	}
	
	public Menu getParent() {
		return this.parentMenu;
	}

	@Override
	public Inventory getInventory() {
		if (this.inventory == null) {
			this.inventory = Bukkit.createInventory((InventoryHolder)this, this.rows * 9, this.title);
		}
		return this.inventory;
	}
	
	public boolean exitOnClickOutside() {
		return this.exitOnClickOutside;
	}
	
	@Override
	protected Menu clone() {
		final Menu clone = new Menu(this.title, this.rows);
		clone.setExitOnClickOutside(this.exitOnClickOutside);
		clone.setMenuCloseBehaviour(this.menuCloseBehaviour);
		for (final Map.Entry<Integer, MenuItem> entry : this.items.entrySet()) {
			clone.addMenuItem(entry.getValue(), entry.getKey());
		}
		return clone;
	}
	
	public void updateMenu() {
		for (final HumanEntity entity : this.getInventory().getViewers()) {
			((Player)entity).updateInventory();
		}
	}
}
