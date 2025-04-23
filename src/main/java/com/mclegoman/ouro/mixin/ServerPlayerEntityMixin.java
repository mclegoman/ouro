/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.mixin;

import com.mclegoman.ouro.entity.OuroPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements OuroPlayer {
	@Unique
	private final List<Identifier> ouro$usedRecipes = new ArrayList<>();
	@Inject(at = @At("RETURN"), method = "readCustomDataFromNbt")
	private void ouro$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		NbtElement usedRecipes = nbt.get("ouro_used_recipes");
		if (usedRecipes != null) {
			Optional<NbtList> usedRecipesList = usedRecipes.asNbtList();
			if (usedRecipesList.isPresent()) {
				for (NbtElement id : usedRecipesList.get()) {
					id.asString().ifPresent(this::ouro$addUsedRecipe);
				}
			}
		}
	}
	@Inject(at = @At("RETURN"), method = "writeCustomDataToNbt")
	private void ouro$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		NbtList usedRecipes = new NbtList();
		for (Identifier id : this.ouro$usedRecipes) usedRecipes.add(NbtString.of(id.toString()));
		nbt.put("ouro_used_recipes", usedRecipes);
	}
	@Inject(at = @At("RETURN"), method = "onRecipeCrafted")
	private void ouro$onRecipeCrafted(RecipeEntry<?> recipe, List<ItemStack> ingredients, CallbackInfo ci) {
		ouro$addUsedRecipe(recipe.id().getValue());
	}
	@Unique
	private void ouro$addUsedRecipe(Identifier recipeId) {
		if (!this.ouro$usedRecipes.contains(recipeId)) this.ouro$usedRecipes.add(recipeId);
		System.out.println("DEBUG: " + this.ouro$usedRecipes);
	}
	@Unique
	private void ouro$addUsedRecipe(String recipeId) {
		try {
			ouro$addUsedRecipe(Identifier.of(recipeId));
		} catch (Exception error) {
			System.out.println("Error adding used recipe: " + error.getLocalizedMessage());
		}
	}
	@Override
	public boolean ouro$canCraft(Identifier recipeId) {
		return !this.ouro$usedRecipes.contains(recipeId);
	}
}