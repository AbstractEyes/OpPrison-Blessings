package com.abstractphil.enchantments.imbued.effects;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedWeapon;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import com.redmancometh.reditems.abstraction.TickingWeaponEffect;
import com.redmancometh.warcore.util.Pair;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class SwordAxePrisonAssault extends ImbuedClazz implements TickingWeaponEffect, ImbuedWeapon {
    ImbuedData imbuedData;
    SharedUtils utils;

    public SwordAxePrisonAssault(ImbuedData dataIn) {
        super(dataIn);
    }
    public SwordAxePrisonAssault(){super();}

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

    @Override
    public void hit(EntityDamageEvent entityDamageEvent, int i) {
        if(entityDamageEvent.isCancelled()) return;
        if(!utils.randomCheck((int)imbuedData.getChance())) return;
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)entityDamageEvent;
        Entity victim = event.getEntity();
        if(victim != null && event.getDamager() != null) {
            event.getDamager().sendMessage(imbuedData.getDisplayMessage());
            entityDamageEvent.setDamage(entityDamageEvent.getDamage() * imbuedData.getArgs()[0]);
        }
    }

    @Override
    public void onTick(Player player, int i) {

    }
}
