package com.abstractphil.enchantments.commands;

import com.abstractphil.enchantments.AbstractEnchantments;
import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.controller.ImbuedController;
import com.abstractphil.enchantments.imbued.effects.ImbuedClazz;
import com.abstractphil.enchantments.imbued.interfaces.Imbued;
import com.redmancometh.reditems.RedItems;
import com.redmancometh.reditems.mediator.AttachmentManager;
import com.redmancometh.reditems.mediator.EnchantManager;
import com.redmancometh.warcore.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

/*
	imbue give:
	description: gives an imbued piece of equipment.
	usage: "imbue give <player> <imbue_name> <item_name>"

	imbue add:
	description: imbues an otherwise normal piece of equipment.
	usage: "imbue add <player> <imbue_name> <item_index>"

	imbue remove:
	description: removes an imbue from a piece of equipment.
	usage: "imbue remove <player> <imbue_name> <item_index>"
*/
public class ImbuedCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		System.out.println("imbue command");
		System.out.println(Arrays.toString(args));
		if (!sender.isOp())
			return false;
		try {
			// Create initial imbued item.
			Player player = Bukkit.getPlayer(args[1]);
			ImbuedController imbues = AbstractEnchantments.getInstance().imbuedController();
			String imbue_name;
			Imbued imbuedInstance;
			ImbuedData data;
			Material material;
			ItemStack baseItem;
			switch (args[0]) {
				case "check":
					EnchantManager em = RedItems.getInstance().getEnchantManager();
					AttachmentManager am = RedItems.getInstance().getAttachManager();
					System.out.println("Checking loaded imbues");
					AbstractEnchantments.getInstance().imbuedController().cfg().getImbued().forEach((name, imbue) -> {
						System.out.println(name + " " + imbue.getName());
					});
					break;
				case "give":
					// Give an imbued item to a player.
					//player
					//imbue_name
					System.out.println("Player " + args[0] + " " + args[1]);
					imbue_name = args[2];
					imbuedInstance = imbues.getImbued(imbue_name);
					data = imbues.getImbuedData(imbue_name);
					material = imbues.getImbuedMaterial(imbue_name);
					baseItem = new ItemStack(material, 1);
					ItemUtil.setName(baseItem, data.getDisplayName());
					((Player)player).getInventory()
							.addItem(RedItems.getInstance().
									getEnchantManager().
										attachEffect(baseItem, imbuedInstance, 1));
					break;
				case "dump":
					// Give a player all of the imbued items.
					for (Map.Entry<String, ImbuedData> entry : imbues.cfg().getImbued().entrySet()) {
						String name = entry.getKey();
						ImbuedData imbue = entry.getValue();
						System.out.println("Player " + args[0] + " " + args[1]);
						imbuedInstance = imbues.getImbued(name);
						data = imbues.getImbuedData(name);
						material = imbues.getImbuedMaterial(name);
						baseItem = new ItemStack(material, 1);
						ItemUtil.setName(baseItem, data.getDisplayName());
						player.getInventory()
								.addItem(RedItems.getInstance().
										getEnchantManager().
										attachEffect(baseItem, imbuedInstance, 1));
					}
					break;
				case "add":
					// Remove an imbued effect from an existing imbued item.
					break;
				default:
					if(player != null) player.sendMessage("Error: God item command invalid.");
					else System.out.print("Error: God item command invalid, command invalid");
					return false;
			}
			return true;
		} catch (Exception ex) {
			Player player = Bukkit.getPlayer(args[1]);
			if(player != null) player.sendMessage("Error: God item command invalid.");
			else System.out.print("Error: God item command invalid, player invalid");
			ex.printStackTrace();
			return false;
		}
	}

}
