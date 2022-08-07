package com.abstractphil.enchantments.imbued.effects;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedArmor;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedTool;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.koletar.jj.mineresetlite.Mine;
import com.koletar.jj.mineresetlite.MineResetLite;
import com.redmancometh.reditems.abstraction.ActiveEffect;
import com.redmancometh.reditems.abstraction.ArmorEffect;
import com.redmancometh.reditems.abstraction.BlockBreakEffect;
import com.redmancometh.warcore.util.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

// has a chance to destroy blocks under the player.
public class BootsCrushingBoots extends ImbuedClazz implements ArmorEffect, BlockBreakEffect {
    ImbuedData imbuedData;

    public BootsCrushingBoots(ImbuedData dataIn) {
        super(dataIn);
    }
    public BootsCrushingBoots(){super();}

    @Override
    public void setData(ImbuedData data) {
        imbuedData = data;
    }
    @Override
    public ImbuedData getData() {
        return imbuedData;
    }

    @Override
    public List<String> getLore() {
        return super.getLore();
    }

    SharedUtils utils;
    @Override
    public void setUtils(SharedUtils utilsIn)
    {
        utils = utilsIn;
    }
    @Override
    public SharedUtils getUtils() { return utils; }

    @Override
    public String getName() {
        return super.getName();
    }


    @Override
    public int getMaxNaturalLevel() {
        return 0;
    }

    @Override
    public List<Pair<String, Supplier<String>>> placeholders() {
        return super.placeholders();
    }

    @Override
    public boolean applicableFor(ItemStack item) {
        return super.applicableFor(item);
    }

    private boolean hasDrillHammer(ItemStack item) {
        if(item.hasItemMeta() && item.getItemMeta().hasLore()){
            List<String> lores = item.getItemMeta().getLore();
            for (String lore : lores) {
                if (lore.toLowerCase(Locale.ROOT).contains("drill")) return true;
            }
        }
        return false;
    }
    private boolean hasExplosive(ItemStack item) {
        if(item.hasItemMeta() && item.getItemMeta().hasLore()){
            List<String> lores = item.getItemMeta().getLore();
            for (String lore : lores) {
                if (lore.toLowerCase(Locale.ROOT).startsWith("explosive")) return true;
            }
        }
        return false;
    }
    public void broke(BlockBreakEvent blockBreakEvent, int i) {
        if(blockBreakEvent.isCancelled()) return;
        if(!utils.randomCheck((int)imbuedData.getChance())) return;
        // Get blocks below player to break.
        try {
            Player player = blockBreakEvent.getPlayer();
            if(player != null && utils.CanUseMiningEnchants(player)) {
                if(hasDrillHammer(player.getItemInHand()) ||
                        hasExplosive(player.getItemInHand())) return;
                int x1 = (int)-imbuedData.getArgs()[0];
                int x2 = (int)imbuedData.getArgs()[1];
                int y1 = (int)-imbuedData.getArgs()[2];
                int y2 = (int)imbuedData.getArgs()[3];
                int z1 = (int)-imbuedData.getArgs()[4];
                int z2 = (int)imbuedData.getArgs()[5];
                Location loc = blockBreakEvent.getPlayer().getLocation();
                int blocksBroken = 0;
                // TODO: crushing and crumbling sound effects for stepping.
                for(int nx = x1; nx <= x2; nx++) {
                    for(int ny = y1; ny <= y2; ny++) {
                        for(int nz = z1; nz <= z2; nz++) {
                            //System.out.println("x " + nx + " Y " + ny + " Z " + nz);
                            Block blk = null;
                            blk = blockBreakEvent.getBlock().getWorld().getBlockAt(
                                    (int)(loc.getBlockX()+nx),
                                    (int)(loc.getBlockY()+ny),
                                    (int)(loc.getBlockZ()+nz));
                            if(utils.mineBlock(blk, player, 1)) blocksBroken += 1;
                        }
                    }
                }
                Mine mine = MineResetLite.instance.getMine(blockBreakEvent.getBlock());
                if(mine != null)
                {
                    player.getWorld().playSound(
                            blockBreakEvent.getBlock().getLocation(),
                            Sound.EXPLODE, (float)0.15, (float)1.0);
                    mine.onBlockBreak(blocksBroken);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error: Crushing Boots malfunctioned");
            ex.printStackTrace();
        }

    }

}
