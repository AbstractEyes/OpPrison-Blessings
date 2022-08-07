package com.abstractphil.enchantments.imbued.effects;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedTool;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

// Guaranteed to provide another ore while mining.
public class PantsKickdown extends ImbuedClazz implements ImbuedTool {
    ImbuedData imbuedData;
    SharedUtils utils;

    public PantsKickdown(ImbuedData dataIn) {
        super(dataIn);
    }
    public PantsKickdown(){super();}

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
        if(blockBreakEvent.isCancelled()) return;
    }

}
