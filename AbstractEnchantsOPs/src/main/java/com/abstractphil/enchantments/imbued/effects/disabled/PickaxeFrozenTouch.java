package com.abstractphil.enchantments.imbued.effects.disabled;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.effects.ImbuedClazz;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedTool;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import com.redmancometh.reditems.abstraction.TickingWeaponEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

//Pickaxe enchant with a high chance of getting double blocks and large explosions
public class PickaxeFrozenTouch extends ImbuedClazz implements ImbuedTool, TickingWeaponEffect {
    ImbuedData imbuedData;

    HashMap<Block, Integer> startingCounters = new HashMap<>();
    HashMap<Block, Integer> iceBleedPoints = new HashMap<>();
    HashMap<Block, Collection<ItemStack>> oldDrops = new HashMap<Block, Collection<ItemStack>>();
    SharedUtils utils;

    public PickaxeFrozenTouch(ImbuedData dataIn) {
        super(dataIn);
    }
    public PickaxeFrozenTouch(){super();}

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
        return EnchantType.PICKAXE;
    }

    @Override
    public int getMaxNaturalLevel() {
        return 1;
    }

    private double getMaxTicks() { return imbuedData.getArgs()[2]; }
    private double getMaxBlockSpread() { return imbuedData.getArgs()[1]; }

    @Override
    public void onTick(Player player, int i) {
        iterateStartingPoints(player);
        iterateIceBleed(player);
    }

    @Override
    public void broke(BlockBreakEvent blockBreakEvent, int i) {
        // Turns blocks into ice slowly, causing large sections to shatter when hit.
        if(blockBreakEvent.isCancelled()) return;
        // Todo: implement ice replacement for block materials, not impacting rewards.
        Player player = blockBreakEvent.getPlayer();
        if(player != null && utils.CanUseMiningEnchants(player)) {
            startingCounters.put(blockBreakEvent.getBlock(), (int)getMaxTicks());
        }
    }

    private void iterateStartingPoints(Player player)
    {
        for (Iterator<Block> currentBlock = startingCounters.keySet().iterator(); currentBlock.hasNext();) {
            Block block = currentBlock.next();
            int value = startingCounters.get(block);
            startingCounters.put(block, value - 1);
            if (value-1 < 0) {
                // remove the starting point.
                currentBlock.remove();
            }
            else if(value % 20 == 1) {
                // get relative blocks
                int x1 = -1; int x2 = 1; int y1 = -1; int y2 = 1; int z1 = -1; int z2 = 1;
                for(int nx = x1; nx <= x2; nx++) {
                    for(int ny = y1; ny <= y2; ny++) {
                        for(int nz = z1; nz <= z2; nz++) {
                            // todo: check if getrelative won't work.
                            Block blk = block.getRelative(nx, ny, nz);
                            if(blk.getType() != Material.ICE && blk.getType() != Material.BEDROCK) {
                                oldDrops.put(blk, blk.getDrops());
                                blk.setType(Material.ICE);
                                iceBleedPoints.put(blk, (int)getData().getArgs()[0]);
                                // todo: Fork for quad block on initial break.
                            }
                        }
                    }
                }
            }
        }
    }

    private void iterateIceBleed(Player player) {

    }

}

