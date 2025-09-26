/*
    Ouro (Only Use Recipe Once)
    Author: dannytaylor
    Github: https://github.com/mclegoman/ouro
    Licence: GNU LGPLv3
*/

package com.mclegoman.ouro.mixin;

import com.mclegoman.ouro.entity.OuroPlayer;
import com.mclegoman.ouro.entity.UsedRecipe;
import com.mclegoman.ouro.entity.UsedRecipeData;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin implements OuroPlayer {
	@Unique
    private static final Codec<Map<ResourceLocation, UsedRecipeData>> usedRecipesCodec = Codec.unboundedMap(ResourceLocation.CODEC, UsedRecipeData.codec);

	@Unique
	private final Map<ResourceLocation, UsedRecipeData> ouro$usedRecipes = new HashMap<>();

	@Inject(at = @At("RETURN"), method = "readAdditionalSaveData")
	private void ouro$readCustomDataFromNbt(ValueInput valueInput, CallbackInfo ci) {
		valueInput.read("ouro_recipe_data", usedRecipesCodec).ifPresent((map) -> {
			this.ouro$usedRecipes.clear();
			this.ouro$usedRecipes.putAll(map);
		});
		ouro$dfu(valueInput);
	}

	@Inject(at = @At("RETURN"), method = "addAdditionalSaveData")
	private void ouro$writeCustomDataToNbt(ValueOutput valueOutput, CallbackInfo ci) {
		valueOutput.store("ouro_recipe_data", usedRecipesCodec, this.ouro$usedRecipes);
	}

	@Inject(at = @At("RETURN"), method = "triggerRecipeCrafted")
	private void ouro$onRecipeCrafted(RecipeHolder<?> recipe, List<ItemStack> ingredients, CallbackInfo ci) {
		this.ouro$addUsedRecipe(recipe.id().location());
	}

	@Inject(at = @At("RETURN"), method = "restoreFrom")
	private void ouro$copyFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
		this.ouro$usedRecipes.putAll(((OuroPlayer)oldPlayer).ouro$getCrafted());
	}

	@Unique
	private void ouro$addUsedRecipe(ResourceLocation recipeId, UsedRecipeData recipeData) {
		this.ouro$getCrafted().put(recipeId, recipeData);
	}

	@Unique
	private void ouro$addUsedRecipe(ResourceLocation recipeId) {
		if (!this.ouro$getCrafted().containsKey(recipeId)) this.ouro$addUsedRecipe(recipeId, new UsedRecipeData());
		else this.ouro$getCrafted().get(recipeId).setCount((current) -> current + 1);
	}

	@Unique
	private void ouro$addUsedRecipe(String recipeId, UsedRecipeData recipeData) {
		try {
			this.ouro$addUsedRecipe(ResourceLocation.parse(recipeId), recipeData);
		} catch (Exception error) {
			System.out.println("Error adding used recipe: " + error.getLocalizedMessage());
		}
	}

	@Unique
	private void ouro$addUsedRecipe(String recipeId) {
		ouro$addUsedRecipe(recipeId, new UsedRecipeData());
	}

	public boolean ouro$canCraft(ResourceLocation recipeId) {
		return UsedRecipe.canCraft(recipeId, this.ouro$getCrafted());
	}

	public Map<ResourceLocation, UsedRecipeData> ouro$getCrafted() {
		return this.ouro$usedRecipes;
	}

	@Unique
	private void ouro$dfu(ValueInput valueInput) {
		// 1.1 used NBT to store recipe data, 1.2 now stores the entire map using codecs.
		valueInput.read("ouro_recipe_data", CompoundTag.CODEC).ifPresent(nbt -> nbt.forEach((key, element) -> element.asCompound().ifPresent(data -> {
			Tag countTag = data.get("count");
			int count = countTag != null ? countTag.asInt().orElse(1) : 1;
			this.ouro$usedRecipes.put(ResourceLocation.tryParse(key), new UsedRecipeData(count));
		})));
		// 1.0 didn't track recipe uses, 1.1 updated the nbt format to accommodate for this,
		// this function simply checks for the old data format and updates it.
		valueInput.read("ouro_used_recipes", Codec.list(Codec.STRING)).ifPresent(list -> list.forEach(this::ouro$addUsedRecipe));
	}
}