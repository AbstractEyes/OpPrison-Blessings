package org.pvpingmc.tokens.utils;

import java.text.NumberFormat;

public class PlaceholderInjector {	
	private String formatNumber(double amount) {
		int exp = (int) (Math.log(amount) / Math.log(1000));

		if (amount >= 1000000000000000000000.0) {
			return "[Number Error]";
		}
		if (amount >= 1000000000000000000.0) {
			return String.format("%.2fQuin", amount / Math.pow(1000, exp));
		}
		if (amount >= 1000000000000000.0) {
			return String.format("%.2fQuad", amount / Math.pow(1000, exp));
		}
		if (amount >= 1000000000000.0) {
			return String.format("%.2fT", amount / Math.pow(1000, exp));
		}
		if (amount >= 1000000000.0) {
			return String.format("%.2fB", amount / Math.pow(1000, exp));
		}
		if (amount >= 1000000.0) {
			return String.format("%.2fM", amount / Math.pow(1000, exp));
		}
		if (amount >= 1000.0) {
			return String.format("%.2fk", amount / Math.pow(1000, exp));
		}
		
		return NumberFormat.getInstance().format(amount);
	}
}
