/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.config;

import com.mclegoman.ouro.simpleconfig.SimpleConfig;

public class OuroConfig {
	protected static SimpleConfig config;
	protected static ConfigProvider configProvider;
	public static int getUses(String recipeId) {
		if (recipeId.equals("*")) return get(recipeId, 1);
		return get(recipeId, get("*", 1));
	}
	public static int get(String id, int defaultValue) {
		if (!configProvider.contains(id)) configProvider.add(id, defaultValue);
		return config.getOrDefault(id, defaultValue);
	}
	static {
		config = SimpleConfig.of("ouro").provider(configProvider = new ConfigProvider()).request();
	}
}