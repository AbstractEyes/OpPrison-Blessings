package com.abstractphil.enchantments.commands;

import com.abstractphil.enchantments.controller.PassivesController;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.RedItems;
import com.abstractphil.enchantments.AbstractEnchantments;

import com.redmancometh.reditems.storage.EnchantData;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PassiveCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		System.out.println("passive command");
		System.out.println(Arrays.toString(args));
		//todo: Enable op check for release.
		if (!sender.isOp()) return false;
		try {
			// Create initial passive item.
			Player player = Bukkit.getPlayer(args[1]);
			PassivesController passives = AbstractEnchantments.getInstance().passivesController();
			SharedUtils utils = AbstractEnchantments.getInstance().imbuedController().getUtils();
			Tuple<Integer, ItemStack>  passiveItem;
			switch (args[0]) {
				case "debug":
				case "check":
					checkPassives(player, passives);
					break;
				case "start":
					// Give the passive item to a player.
					//player
					startPassives(player, passives);
					break;
				case "addrandomlevel":
					addRandomLevel(player, passives);
					break;
				case "clean":
					purgeOldPassives(player, passives);
					break;
				case "levelup":
					// levelup <PLAYER> <PASSIVE_NAME> <AMOUNT>
					addLevel(player, passives, args[2],  Integer.parseInt(args[3]));
					break;
				case "setlevel":
					// setlevel <PLAYER> <PASSIVE_NAME> <LEVEL>
					System.out.println("Player " + player.getName() + " set passive level for " + args[2] + " to " + args[3]);
					if(args[2].equals("add")) {
						passiveItem = passives.getPassiveItemTuple(player);
						passives.setPassiveLevel(passiveItem.b(), args[2], Integer.parseInt(args[3]));
					}
					break;
				default:
					if(player != null) player.sendMessage("Error: Passive command invalid.");
					else System.out.print("Error: Passive command invalid, no player");
					return false;
			}
			return true;
		} catch (Exception ex) {
			Player player = Bukkit.getPlayer(args[1]);
			if(player != null) player.sendMessage("Error: Passive command invalid.");
			else System.out.print("Error: Passive command invalid, no player");
			ex.printStackTrace();
			return false;
		}
	}

	private void addLevel(Player player, PassivesController passives, String passiveName, int intendedLevel) {
		if(player == null) return;
		passives.addPassiveLevel(player, passiveName, intendedLevel);
	}

	private void purgeOldPassives(Player player, PassivesController passives) {
		Tuple<Integer, ItemStack> passiveItem = passives.getPassiveItemTuple(player);
		if(passiveItem != null) {
			passives.purgeMissingPassives(player, passiveItem.b());
		}
	}

	private void addRandomLevel(Player player, PassivesController passives){
		Tuple<Integer, ItemStack> passiveItem;
		passiveItem = passives.getPassiveItemTuple(player);
		if(passiveItem != null) {
			passives.levelRandomPassive(player);
		}
	}

	private void checkPassives(Player player, PassivesController passives) {
		Tuple<Integer, ItemStack> passiveItem;
		System.out.println("Checking loaded passives");
		passives.cfg().getPassives().forEach((name, passive) -> {
			System.out.println(name + " " + passive.getName());
		});
		System.out.println("Passive checking finished");
		passiveItem = passives.getPassiveItemTuple(player);
		System.out.println("Passive Item: " + passiveItem);
		if(passiveItem != null) {
			passiveItem.b().getItemMeta().getLore().forEach(System.out::println);
			for (EnchantData effect : RedItems.getInstance().getEnchantManager().getEffects(passiveItem.b())) {
				System.out.println(effect.getEffect().getName() + " " +
						passives.getPassiveLevel(
								passiveItem.b(), effect.getEffect().getName()));
				System.out.println(effect.getEffectLore());
			}
		} else {
			System.out.println("No passive item found");
		}
	}


	private void startPassives(Player player, PassivesController passives) {
		System.out.println("Player " + player.getName() + " started a passive item.");
		ItemStack baseItem = passives.prepareBasePassivesContainer(player);
		System.out.println(baseItem);
		player.getInventory().addItem(baseItem);
	}


}
