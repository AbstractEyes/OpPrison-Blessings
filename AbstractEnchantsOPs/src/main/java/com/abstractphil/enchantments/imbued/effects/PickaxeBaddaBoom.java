package com.abstractphil.enchantments.imbued.effects;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedTool;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.koletar.jj.mineresetlite.Mine;
import com.koletar.jj.mineresetlite.MineResetLite;
import com.redmancometh.reditems.EnchantType;
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

// Pickaxe effect that gives the chance to create LARGE explosions.
public class PickaxeBaddaBoom extends ImbuedClazz implements ImbuedTool {
    ImbuedData imbuedData;
    SharedUtils utils;

    public PickaxeBaddaBoom(ImbuedData dataIn) {
        super(dataIn);
    }
    public PickaxeBaddaBoom(){super();}

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

    public boolean hasExplosive(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        if (!(stack.hasItemMeta()) || !(stack.getItemMeta().hasLore())) return false;

        for (String s : stack.getItemMeta().getLore()) {
            String stripStr = ChatColor.stripColor(s);
            if (stripStr.startsWith("Explosive ")) return true;
        }
        return false;
    }

    @Override
    public void broke(BlockBreakEvent blockBreakEvent, int i) {
        // Destroy large scale area where the player swung their pickaxe.
        if(blockBreakEvent.isCancelled()) return;

        if(!utils.randomCheck((int)imbuedData.getChance())) return;
        try {
            Player player = blockBreakEvent.getPlayer();
            // Bypass for usual modified explosive enchants.
            if(player == null || hasExplosive(player.getItemInHand())) return;
            utils.setExpDrop(blockBreakEvent.getBlock(), blockBreakEvent.getExpToDrop());
            if(utils.CanUseMiningEnchants(player) && !utils.hasExplosive(player.getItemInHand())) {
                //player.sendMessage(imbuedData.getDisplayMessage());
                double x1 = -imbuedData.getArgs()[0];
                double x2 = imbuedData.getArgs()[1];
                double y1 = -imbuedData.getArgs()[2];
                double y2 = imbuedData.getArgs()[3];
                double z1 = -imbuedData.getArgs()[4];
                double z2 = imbuedData.getArgs()[5];
                int blocksBroken = 0;
                Location loc = blockBreakEvent.getBlock().getLocation();
                for(double nx = x1; nx <= x2; nx++) {
                    for(double ny = y1; ny <= y2; ny++) {
                        for(double nz = z1; nz <= z2; nz++) {
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
            System.out.println("Error: BADDA BOOM");
            ex.printStackTrace();
        }
    }

}
