/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.entity;

import com.mclegoman.ouro.config.OuroConfig;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class UsedRecipe {
	public static boolean canCraft(ResourceLocation recipeId, Map<ResourceLocation, UsedRecipeData> usedRecipes) {
		int maxUses = OuroConfig.getUses(recipeId.toString());
		return (usedRecipes.get(recipeId) == null || usedRecipes.get(recipeId).getCount() < maxUses || maxUses < 0) && maxUses != 0;
	}
}
