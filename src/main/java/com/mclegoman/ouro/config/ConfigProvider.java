/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.config;

import com.mclegoman.ouro.simpleconfig.SimpleConfig;

import java.util.HashMap;
import java.util.Map;

public class ConfigProvider implements SimpleConfig.DefaultConfig {
	private String contents = "";
	private final Map<String, Object> configList = new HashMap<>();
	public void add(String id, Object value) {
		configList.put(id, value);
	}
	public boolean contains(String id) {
		return configList.containsKey(id);
	}
	@Override
	public String get(String namespace) {
		return contents;
	}
}
