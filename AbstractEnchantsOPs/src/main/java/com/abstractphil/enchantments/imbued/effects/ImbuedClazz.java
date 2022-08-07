package com.abstractphil.enchantments.imbued.effects;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.interfaces.Imbued;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedArmor;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedTool;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedWeapon;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;

public class ImbuedClazz implements Imbued { //, ImbuedTool, ImbuedArmor, ImbuedWeapon {

    public ImbuedClazz(ImbuedData dataIn)
    {
        setData(dataIn);
    }
    public ImbuedClazz() {
        super();
    }

    @Override
    public void setData(ImbuedData data) {

    }

    @Override
    public ImbuedData getData() {
        return null;
    }

    @Override
    public void setUtils(SharedUtils utilsIn) {

    }

    @Override
    public SharedUtils getUtils() {
        return null;
    }

    @Override
    public EnchantType getType() {
        return null;
    }

    @Override
    public int getMaxNaturalLevel() {
        return 0;
    }
}
