/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.mixin;

import com.mclegoman.ouro.entity.OuroPlayer;
import com.mclegoman.ouro.entity.UsedRecipeData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements OuroPlayer {
	@Unique
	private final Map<Identifier, UsedRecipeData> ouro$usedRecipes = new HashMap<>();
	@Inject(at = @At("RETURN"), method = "readCustomDataFromNbt")
	private void ouro$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		NbtElement usedRecipes = nbt.get("ouro_recipe_data");
		if (usedRecipes != null) usedRecipes.asCompound().ifPresent((recipes) -> recipes.forEach((id, data) -> {
			data.asCompound().ifPresent((nbtData) -> {
				NbtElement nbtDataCount = nbtData.get("count");
				int count = nbtDataCount != null ? nbtDataCount.asInt().orElse(1) : 1;
				ouro$addUsedRecipe(Identifier.of(id), new UsedRecipeData(count));
			});
		}));
		this.ouro$dfu(nbt);
	}
	@Inject(at = @At("RETURN"), method = "writeCustomDataToNbt")
	private void ouro$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		NbtCompound usedRecipes = new NbtCompound();
		this.ouro$usedRecipes.forEach((id, data) -> {
			NbtCompound nbtData = new NbtCompound();
			nbtData.put("count", NbtInt.of(data.getCount()));
			usedRecipes.put(id.toString(), nbtData);
		});
		nbt.put("ouro_recipe_data", usedRecipes);
	}
	@Inject(at = @At("RETURN"), method = "onRecipeCrafted")
	private void ouro$onRecipeCrafted(RecipeEntry<?> recipe, List<ItemStack> ingredients, CallbackInfo ci) {
		this.ouro$addUsedRecipe(recipe.id().getValue());
	}
	@Inject(at = @At("RETURN"), method = "copyFrom")
	private void ouro$copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		this.ouro$usedRecipes.putAll(((OuroPlayer)oldPlayer).ouro$getCrafted());
	}
	@Unique
	private void ouro$addUsedRecipe(Identifier recipeId, UsedRecipeData recipeData) {
		this.ouro$getCrafted().put(recipeId, recipeData);
	}
	@Unique
	private void ouro$addUsedRecipe(Identifier recipeId) {
		if (!this.ouro$getCrafted().containsKey(recipeId)) this.ouro$addUsedRecipe(recipeId, new UsedRecipeData());
		else this.ouro$getCrafted().get(recipeId).setCount((current) -> current + 1);
	}
	@Unique
	private void ouro$addUsedRecipe(String recipeId, UsedRecipeData recipeData) {
		try {
			this.ouro$addUsedRecipe(Identifier.of(recipeId), recipeData);
		} catch (Exception error) {
			System.out.println("Error adding used recipe: " + error.getLocalizedMessage());
		}
	}
	@Unique
	private void ouro$addUsedRecipe(String recipeId) {
		ouro$addUsedRecipe(recipeId, new UsedRecipeData());
	}
	public boolean ouro$canCraft(Identifier recipeId) {
		// We check if the recipe either hasn't been used, or is set to less than 1 use (this could be modified using a config/gamerule in future).
		return this.ouro$getCrafted().get(recipeId) == null || this.ouro$getCrafted().get(recipeId).getCount() < 1;
	}
	public Map<Identifier, UsedRecipeData> ouro$getCrafted() {
		return this.ouro$usedRecipes;
	}
	@Unique
	public void ouro$dfu(NbtCompound nbt) {
		// 1.0 didn't track recipe uses, 1.1 updated the nbt format to accommodate for this,
		// this function simply checks for the old data format and updates it.
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
}