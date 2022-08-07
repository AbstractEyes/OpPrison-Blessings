package org.pvpingmc.tokens.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.pvpingmc.tokens.Main;

import com.koletar.jj.mineresetlite.Mine;
import com.koletar.jj.mineresetlite.MineResetLite;

import ninja.coelho.arkjs.system.level.Region;

public class MRLUtils {
	
	private static final Map<String, Region> mineNearbyCache = new HashMap<>();
	
	public static Mine getMine(Block b) {
		return MineResetLite.instance.getMine(b);
	}
	
	public static Mine getMine(Location loc) {
		return MineResetLite.instance.getMine(loc);
	}

	public static Mine getMineNearby(Location loc) {
		Iterator<Mine> mineIt = MineResetLite.instance.mines.iterator();
		Mine mineNext = null;
		while (mineIt.hasNext()) {
			mineNext = mineIt.next();
			if (mineNext.getWorld() != loc.getWorld()) continue;

			Region region = mineNext.getRegion();
			Region regionCached = mineNearbyCache.computeIfAbsent(mineNext.getName(), k -> region.expand(1, 12, 1));
			if (regionCached.contains(loc)) return mineNext;
		}
		return null;
	}
	
	public static void countBreak(Mine mine, int blocks) {
		if (Main.getInstance().isMrlEnabled() && mine != null && mine.getPercentageToReset() > 0) {
			mine.onBlockBreak(blocks);
		}
	}
}
