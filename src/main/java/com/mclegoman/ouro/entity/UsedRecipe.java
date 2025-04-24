/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.entity;

import com.mclegoman.ouro.config.OuroConfig;
import net.minecraft.util.Identifier;

import java.util.Map;

public class UsedRecipe {
	public static boolean canCraft(Identifier recipeId, Map<Identifier, UsedRecipeData> usedRecipes) {
		return usedRecipes.get(recipeId) == null || usedRecipes.get(recipeId).getCount() < OuroConfig.getUses(recipeId.toString()) || OuroConfig.getUses(recipeId.toString()) < 0;
	}
}
