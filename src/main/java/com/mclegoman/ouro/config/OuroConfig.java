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
		if (!configProvider.contains(recipeId)) configProvider.add(recipeId, 1);
		return config.getOrDefault(recipeId, 1);
	}
	static {
		config = SimpleConfig.of("ouro").provider(configProvider = new ConfigProvider()).request();
	}
}