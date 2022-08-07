package com.abstractphil.enchantments.imbued.interfaces;

import com.abstractphil.enchantments.cfg.ImbuedData;
import com.abstractphil.enchantments.cfg.PassivesData;
import com.abstractphil.enchantments.utils.SharedUtils;
import com.redmancometh.reditems.abstraction.CharmEffect;
import com.redmancometh.reditems.abstraction.Effect;
import com.redmancometh.reditems.abstraction.TickingArmorEffect;

import java.util.List;

public interface Imbued extends Effect {
	public void setData(ImbuedData data);

	public ImbuedData getData();

	@Override
	default List<String> getLore() {
		return getData().getLore();
	}


	public void setUtils(SharedUtils utilsIn);
	public SharedUtils getUtils();

	@Override
	default String getName() {
		return getData().getName();
	}
}
