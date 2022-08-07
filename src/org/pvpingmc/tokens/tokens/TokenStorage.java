package org.pvpingmc.tokens.tokens;

import org.bukkit.entity.Player;

import ninja.coelho.arkjs.extlib.ExtLibService;

public class TokenStorage {
	
	private static TokenStorage instance;

	public static TokenStorage getInstance() {
		if (instance == null) {
			synchronized (TokenStorage.class) {
				if (instance == null) {
					instance = new TokenStorage();
				}
			}
		}

		return instance;
	}

	public void giveTokens(Player p, int amt) {
		if (p == null) return;
		ExtLibService.get().currency("@tokens").give(p, amt, true);
	}

	public void takeTokens(Player p, int amt) {
		if (p == null) return;
		ExtLibService.get().currency("@tokens").take(p, amt);
	}

	public int getTokens(Player p) {
		if (p == null) return 0;
		return ExtLibService.get().currency("@tokens").get(p);
	}
}
