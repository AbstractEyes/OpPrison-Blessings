package com.abstractphil.enchantments.cfg;

import com.abstractphil.enchantments.imbued.effects.ImbuedClazz;
import lombok.Data;
import org.bukkit.Material;

import java.util.List;

@Data
public class ImbuedData {
	private Class<? extends ImbuedClazz> imbuedClass;
	private String name, displayName, displayMessage;
	private List<String> lore, displayLore;
	private Material material;
	private double chance;
	private double[] args, passivesBonuses;
	private double cooldown;
}
