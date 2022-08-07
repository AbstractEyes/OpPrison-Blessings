package com.abstractphil.enchantments.controller;

import com.abstractphil.enchantments.AbstractEnchantments;
import com.abstractphil.enchantments.cfg.ImbuedConfig;
import com.abstractphil.enchantments.cfg.PassivesConfig;
import com.abstractphil.enchantments.cfg.PassivesData;
import com.abstractphil.enchantments.cfg.PassivesDetails;
import com.abstractphil.enchantments.passives.interfaces.Passive;
import com.abstractphil.enchantments.passives.interfaces.PassiveContainer;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.configcore.config.ConfigManager;
import com.redmancometh.reditems.RedItems;
import com.redmancometh.reditems.abstraction.Effect;
import com.redmancometh.reditems.mediator.EnchantManager;
import com.redmancometh.reditems.storage.EnchantData;
import com.redmancometh.reditems.storage.SimpleContainer;
import com.redmancometh.warcore.util.ItemUtil;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.pvpingmc.tokens.utils.CleanItem;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.Console;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class PassivesController {
	private ConfigManager<PassivesConfig> passiveCfg = new ConfigManager("passives.json", PassivesConfig.class);
	private Map<String, Passive> passiveMap = new ConcurrentHashMap();
	private PassiveContainer containerEffect;
	private SharedUtils utils;
	@Nullable
	public SharedUtils getUtils(){
		return utils;
	}
	private void setUtils(SharedUtils utilsIn){
		utils = utilsIn;
	}

	public void init() {
		passiveCfg.init();
		Map<String, PassivesData> passives = passiveCfg.getConfig().getPassives();
		setUtils(AbstractEnchantments.getInstance().imbuedController.getUtils());
		System.out.println(passiveCfg.getConfig());


		try {
			// Instantiate the container.
			containerEffect = new PassiveContainer();
			containerEffect.setData(passives);
			containerEffect.setDetails(getPassiveDetails());
			containerEffect.setUtils(getUtils());
			RedItems.getInstance().getEnchantManager().registerEffect(containerEffect);
		} catch (Exception ex) {
			System.out.println("Container instantiation failed");
			ex.printStackTrace();
		}
		passives.forEach((name, passive) -> {
			try {
				Passive passiveInst = passive.getPassiveClass().newInstance();
				passiveInst.setData(passive);
				passiveMap.put(name, passiveInst);
				System.out.println("REDITEMS: " + (RedItems.getInstance()));
				System.out.println("ENCHMAN: " + (RedItems.getInstance().getEnchantManager()));
				RedItems.getInstance().getEnchantManager().registerEffect(passiveInst);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public int getPassiveLevel(ItemStack item, String passiveName) {
		try {
			return RedItems.getInstance().getEnchantManager().getEffectLevel(item, passiveMap.get(passiveName));
		} catch (Exception ex) {
			System.out.println("Failed to get passive level");
			ex.printStackTrace();
			return 0;
		}
	}

	public void setPassiveLevel(ItemStack item, String passiveName, int setLevel) {
		try {
			RedItems.getInstance().getEnchantManager().
					levelOrAddEffect(item, passiveMap.get(passiveName), setLevel, true);
		} catch (Exception ex) {
			System.out.println("failed to set passive level");
		}
	}

	public void purgeMissingPassives(Player player, ItemStack item) {
		//try {
			/*
			passiveCfg.getConfig().getPassives().forEach( (passiveName, data) -> {
				String
				//System.out.println("Passive name: " + passiveName);
				int level = getPassiveLevel(item, passiveName);
				//System.out.println("Level: " + level);
				(RedItems.getInstance().getEnchantManager().getEffects(item)).forEach( effect -> {
					if(effect.getEffect().getName().equalsIgnoreCase(passiveName))
				});
				//System.out.println("Natural level: " + getPassive(passiveName).getMaxNaturalLevel());
				if(Math.max(1, level + levelAmount) <= getPassive(passiveName).getMaxNaturalLevel())
				{
					//System.out.println("Level before: " + getPassiveLevel(item, passiveName));
					// Get simple container.
					Optional<SimpleContainer> data = RedItems.getInstance().getEnchantManager().getData(item);
					// Get enchantment list.
					//System.out.println("Level Data: " + data);
					@Nullable ArrayList<EnchantData> eData;
					eData = (ArrayList<EnchantData>)data.get().getEnchants();
					if(eData != null){
						//System.out.println("eData " + eData);
						ArrayList<EnchantData> newData = removeEnchant(eData, passiveMap.get(passiveName));
						//System.out.println("newData " + newData);
						data.get().setEnchants(newData);
						//System.out.println("newData " + newData);
					}
					ItemStack readyItem = RedItems.getInstance().getEnchantManager().
							attachEffect(item, passiveMap.get(passiveName), Math.max(1, level + levelAmount));
					//System.out.println("readyitem " + readyItem);
					readyItem = updateLores(player, readyItem);
					//System.out.println("Final item: " + readyItem);
					return readyItem;
				} else {
					return item;
				}
			});

		} catch (Exception ex) {
			System.out.println("failed to add a passive level");
			ex.printStackTrace();
		}

			 */
	}

	public void addPassiveLevel(Player player, String passiveName, int intendedLevel) {
		if(player == null) return;
		System.out.println("Player " + player.getName() + "passiveName" + " add passive level " + intendedLevel);
		Tuple<Integer, ItemStack> passiveItem = getPassiveItemTuple(player);
		if(!(passiveItem == null)) {
			ItemStack newPassive = preparePassiveLevel(player, passiveItem.b(), passiveName, intendedLevel);
			if(newPassive != passiveItem.b()) {
				player.getInventory().setItem(passiveItem.a(), newPassive);
				/* player.sendMessage("Success: Leveled " +
						getPassiveData(passiveName).getDisplayName() + " to " +
						getPassiveLevel(newPassive, passiveName));
				 */
			} else {
				//player.sendMessage("You cannot level this passive to that level");
			}
		} else {
			//player.sendMessage("You do not have the base passive item to level up.");
		}
	}

	private ItemStack preparePassiveLevel(Player player, ItemStack item, String passiveName, int levelAmount) {
		try {
			if(!canLevelPassive(item, passiveName, levelAmount)){
				//player.sendMessage("Blessing level " + passiveName + " is too high to level up.");
				return item;
			}
			//System.out.println("Passive name: " + passiveName);
			int level = getPassiveLevel(item, passiveName);
			//System.out.println("Level: " + level);
			if(level < 0) level = 0;
			//System.out.println("Natural level: " + getPassive(passiveName).getMaxNaturalLevel());
			if(Math.max(1, level + levelAmount) <= getPassive(passiveName).getMaxNaturalLevel())
			{
				//System.out.println("Level before: " + getPassiveLevel(item, passiveName));
				// Get simple container.
				Optional<SimpleContainer> data = RedItems.getInstance().getEnchantManager().getData(item);
				// Get enchantment list.
				//System.out.println("Level Data: " + data);
				@Nullable ArrayList<EnchantData> eData;
				eData = (ArrayList<EnchantData>)data.get().getEnchants();
				if(eData != null){
					//System.out.println("eData " + eData);
					ArrayList<EnchantData> newData = removeEnchant(eData, passiveMap.get(passiveName));
					//System.out.println("newData " + newData);
					data.get().setEnchants(newData);
					//System.out.println("newData " + newData);
				}
				ItemStack readyItem = RedItems.getInstance().getEnchantManager().
						attachEffect(item, passiveMap.get(passiveName), Math.max(1, level + levelAmount));
				//System.out.println("readyitem " + readyItem);
				readyItem = updateLores(player, readyItem);
				//System.out.println("Final item: " + readyItem);
				return readyItem;
			} else {
				return item;
			}
		} catch (Exception ex) {
			System.out.println("failed to add a passive level");
			ex.printStackTrace();
		}
		return item;
	}

	private ArrayList<EnchantData> removeEnchant(ArrayList<EnchantData> data, Effect effectToRemove) {
		if(data.size() == 0) return data;
		int oi = -1;
		for (int i = 0, dataSize = data.size(); i < dataSize; i++) {
			EnchantData ench = data.get(i);
			if (ench.getEffect().getName().equals(effectToRemove.getName())) {
				oi = i;
				break;
			}
		}
		if(oi >= 0) data.remove(oi);
		return data;
	}

	private ItemStack updateLores(Player player, ItemStack passiveItem) {
		List<String> lore = passiveItem.getItemMeta().getLore();
		lore.clear();
		String fullUUID = utils.translateColors("&0" + player.getUniqueId().toString());
		lore.add(fullUUID.substring(0, 26));
		lore.add(utils.translateColors("&0" + fullUUID.substring(27)));
		//System.out.println("UUID check");
		EnchantManager em = RedItems.getInstance().getEnchantManager();
		if(em.getEffects(passiveItem).size() > 0) {
			em.getEffects(passiveItem).forEach( effect -> {
				List<String> tempLore = effect.getEffect().getLore();
				if(tempLore.size() > 0) {
					String preppedLore = tempLore.get(0).replace("%l", Integer.toString(effect.getLevel()));
					lore.add(preppedLore);
				}
			});
		}
		return ItemUtil.setLore(passiveItem, lore);
	}

	public ItemStack prepareBasePassivesContainer(Player player) {
		ItemStack baseItem = new ItemStack(getPassiveItemMaterial(), 1, (short)3);
		SkullMeta meta = (SkullMeta)baseItem.getItemMeta();
		meta.setOwner(player.getName());
		baseItem.setItemMeta(meta);
		ItemUtil.setName(baseItem, getPassiveItemName());
		ArrayList<String> lores = new ArrayList<>();
		String fullUUID = utils.translateColors("&0" + player.getUniqueId().toString());

		lores.add(fullUUID.substring(0, 22));
		lores.add(utils.translateColors("&0" + fullUUID.substring(23)));
		baseItem = RedItems.getInstance().getEnchantManager().attachEffect(
				baseItem, getPassiveContainerEffect(), 1);
		baseItem = ItemUtil.setLore(baseItem, lores);
		return baseItem;
	}

	public void prepareNewEffects(ArrayList<Passive> effectsIn) {

	}

	private void updateLores(ItemStack item, String passiveName) {

	}

	public boolean canLevelPassive(ItemStack item, String passiveName, int levelAmount) {
		return getPassiveLevel(item, passiveName) + levelAmount <= getPassiveData(passiveName).getMaxLevel();
	}

	public PassivesData getPassiveData(String passiveName) {
		return passiveCfg.getConfig().getPassives().get(passiveName);
	}

	public PassivesDetails getPassiveDetails() {
		return passiveCfg.getConfig().getPassivesDetails();

	}

	public Material getPassiveMaterial(String passiveName) {
		return getPassive(passiveName).getData().getMaterial();
	}

	public Passive getPassive(String passiveName) {
		return passiveMap.get(passiveName);
	}

	public PassivesConfig cfg() {
		return passiveCfg.getConfig();
	}

	public Map<String, Passive> getPassiveMap() {
		return passiveMap;
	}

	public String getPassiveItemName()
	{
		return getPassiveDetails().getName();
	}
	public Material getPassiveItemMaterial() {
		return getPassiveDetails().getMaterial();
	}
	public String getPassiveItemSkullMeta() {
		return getPassiveDetails().getSkullMeta();
	}

	public PassiveContainer getPassiveContainerEffect(){
		return containerEffect;
	}

	public void terminate() {
		passiveCfg = null;
		passiveMap.values().forEach((passive) -> RedItems.getInstance().getEnchantManager().deregisterEffect(passive));
	}

	/*@Nullable
	public ItemStack getPassiveItem(Player player) {
		if(player == null) return null;
		PassivesController passives = AbstractEnchantments.getInstance().getPassivesController();
		EnchantManager manager = RedItems.getInstance().getEnchantManager();
		ItemStack[] contents = player.getInventory().getContents();
		for (ItemStack item : contents) {
			if (manager.isRedItem(item)) {
				for (EnchantData effect : manager.getEffects(item)) {
					if (effect.getEffect() instanceof PassiveContainer) {
						System.out.println("Found specified passive container.");
						return item;
					}
				}
			}
		}
		return null;
	}*/

	public void levelRandomPassive(Player player)
	{
		Tuple<Integer, ItemStack> passiveItem = getPassiveItemTuple(player);
		if(passiveItem == null) return;
		ArrayList<String> list = new ArrayList<>();
		getPassiveMap().forEach( (name, passive) -> {
			RedItems.getInstance().getEnchantManager().
					getEffects(passiveItem.b()).forEach( effect -> {
				if(!effect.getEffect().getName().equals("passivecontainer")) {
					list.add(effect.getEffect().getName().toLowerCase(Locale.ROOT));
				}
			});
		});
		if(list.size() > 0)
		{
			String getEffectName = list.get(ThreadLocalRandom.current().nextInt(list.size() - 1));

			ItemStack newPassive = preparePassiveLevel(player, passiveItem.b(), getEffectName, 1);
			if(newPassive != passiveItem.b()) {
				player.getInventory().setItem(passiveItem.a(), newPassive);
				player.sendMessage("Success: Leveled " +
						getPassiveData(getEffectName).getDisplayName() + " to " +
						getPassiveLevel(newPassive, getEffectName));
			}
		}

	}

	@Nullable
	public ItemStack getPassiveItem(Player player) {
		Tuple<Integer, ItemStack> passiveItem;
		if((passiveItem = getPassiveItemTuple(player)) != null) return passiveItem.b();
		return null;
	}

	@Nullable
	public Tuple<Integer, ItemStack> getPassiveItemTuple(Player player) {
		if(player == null) return null;
		PassivesController passives = AbstractEnchantments.getInstance().passivesController();
		EnchantManager manager = RedItems.getInstance().getEnchantManager();
		ItemStack[] contents = player.getInventory().getContents();
		for (int i = 0, contentsLength = contents.length; i < contentsLength; i++) {
			ItemStack item = contents[i];
			if (manager.isRedItem(item)) {
				for (EnchantData effect : manager.getEffects(item)) {
					if (effect.getEffect() instanceof PassiveContainer) {
						//System.out.println("Found specified passive container.");
						return new Tuple<>(i, item);
					}
				}
			}
		}
		return null;
	}

}
