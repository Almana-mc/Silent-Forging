package me.almana.silentforging.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.silentchaos512.gear.crafting.recipe.ShapelessCompoundPartRecipe;

public final class GatedPartRecipeSerializer {
    private static final MapCodec<GatedPartRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CODEC.comapFlatMap(GatedPartRecipeSerializer::asCompoundPart, r -> r)
                    .fieldOf("delegate").forGetter(GatedPartRecipe::delegate)
    ).apply(instance, GatedPartRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, GatedPartRecipe> STREAM_CODEC = Recipe.STREAM_CODEC.map(
            recipe -> new GatedPartRecipe((ShapelessCompoundPartRecipe) recipe),
            GatedPartRecipe::delegate
    );

    private GatedPartRecipeSerializer() {
    }

    public static RecipeSerializer<GatedPartRecipe> create() {
        return new RecipeSerializer<>(CODEC, STREAM_CODEC);
    }

    private static DataResult<ShapelessCompoundPartRecipe> asCompoundPart(Recipe<?> recipe) {
        if (recipe instanceof ShapelessCompoundPartRecipe part) {
            return DataResult.success(part);
        }
        return DataResult.error(() -> "delegate is not a silentgear compound part recipe");
    }
}
