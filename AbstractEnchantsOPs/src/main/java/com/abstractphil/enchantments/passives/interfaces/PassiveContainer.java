package com.abstractphil.enchantments.passives.interfaces;

import com.abstractphil.enchantments.AbstractEnchantments;
import com.abstractphil.enchantments.cfg.PassivesData;
import com.abstractphil.enchantments.cfg.PassivesDetails;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import com.redmancometh.reditems.RedItems;
import com.redmancometh.reditems.abstraction.Effect;
import com.redmancometh.reditems.storage.EnchantData;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PassiveContainer implements Effect {
    SharedUtils utils;
    Map<String, PassivesData> data;
    PassivesDetails details;

    public void setDetails(PassivesDetails detailsIn){
        details = detailsIn;
    }
    public PassivesDetails getDetails(){ return details; }
    public void setUtils(SharedUtils utilsIn) {
        utils = utilsIn;
    }
    public SharedUtils getUtils() {
        return utils;
    }
    public void setData(Map<String, PassivesData> dataIn) {
        data = dataIn;
    }
    public Map<String, PassivesData> getData(){
        return data;
    }

    @Override
    public List<String> getLore() {
        return new LinkedList<>();
    }

    @Override
    public String getName() {
        return details.getName();
    }

    @Override
    public EnchantType getType() {
        return null;
    }

    @Override
    public int getMaxNaturalLevel() {
        return (data.size() -1) * 10000;
    }
}
