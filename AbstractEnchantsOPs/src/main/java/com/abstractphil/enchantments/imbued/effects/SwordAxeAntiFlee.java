package com.abstractphil.enchantments.imbued.effects;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.imbued.interfaces.ImbuedWeapon;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.EnchantType;
import com.redmancometh.reditems.abstraction.TickingWeaponEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class SwordAxeAntiFlee extends ImbuedClazz implements ImbuedWeapon, TickingWeaponEffect {
    ImbuedData imbuedData;
    SharedUtils utils;

    public SwordAxeAntiFlee(ImbuedData dataIn) {
        super(dataIn);
    }
    public SwordAxeAntiFlee(){super();}

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

    @Override
    public void hit(EntityDamageEvent entityDamageEvent, int i) {
        if(entityDamageEvent.isCancelled()) return;
        if(!utils.randomCheck((int)imbuedData.getChance())) return;
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)entityDamageEvent;
        Entity victim = event.getEntity();
        Entity attacker = event.getDamager();
        if(victim != null && attacker != null) {
            // Chance trigger to pull in.
            Vector from = new Vector(victim.getLocation().getX(),
                    victim.getLocation().getY(),
                    victim.getLocation().getZ());
            Vector to  = new Vector(attacker.getLocation().getX(),
                    attacker.getLocation().getY(),
                    attacker.getLocation().getZ());

            Vector vectorTo = to.subtract(from);
            victim.setVelocity(vectorTo.subtract(
                    new Vector(vectorTo.getX() / getData().getArgs()[0],
                            vectorTo.getY() / getData().getArgs()[0],
                            vectorTo.getZ() / getData().getArgs()[0])));
            vectorTo.normalize();
        }
    }

    @Override
    public void onTick(Player player, int i) {

    }
}
