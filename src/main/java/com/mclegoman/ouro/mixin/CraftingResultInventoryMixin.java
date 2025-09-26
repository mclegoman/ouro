/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.mixin;

import com.mclegoman.ouro.entity.OuroPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ResultContainer.class, priority = 100)
public abstract class CraftingResultInventoryMixin implements RecipeCraftingHolder {
	@Override
	public boolean setRecipeUsed(ServerPlayer player, RecipeHolder<?> recipe) {
		if (recipe != null && !((OuroPlayer)player).ouro$canCraft(recipe.id().location())) return false;
		return RecipeCraftingHolder.super.setRecipeUsed(player, recipe);
	}
}
