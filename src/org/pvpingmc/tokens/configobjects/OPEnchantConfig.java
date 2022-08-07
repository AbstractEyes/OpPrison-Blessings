package org.pvpingmc.tokens.configobjects;

import lombok.Data;

import java.util.Map;

@Data
public class OPEnchantConfig {
    Map<String, OPEnchantData> openchants;
}
