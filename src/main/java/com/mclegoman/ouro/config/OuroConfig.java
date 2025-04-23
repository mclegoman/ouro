/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.config;

import com.mclegoman.ouro.simpleconfig.SimpleConfig;

public class OuroConfig {
	private SimpleConfig values = SimpleConfig.of("ouro").request();
	public static OuroConfig config = new OuroConfig();
	public static int getUses(String recipeId) {
		return config.values.getOrDefault(recipeId, 1);
	}
}