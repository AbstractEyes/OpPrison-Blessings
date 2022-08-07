package com.abstractphil.enchantments.passives.interfaces;

import com.abstractphil.enchantments.cfg.PassivesData;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.abstraction.*;

import java.util.List;

public abstract class Passive implements Effect {
	public abstract void setUtils(SharedUtils utilsIn);
	public abstract SharedUtils getUtils();

	public abstract void setData(PassivesData data);
	public abstract PassivesData getData();

	@Override
	public List<String> getLore() {
		return getData().getDisplayLore();
	}

	public List<String> getDisplayLore() {
		return getData().getLore();
	}

	@Override
	public String getName() {
		return getData().getName();
	}

	@Override
	public int getMaxNaturalLevel() {
		return (int)getData().getMaxLevel();
	}
}
