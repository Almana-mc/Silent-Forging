package me.almana.silentforging.setup;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.recipe.ForgingRecipe;
import me.almana.silentforging.recipe.GatedPartRecipe;
import me.almana.silentforging.recipe.GatedPartRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SfRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Silentforging.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Silentforging.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ForgingRecipe>> FORGING_TYPE =
            RECIPE_TYPES.register(
                    "forging",
                    () -> RecipeType.simple(Identifier.fromNamespaceAndPath(Silentforging.MODID, "forging"))
            );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ForgingRecipe>> FORGING =
            RECIPE_SERIALIZERS.register(
                    "forging",
                    () -> new RecipeSerializer<>(ForgingRecipe.CODEC, ForgingRecipe.STREAM_CODEC)
            );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GatedPartRecipe>> GATED_PART =
            RECIPE_SERIALIZERS.register(
                    "gated_part",
                    GatedPartRecipeSerializer::create
            );

    private SfRecipes() {
    }
}
