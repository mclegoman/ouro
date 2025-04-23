/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.entity;

import net.minecraft.util.Identifier;

import java.util.Map;

public interface OuroPlayer {
	Map<Identifier, UsedRecipeData> ouro$getCrafted();
	boolean ouro$canCraft(Identifier recipeId);
}
