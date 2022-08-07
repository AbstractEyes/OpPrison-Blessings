package com.abstractphil.enchantments.cfg;

import lombok.Data;

import java.util.Map;

@Data
public class PassivesConfig {
	private PassivesDetails passivesDetails;
	private Map<String, PassivesData> passives;
}
