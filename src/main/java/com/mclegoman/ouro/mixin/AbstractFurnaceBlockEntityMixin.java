/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.mixin;

import com.mclegoman.ouro.entity.OuroPlayer;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = AbstractFurnaceBlockEntity.class, priority = 100)
public abstract class AbstractFurnaceBlockEntityMixin implements RecipeUnlocker {
	@Override
	public boolean shouldCraftRecipe(ServerPlayerEntity player, RecipeEntry<?> recipe) {
		if (recipe != null && !((OuroPlayer)player).ouro$canCraft(recipe.id().getValue())) return false;
		return RecipeUnlocker.super.shouldCraftRecipe(player, recipe);
	}
}
