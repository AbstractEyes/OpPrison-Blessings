package org.pvpingmc.tokens.utils;

import com.abstractphil.enchantments.AbstractEnchantments;
import com.abstractphil.enchantments.cfg.ImbuedConfig;
import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.cfg.PassivesConfig;
import com.abstractphil.enchantments.cfg.PassivesData;
import com.redmancometh.reditems.RedItems;
import com.redmancometh.reditems.abstraction.Effect;
import com.redmancometh.reditems.mediator.EnchantManager;
import com.redmancometh.reditems.storage.EnchantData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.pvpingmc.tokens.configobjects.OPEnchantConfig;
import org.pvpingmc.tokens.configobjects.OPEnchantData;

import javax.annotation.Nullable;
import java.util.Map;

public class RedItemsUtils {
    Map<String, OPEnchantData> opcfg;
    Map<String, ImbuedData> icfg;
    Map<String, PassivesData> pcfg;

    public RedItemsUtils(Map<String, OPEnchantData> opConfigIn,
                         Map<String, ImbuedData> imbueConfigIn,
                         Map<String, PassivesData> passivesConfigIn) {
        opcfg = opConfigIn;
        icfg = imbueConfigIn;
        pcfg = passivesConfigIn;
    }

    public Map<String, OPEnchantData> opEnchantConfig() {
        return opcfg;
    }
    public Map<String, ImbuedData> imbuedConfig()
    {
        return icfg;
    }
    public Map<String, PassivesData> passivesConfig() {
        return pcfg;
    }

    public boolean isRedItem(ItemStack item) {
        return getEnchantManager().isRedItem(item);
    }

    public boolean checkImbueName(ItemStack item, String stringIn){
        if(isRedItem(item)) {
            for (EnchantData enchant : getEnchantManager().getEffects(item)) {
                if (enchant.getEffect().getName().equalsIgnoreCase(stringIn)) return true;
            }
        }
        return false;
    }

    @Nullable
    public ImbuedData getImbuedConfig(ItemStack item, String stringIn) {
        if(isRedItem(item)) {
            for (Map.Entry<String, ImbuedData> entry : imbuedConfig().entrySet()) {
                String str = entry.getKey();
                ImbuedData imbue = entry.getValue();
                if (str.equalsIgnoreCase(stringIn)) {
                    return imbue;
                }
            }
        }
        return null;
    }

    @Nullable
    public PassivesData getPassiveConfig(ItemStack item, String stringIn) {
        if(isRedItem(item)) {
            for (Map.Entry<String, PassivesData> entry : passivesConfig().entrySet()) {
                String str = entry.getKey();
                PassivesData imbue = entry.getValue();
                if (str.equalsIgnoreCase(stringIn)) {
                    return imbue;
                }
            }
        }
        return null;
    }

    public boolean enchantedWithEffect(ItemStack item, Effect effectIn){
        if(!isRedItem(item)) return false;
        if(getEnchantManager().hasEffect(item, effectIn)) return true;
        return false;
    }

    @Nullable
    public ItemStack getPassiveItem(Player player) {
        return AbstractEnchantments.getInstance().passivesController().getPassiveItem(player);
    }

    public EnchantManager getEnchantManager() {
        return RedItems.getInstance().getEnchantManager();
    }


}
