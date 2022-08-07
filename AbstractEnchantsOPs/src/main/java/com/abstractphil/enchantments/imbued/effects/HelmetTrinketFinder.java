package com.abstractphil.enchantments.imbued.effects;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedArmor;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import org.bukkit.entity.Player;

//a chance to return a failed scratcher
// Requires lore check in JS to latch into scratcher code.
public class HelmetTrinketFinder extends ImbuedClazz implements ImbuedArmor {
    ImbuedData imbuedData;
    SharedUtils utils;

    public HelmetTrinketFinder(ImbuedData dataIn) {
        super(dataIn);
    }
    public HelmetTrinketFinder(){super();}

    @Override
    public void setUtils(SharedUtils utilsIn)
    {
        utils = utilsIn;
    }
    @Override
    public SharedUtils getUtils() { return utils; }

    @Override
    public void setData(ImbuedData data) {
        imbuedData = data;
    }

    @Override
    public ImbuedData getData() {
        return imbuedData;
    }

    @Override
    public EnchantType getType() {
        return EnchantType.HELMET;
    }

    @Override
    public int getMaxNaturalLevel() {
        return 1;
    }

    @Override
    public void onTick(Player player, int i) {

    }
}
