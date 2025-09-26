/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.entity;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public interface OuroPlayer {
	Map<ResourceLocation, UsedRecipeData> ouro$getCrafted();
	boolean ouro$canCraft(ResourceLocation recipeId);
}
