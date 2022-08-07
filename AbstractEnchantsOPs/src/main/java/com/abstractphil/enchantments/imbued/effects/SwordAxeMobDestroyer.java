package com.abstractphil.enchantments.imbued.effects;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedWeapon;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import com.redmancometh.reditems.abstraction.TickingWeaponEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class SwordAxeMobDestroyer extends ImbuedClazz implements ImbuedWeapon, TickingWeaponEffect {
    ImbuedData imbuedData;
    SharedUtils utils;

    public SwordAxeMobDestroyer(ImbuedData dataIn) {
        super(dataIn);
    }
    public SwordAxeMobDestroyer(){super();}

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
        return EnchantType.WEAPON;
    }

    @Override
    public int getMaxNaturalLevel() {
        return 1;
    }

    @Override
    public void hit(EntityDamageEvent entityDamageEvent, int i) {
        if(entityDamageEvent.isCancelled()) return;
        if(!utils.randomCheck((int)imbuedData.getChance())) return;
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)entityDamageEvent;
        Entity victim = event.getEntity();
        if(victim.getType() != EntityType.PLAYER) {
            entityDamageEvent.setDamage(entityDamageEvent.getDamage() * imbuedData.getArgs()[0]);
        }
    }

    @Override
    public void onTick(Player player, int i) {

    }
}
