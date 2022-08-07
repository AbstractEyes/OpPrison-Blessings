package com.abstractphil.enchantments.imbued.effects.disabled;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.effects.ImbuedClazz;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedTool;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import com.redmancometh.reditems.abstraction.ClickEffect;
import com.redmancometh.reditems.abstraction.ClickedCharm;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

//Pickaxe enchant with a high chance of getting double blocks and large explosions
public class PickaxeMeteorStrike extends ImbuedClazz implements ImbuedTool, ClickEffect, ClickedCharm {
    ImbuedData imbuedData;
    SharedUtils utils;

    public PickaxeMeteorStrike(ImbuedData dataIn) {
        super(dataIn);
    }
    public PickaxeMeteorStrike(){super();}

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
        return null;
    }

    @Override
    public int getMaxNaturalLevel() {
        return 1;
    }

    @Override
    public void broke(BlockBreakEvent blockBreakEvent, int i) {
        // Meteor strike, calls a meteor to hit the mine, destroying sections.
        // Todo: implement animations and meteor itself.
        if(blockBreakEvent.isCancelled()) return;
    }

    @Override
    public void onRightClick(PlayerInteractEvent playerInteractEvent, int i) {
        if(playerInteractEvent.isCancelled()) return;

    }

    @Override
    public void onLeftClick(PlayerInteractEvent playerInteractEvent, int i) {
        if(playerInteractEvent.isCancelled()) return;

    }

    @Override
    public void onTick(Player player, int i) {

    }
}
