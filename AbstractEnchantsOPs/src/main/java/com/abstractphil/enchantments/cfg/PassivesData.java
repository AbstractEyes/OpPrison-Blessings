package com.abstractphil.enchantments.cfg;

import com.abstractphil.enchantments.passives.interfaces.Passive;
import lombok.Data;
import org.bukkit.Material;

import java.util.List;

@Data
public class PassivesData {
	private Class<? extends Passive> passiveClass;
	private String name, displayName;
	private List<String> lore, displayLore;
	private Material material;
	private double maxLevel;
	private double chance;
	private double chancePerLevel;
	private double maxChance;
	private double baseCost;
	private double[] masteryBonus;
	private double costMultiplier;
	private double[] args;
	private double cooldown;

}
