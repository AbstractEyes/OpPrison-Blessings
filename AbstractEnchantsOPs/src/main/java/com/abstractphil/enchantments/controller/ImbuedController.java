package com.abstractphil.enchantments.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.effects.ImbuedClazz;
import com.abstractphil.enchantments.imbued.interfaces.Imbued;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.configcore.config.ConfigManager;
import com.redmancometh.reditems.RedItems;
import com.abstractphil.enchantments.cfg.ImbuedConfig;
import com.redmancometh.reditems.abstraction.Effect;
import com.redmancometh.reditems.storage.EnchantData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class ImbuedController {
	private ConfigManager<ImbuedConfig> imbuedCfg = new ConfigManager("imbued.json", ImbuedConfig.class);
	private Map<String, ImbuedClazz> imbuedMap = new ConcurrentHashMap();
	private SharedUtils utils;

	public void init() {
		imbuedCfg.init();
		Map<String, ImbuedData> imbued = imbuedCfg.getConfig().getImbued();
		System.out.println(imbuedCfg.getConfig());
		setUtils(new SharedUtils());

		imbued.forEach((name, imbue) -> {
			ImbuedClazz imbuedInstance;
			Object obj = null;
			try {
				obj = imbue.getImbuedClass().getConstructor().newInstance();
			} catch(Throwable e) {
				throw new RuntimeException(e);
			}
			imbuedInstance = (ImbuedClazz)obj;
			imbuedInstance.setData(imbue);
			imbuedInstance.setUtils(utils);
			imbuedMap.put(name, imbuedInstance);
			System.out.println("REDITEMS: " + (RedItems.getInstance()));
			System.out.println("ENCHMAN: " + (RedItems.getInstance().getEnchantManager()));
			RedItems.getInstance().getEnchantManager().registerEffect(imbuedInstance);
		});
	}

	@Nullable
	public SharedUtils getUtils(){
		return utils;
	}
	private void setUtils(SharedUtils utilsIn){
		utils = utilsIn;
	}

	public String getImbuedDisplayMessage(String imbuedName) {
		return getImbuedData(imbuedName).getDisplayMessage();
	}

	public ImbuedData getImbuedData(String imbuedName) {
		return imbuedCfg.getConfig().getImbued().get(imbuedName);
	}

	public Material getImbuedMaterial(String imbuedName) {
		return getImbued(imbuedName).getData().getMaterial();
	}

	public double getCooldown(String imbuedName)
	{
		return getImbued(imbuedName).getData().getCooldown();
	}

	public Imbued getImbued(String imbuedName) {
		return imbuedMap.get(imbuedName);
	}

	public ImbuedConfig cfg() {
		return imbuedCfg.getConfig();
	}

	public boolean isWearingImbue(Player player, String effectName) {
		return null != getEffectData(player, effectName);
	}

	@Nullable
	public EnchantData getEffectData(Player player, String effectName) {
		for(ItemStack eItem : player.getInventory().getArmorContents()) {
			if(RedItems.getInstance().getEnchantManager().isRedItem(eItem)){
				for(EnchantData effect : RedItems.getInstance().getEnchantManager().getEffects(eItem)) {
					if(effect.getEffect().getName().equalsIgnoreCase(effectName)) {
						return effect;
					}
				}
			}
		}
		return null;
	}

	public void terminate() {
		imbuedCfg = null;
		imbuedMap.values().forEach((imbued) -> RedItems.getInstance().getEnchantManager().deregisterEffect(imbued));
	}
}
