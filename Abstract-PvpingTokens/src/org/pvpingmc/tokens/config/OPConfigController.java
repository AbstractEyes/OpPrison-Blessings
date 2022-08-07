package org.pvpingmc.tokens.config;

import com.abstractphil.enchantments.cfg.ImbuedConfig;
import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.cfg.PassivesConfig;
import com.abstractphil.enchantments.cfg.PassivesData;
import com.redmancometh.configcore.config.ConfigManager;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.pvpingmc.tokens.configobjects.OPEnchantConfig;
import org.pvpingmc.tokens.configobjects.OPEnchantData;
import org.pvpingmc.tokens.utils.RedItemsUtils;

import java.util.Map;

@Data
public class OPConfigController {
    private ConfigManager<OPEnchantConfig> enchantConfigs = new ConfigManager("opminingenchants.json", OPEnchantConfig.class);
    private ConfigManager<ImbuedConfig> imbueConfigs = new ConfigManager("imbued.json", ImbuedConfig.class);
    private ConfigManager<PassivesConfig> passiveConfigs = new ConfigManager("passives.json", PassivesConfig.class);
    private Map<String, OPEnchantData> configOPData;
    private Map<String, ImbuedData> configImbueData;
    private Map<String, PassivesData> configPassiveImbueData;
    @Getter @Setter private RedItemsUtils utils;

    public void init() {
        enchantConfigs.init();
        imbueConfigs.init();
        passiveConfigs.init();
        //OPEnchantConfig cfg = enchantConfigs.getConfig();
        configOPData = enchantConfigs.getConfig().getOpenchants();
        configImbueData = imbueConfigs.getConfig().getImbued();
        configPassiveImbueData = passiveConfigs.getConfig().getPassives();
        utils = new RedItemsUtils(configOPData, configImbueData, configPassiveImbueData);
        //SharedUtils utils = new SharedUtils();
    }

    public RedItemsUtils utils()
    {
        return utils;
    }

    private Map<String, OPEnchantData> getConfigOPData() {
        return configOPData;
    }

}
