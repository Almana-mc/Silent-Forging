package me.almana.silentforging.data;

import me.almana.silentforging.setup.SfItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class SfRecipeProvider extends RecipeProvider {
    public SfRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        Ingredient anyPickaxe = Ingredient.of(
                Items.WOODEN_PICKAXE,
                Items.STONE_PICKAXE,
                Items.IRON_PICKAXE,
                Items.GOLDEN_PICKAXE,
                Items.DIAMOND_PICKAXE,
                Items.NETHERITE_PICKAXE
        );

        shaped(RecipeCategory.DECORATIONS, SfItems.TOOL_FORGE.get())
                .define('A', Blocks.ANVIL)
                .define('P', anyPickaxe)
                .define('S', Tags.Items.TOOLS_SHEAR)
                .define('C', Blocks.CAMPFIRE)
                .pattern("   ")
                .pattern("PAS")
                .pattern(" C ")
                .unlockedBy("has_anvil", has(Blocks.ANVIL))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new SfRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Silent Forging recipes";
        }
    }
}
