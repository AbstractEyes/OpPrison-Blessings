package org.pvpingmc.tokens.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class MenuItem {
	private Menu menu;
	private Integer slot;
	private int number;
	private ItemStack icon;
	private String text;
	private List<String> descriptions;
	
	public MenuItem(final ItemStack icon) {
		this(null, icon);
	}
	
	public MenuItem(final String text) {
		this(text, new ItemStack(Material.PAPER));
	}
	
	public MenuItem(final String text, final ItemStack icon) {
		this(text, icon, 1);
	}
	
	public MenuItem(final String text, final ItemStack icon, final int number) {
		this.text = null;
		this.descriptions = new ArrayList<String>();
		if (text != null) {
			this.text = ChatColor.translateAlternateColorCodes('&', text);
		}
		this.icon = icon;
		this.number = number;
	}
	
	protected void addToMenu(final Menu menu) {
		this.menu = menu;
	}
	
	protected void removeFromMenu(final Menu menu) {
		if (this.menu == menu) {
			this.menu = null;
		}
	}
	
	public Menu getMenu() {
		return this.menu;
	}
	
	public int getSlot() {
		return this.slot;
	}
	
	public void setSlot(final int slot) {
		this.slot = slot;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public ItemStack getIcon() {
		return this.icon;
	}
	
	public void setIcon(final ItemStack icon) {
		this.icon = icon;
		this.menu.getInventory().setItem((int)this.slot, this.getIcon());
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setDescriptions(final List<String> lines) {
		this.descriptions = lines;
	}
	
	public void addDescription(final String line) {
		this.descriptions.add(ChatColor.translateAlternateColorCodes('&', line));
	}
	
	protected ItemStack getItemStack() {
		final ItemStack slot = this.getIcon().clone();
		final ItemMeta meta = slot.getItemMeta();
		if (meta.hasLore()) {
			meta.getLore().addAll(this.descriptions);
		}
		else {
			meta.setLore(this.descriptions);
		}
		if (this.getText() != null) {
			meta.setDisplayName(this.getText());
		}
		slot.setItemMeta(meta);
		return slot;
	}
	
	public abstract void onClick(final Player p, final InventoryClickType click);

	public static class UnclickableMenuItem extends MenuItem {
		public UnclickableMenuItem(final ItemStack icon) {
			super(icon);
		}

		@Override
		public void onClick(final Player player, final InventoryClickType clickType) {
		}
	}
}
