/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public class UsedRecipeData {
	public static final Codec<UsedRecipeData> codec = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("count").forGetter(UsedRecipeData::getCount)).apply(instance, UsedRecipeData::new));
	private int count;
	public UsedRecipeData() {
		this.count = 1;
	}
	public UsedRecipeData(int count) {
		this.count = count;
	}
	public int getCount() {
		return this.count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void setCount(Function<Integer, Integer> count) {
		this.setCount(count.apply(this.getCount()));
	}
}
