package me.almana.silentforging.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.almana.silentforging.forge.ForgeRule;
import me.almana.silentforging.setup.SfRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public record ForgingRecipe(
        Identifier sourceRecipe,
        int target,
        int range,
        List<ForgeRule> rules
) implements Recipe<SingleRecipeInput> {
    public static final MapCodec<ForgingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("source_recipe").forGetter(ForgingRecipe::sourceRecipe),
            Codec.INT.fieldOf("target").forGetter(ForgingRecipe::target),
            Codec.INT.optionalFieldOf("range", 3).forGetter(ForgingRecipe::range),
            ForgeRule.CODEC.listOf(1, 3).fieldOf("rules").forGetter(ForgingRecipe::rules)
    ).apply(instance, ForgingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ForgingRecipe> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, ForgingRecipe::sourceRecipe,
            ByteBufCodecs.VAR_INT, ForgingRecipe::target,
            ByteBufCodecs.VAR_INT, ForgingRecipe::range,
            ForgeRule.STREAM_CODEC.apply(ByteBufCodecs.list(3)), ForgingRecipe::rules,
            ForgingRecipe::new
    );

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return SfRecipes.FORGING.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return SfRecipes.FORGING_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
}
