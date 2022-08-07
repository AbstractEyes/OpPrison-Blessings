package com.abstractphil.enchantments.utils;

import com.abstractphil.enchantments.AbstractEnchantments;
import com.abstractphil.enchantments.controller.PassivesController;
import com.abstractphil.enchantments.passives.interfaces.Passive;
import com.google.common.collect.PeekingIterator;
import com.koletar.jj.mineresetlite.Mine;
import com.koletar.jj.mineresetlite.MineManager;
import com.koletar.jj.mineresetlite.MineResetLite;
import com.redmancometh.reditems.RedItems;
import com.redmancometh.reditems.abstraction.Effect;
import com.redmancometh.reditems.mediator.EnchantManager;
import com.redmancometh.reditems.storage.EnchantData;
import net.minecraft.server.v1_8_R3.Tuple;
import ninja.coelho.arkjs.system.level.Region;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.pvpingmc.backpacks.backpack.Backpack;
import org.pvpingmc.backpacks.backpack.BackpackManager;
import org.pvpingmc.tokens.Main;
import org.pvpingmc.tokens.event.TokenEarnEvent;
import org.pvpingmc.tokens.tokens.TokenStorage;
import org.pvpingmc.tokens.utils.MRLUtils;
import org.pvpingmc.tokens.utils.Sounds;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SharedUtils {
    PassivesController passivesController = AbstractEnchantments.getInstance().passivesController();
    String mapName = "mapworld";
    HashMap<Block, Integer> expRates = new HashMap<>();

    HashMap<String, Region> regionMineWallCache = new HashMap();

    /*
        Mining functions.
     */
    public void setExpDrop(Block block, int exp){
        expRates.put(block, exp);
    }
    public int getExpDrop(Block block) {
        if(expRates.get(block) == null) return 0;
        return expRates.get(block);
    }

    public String translateColors(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private boolean findMineWallAt(Block block) {
        MineManager mineManager = MineResetLite.instance.getMineManager();
        Location blockLoc = block.getLocation();
        Iterator<Mine> mineIt = mineManager.getAll().iterator();
        Mine mineNext;
        while (mineIt.hasNext()) {
            mineNext = mineIt.next();
            if (mineNext.getWorld() != block.getWorld()) continue;

            Region mineRegion = mineNext.getRegion();
            Region mineRegionCached = regionMineWallCache.computeIfAbsent(mineNext.getName(), ( region -> mineRegion.expand(2, 3, 2)));
            if (mineRegionCached.contains(blockLoc) && !mineRegion.contains(blockLoc.getX(), blockLoc.getY(), blockLoc.getZ())) return true;
        }
        return false;
    }

    public boolean CanUseMiningEnchants(Player player) {
        return isHoldingPickaxe(player) && mapName.equals("mapworld");
    }

    // Todo: check for passive item in inventory, should be intrinsic to reditems.
    public boolean HasPassiveItem(Player player) {
        if(passivesController.getPassiveItemTuple(player) != null)
            return true;
        return false;
    }

    public boolean checkPantsKickdown(Player player) {
        EnchantManager redEnchants = RedItems.getInstance().getEnchantManager();
        boolean isRedItem = redEnchants.isRedItem(player.getEquipment().getLeggings());
        List<EnchantData> isKickdownPants = redEnchants.getEffects(player.getEquipment().getLeggings());
        for (EnchantData enchant : isKickdownPants) {
            if (enchant.getEffect().getName().equalsIgnoreCase("pantskickdown")) return true;
        }
        return false;
    }

    public boolean hasExplosive(ItemStack item)
    {
        for (String loreLine : item.getItemMeta().getLore()) {
            if (loreLine.toLowerCase().startsWith("explo")) return true;
        }
        return false;
    }

    public boolean randomCheck(int chanceIn)
    {
        //Random check.
        return (ThreadLocalRandom.current().nextInt(100) < chanceIn);
    }

    // If mined correctly return true.
    public boolean mineBlock(Block blk, Player player, int amountToAdd) {
        if(blk != null && blk.getType() != Material.AIR) {
            if(canBreakBlock(blk)) {
                Material mat = autoSmelt(blk);
                ItemStack blkItem = new ItemStack(mat, 1);
                PeekingIterator<Map.Entry<Integer, Backpack>>  backpackList =
                        BackpackManager.getInstance().getFreeBackpacks(player);
                if (backpackList != null) {
                    while (backpackList.hasNext()) {
                        Map.Entry<Integer, Backpack> entry = (Map.Entry)backpackList.next();
                        Backpack backpack = (Backpack)entry.getValue();
                        int slot = (Integer)entry.getKey();
                        amountToAdd = backpack.addItem(blkItem);
                        player.getInventory().setItem(slot, backpack.getUpdatedBackpack(player.getInventory().getItem(slot)));
                        if(amountToAdd <= 0) break;
                    }
                    if(amountToAdd > 0) player.getInventory().addItem(blkItem);
                } else player.getInventory().addItem(blkItem);
                player.giveExp(getExpDrop(blk));

                // Bedrock literally doesn't matter.
                try {
                    player.incrementStatistic(Statistic.MINE_BLOCK, blk.getType());
                } catch (Exception ex) { }
                safeLooterCheck(player, blk);
                blk.setType(Material.AIR);
                return true;

            }
        }
        return false;
    }

    private int getDropCount(Block blk, int fortuneLevel)
    {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        double count = Math.max(random.nextInt(fortuneLevel + 2) - 1, 0);
        return (int)(Math.max(count, 1) * (count + 1));
    }

    // Looter functions.
    public boolean hasLooter(ItemStack stack) {
        if (stack != null && stack.getType() != Material.AIR) {
            if (stack.hasItemMeta() && stack.getItemMeta().hasLore()) {
                Iterator var2 = stack.getItemMeta().getLore().iterator();

                String stripStr;
                do {
                    if (!var2.hasNext()) {
                        return false;
                    }

                    String s = (String)var2.next();
                    stripStr = ChatColor.stripColor(s);
                } while(!stripStr.startsWith("Looter "));

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    private void safeLooterCheck(Player p, Block b) {
        if (!p.hasMetadata("LOOTER-COOLDOWN") ||
                p.getMetadata("LOOTER-COOLDOWN").isEmpty() ||
                ((MetadataValue)p.getMetadata("LOOTER-COOLDOWN").
                        get(0)).asLong() <= System.currentTimeMillis()) {
            Mine mine = MRLUtils.getMine(b);
            if (mine != null) {
                Location loc = b.getLocation();
                ItemStack item = p.getItemInHand();
                if (this.hasLooter(item) && p.getGameMode() != GameMode.CREATIVE && p.getInventory().firstEmpty() != -1) {
                    Iterator var8 = item.getItemMeta().getLore().iterator();

                    while(var8.hasNext()) {
                        String s = (String)var8.next();
                        String stripStr = ChatColor.stripColor(s);
                        if (stripStr.startsWith("Looter ")) {
                            boolean var11 = false;

                            int level;
                            try {
                                level = Integer.parseInt(stripStr.split(" ")[1]);
                                if (Main.MAYHEM_ENCHS) {
                                    level *= 4;
                                }
                            } catch (NumberFormatException var13) {
                                break;
                            }

                            if (level >= 1) {
                                this.executeLooter(p, level);
                            }
                            break;
                        }
                    }

                    p.setMetadata("LOOTER-COOLDOWN", new FixedMetadataValue(Main.getInstance(), System.currentTimeMillis() + 500L));
                }
            }
        }
    }
    private int getAmountOfTokens(int level) {
        double levelChance = Math.min(Math.max((double)level * 2.0E-4D, 0.05D), 0.45D);
        if (Math.random() > levelChance) {
            return 0;
        } else {
            int levelMin = (int)Math.ceil((double)level * 0.01D + 3.0D);
            int levelMax = (int)Math.ceil((double)level * 0.03D + 6.0D);
            return ThreadLocalRandom.current().nextInt(levelMin, levelMax + 1);
        }
    }
    private void executeLooter(Player p, int level) {
        int random = this.getAmountOfTokens(level);
        if (random > 0) {
            Bukkit.getServer().getPluginManager().callEvent(new TokenEarnEvent(p, random));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l+" + NumberFormat.getInstance().format((long)random) + " " + (random == 1 ? "TOKEN" : "TOKENS")));
            p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1.0F, 1.5F);
            TokenStorage.getInstance().giveTokens(p, random);
        }
    }

    // Clean the drops
    public Material autoSmelt(Block block) {
        if (block.getType() == Material.COBBLESTONE) return Material.STONE;
        if (block.getType() == Material.COAL_ORE) return Material.COAL;
        if (block.getType() == Material.REDSTONE_ORE) return Material.REDSTONE;
        if (block.getType() == Material.IRON_ORE) return Material.IRON_INGOT;
        if (block.getType() == Material.GOLD_ORE) return Material.GOLD_INGOT;
        if (block.getType() == Material.DIAMOND_ORE) return Material.DIAMOND;
        if (block.getType() == Material.EMERALD_ORE) return Material.EMERALD;
        if (block.getType() == Material.LAPIS_ORE) return Material.LAPIS_BLOCK;
        if (block.getType() == Material.QUARTZ_ORE) return Material.QUARTZ;
        if (block.getType() == Material.GLOWING_REDSTONE_ORE) return Material.REDSTONE_BLOCK;
        return block.getType();
    }
    // The block must be within a mine
    public boolean canBreakBlock(Block block) {
        Mine mine = MineResetLite.instance.getMine(block);
        if(mine == null) return false;
        try
        {
            if(mine.getRegion().getBounds().contains(block.getLocation()))
                if(!findMineWallAt(block) &&
                        mine.getRegion().getBounds().contains(block.getLocation())) { // Permission checks go here if necessary.
                    return true;
                }
            return false;
        }
        catch (NullPointerException ex)
        {
            if(block == null) return false;
            System.out.println("Block not null");
            if(mine.getRegion() == null) return false;
            System.out.println("Mine's region not null");
            if(mine.getRegion().getBounds() == null) return false;
            System.out.println("Mine's bounds not null");
        }
        return false;
    }
    // Is the player holding a pickaxe
    public boolean isHoldingPickaxe(Player player) {
        return player.getItemInHand().getType() == Material.DIAMOND_PICKAXE;
    }

    /*
        Passives and passive importance.
     */
    // The level of the player's quad block.
    public double quadBlockLevel(Player player) {
        Tuple<Integer, ItemStack> item = passivesController.getPassiveItemTuple(player);
        if(!isHoldingPickaxe(player) || item == null) return 0;
        int level = passivesController.getPassiveLevel(item.b(), "quadblocks");
        if(level <= 1) return 0;
        return level;
    }
    // * Enchantment specifics.
    public boolean QuadBlockRNGCheck(Player player) {
        double next = new Random().nextDouble() * 100.00;
        if(quadBlockLevel(player) * getPassive("quadblock").getData().getChancePerLevel() >= next)
        {
            return true;
        }
        return false;
    }

    public Passive getPassive(String passiveName) {
        return passivesController.getPassive(passiveName);
    }

    public int getPassiveLevel(Player player, Effect effect) {
        Tuple<Integer, ItemStack> item = passivesController.getPassiveItemTuple(player);
        if(!isHoldingPickaxe(player) || item == null) return 0;
        return passivesController.getPassiveLevel(item.b(), effect.getName());
        //return passivesController.getPassiveLevel(effect);
    }

}
