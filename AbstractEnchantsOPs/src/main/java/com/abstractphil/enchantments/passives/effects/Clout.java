package com.abstractphil.enchantments.passives.effects;

import com.abstractphil.enchantments.cfg.PassivesData;
import com.abstractphil.enchantments.passives.interfaces.Passive;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import com.redmancometh.reditems.abstraction.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Decrease enchantment token costs due to reputation
public class Clout extends Passive implements Effect {
    PassivesData passivesData;
    SharedUtils utils = new SharedUtils();
    @Override
    public void setData(PassivesData data) {
        passivesData = data;
    }

    @Override
    public void setUtils(SharedUtils utilsIn) {
        utils = utilsIn;
    }

    @Override
    public SharedUtils getUtils() {
        return utils;
    }

    @Override
    public PassivesData getData() {
        return passivesData;
    }

    @Override
    public EnchantType getType() {
        return EnchantType.ANY;
    }
}
