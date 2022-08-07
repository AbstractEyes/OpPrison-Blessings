package com.abstractphil.enchantments.imbued.effects.disabled;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.effects.ImbuedClazz;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedTool;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import org.bukkit.event.block.BlockBreakEvent;

//Pickaxe enchant with a high chance of getting double blocks and large explosions
public class PickaxeBlackHole extends ImbuedClazz implements ImbuedTool {
    ImbuedData imbuedData;
    SharedUtils utils;

    public PickaxeBlackHole(ImbuedData dataIn) {
        super(dataIn);
    }
    public PickaxeBlackHole() {
        super();
    }

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
        return EnchantType.ANY;
    }

    @Override
    public int getMaxNaturalLevel() {
        return 1;
    }

    @Override
    public void broke(BlockBreakEvent blockBreakEvent, int i) {
        // Looking forward to making this one.
        // Todo: create bleeding effect on blocks.
        if(blockBreakEvent.isCancelled()) return;
    }

}
