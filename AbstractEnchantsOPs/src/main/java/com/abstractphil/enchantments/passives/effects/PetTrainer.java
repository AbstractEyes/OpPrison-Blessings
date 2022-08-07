package com.abstractphil.enchantments.passives.effects;

import com.abstractphil.enchantments.cfg.PassivesData;
import com.abstractphil.enchantments.passives.interfaces.Passive;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import com.redmancometh.reditems.abstraction.BlockBreakEffect;
import com.redmancometh.reditems.abstraction.ClickEffect;
import com.redmancometh.reditems.abstraction.HeldEffect;
import com.redmancometh.reditems.abstraction.TickingWeaponEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

// accumulating chance to get quadruple blocks as a reward on top of other enchantments
public class PetTrainer extends Passive implements BlockBreakEffect, HeldEffect, ClickEffect, TickingWeaponEffect {
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
    public void onRightClick(PlayerInteractEvent playerInteractEvent, int i) {

    }

    @Override
    public void onLeftClick(PlayerInteractEvent playerInteractEvent, int i) {
    }

    @Override
    public void onTick(Player player, int i) {

    }

    @Override
    public EnchantType getType() {
        return EnchantType.ANY;
    }

    @Override
    public void broke(BlockBreakEvent blockBreakEvent, int i) { }
}