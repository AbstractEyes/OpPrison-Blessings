package com.abstractphil.enchantments.imbued.effects;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedTool;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.koletar.jj.mineresetlite.Mine;
import com.koletar.jj.mineresetlite.MineResetLite;
import com.redmancometh.reditems.EnchantType;
import com.redmancometh.reditems.abstraction.ArmorEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;

public class PickaxeMegaDrill extends ImbuedClazz implements ArmorEffect, ImbuedTool {
    ImbuedData imbuedData;
    SharedUtils utils;

    public PickaxeMegaDrill(ImbuedData dataIn) {
        super(dataIn);
    }
    public PickaxeMegaDrill(){super();}

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
        return 0;
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

    @Override
    public void broke(BlockBreakEvent blockBreakEvent, int i) {
        // Mega drill allows a player to drill an entire line of blocks to bedrock.
        // Todo: implement proc chance and math.
        if(blockBreakEvent.isCancelled()) return;
        if(!utils.randomCheck((int)imbuedData.getChance())) return;
        try {
            Player player = blockBreakEvent.getPlayer();
            if (player != null && utils.CanUseMiningEnchants(player))
            {
                // Drill hammer exists, replace with pvptokens version.
                if(hasDrillHammer(player.getItemInHand())) return;
                // Drill to bedrock.
                Location loc = blockBreakEvent.getBlock().getLocation();
                int blocksBroken = 0;
                for(int index = 0; index <= loc.getBlockY(); index += 1)
                {
                    Block blk = null;
                    blk = blockBreakEvent.getBlock().getWorld().getBlockAt(
                            (loc.getBlockX()), (index), (loc.getBlockZ()));
                    if(getUtils().mineBlock(blk, player, 1)) blocksBroken +=1;
                }
                Mine mine = MineResetLite.instance.getMine(blockBreakEvent.getBlock());
                if(mine != null)
                {
                    player.getWorld().playSound(
                            blockBreakEvent.getBlock().getLocation(),
                            Sound.DIG_STONE, (float)0.2, (float)1.0);
                    mine.onBlockBreak(blocksBroken);
                }
            }
        } catch (Exception ex) {
            System.out.println("ERROR: MEGA DRILL broke");
            ex.printStackTrace();
        }
    }

}
