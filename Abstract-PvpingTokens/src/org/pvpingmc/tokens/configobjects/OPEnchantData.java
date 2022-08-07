package org.pvpingmc.tokens.configobjects;

import lombok.Data;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

@Data
public class OPEnchantData {
    private Class<? extends OPEnchantData> clazz;
    private String name, displayName, displayMessage;
    private Map<String, Double> ranges, timers, multipliers;
    private Map<String, String> args;
}
