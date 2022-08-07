package com.abstractphil.enchantments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.abstractphil.enchantments.commands.ImbuedCommands;
import com.abstractphil.enchantments.commands.PassiveCommands;
import com.abstractphil.enchantments.controller.ImbuedController;
import com.abstractphil.enchantments.controller.PassivesController;

import com.abstractphil.enchantments.passives.interfaces.Passive;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

@Getter
public class AbstractEnchantments extends JavaPlugin {
	public static ImbuedController imbuedController;
	public static PassivesController passivesController;

	@Override
	public void onEnable() {
		try {
			super.onEnable();
			imbuedController = new ImbuedController();
			imbuedController.init();
			passivesController = new PassivesController();
			passivesController.init();
		} catch(Throwable e) {
			e.printStackTrace();
		}
		getCommand("imbue").setExecutor(new ImbuedCommands());
		getCommand("passive").setExecutor(new PassiveCommands());
	}

	@Override
	public void onDisable() {
		imbuedController.terminate();
		passivesController.terminate();
		super.onDisable();
	}


	public ImbuedController getImbuedController() {
		return imbuedController;
	}
	public PassivesController getPassivesController() {
		return passivesController;
	}

	public ImbuedController imbuedController() {
		return imbuedController;
	}
	public PassivesController passivesController() {
		return passivesController;
	}

	public static AbstractEnchantments getInstance() {
		return (AbstractEnchantments)JavaPlugin.getPlugin(AbstractEnchantments.class);
	}
}
