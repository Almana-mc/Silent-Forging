package me.almana.silentforging.compat.jei;

import com.mojang.logging.LogUtils;
import me.almana.silentforging.recipe.DefaultForgeProfiles;
import me.almana.silentforging.recipe.ForgingRecipe;
import me.almana.silentforging.recipe.GatedPartRecipe;
import me.almana.silentforging.setup.SfDataComponents;
import me.almana.silentforging.setup.SfRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.silentchaos512.gear.crafting.ingredient.BlueprintIngredient;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.crafting.recipe.ShapelessCompoundPartRecipe;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ForgingDisplays {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ForgingDisplays() {
    }

    public static List<ForgingDisplay> build() {
        List<ForgingDisplay> displays = new ArrayList<>();
        RecipeMap recipes = ForgeRecipeSnapshot.recipes();
        Map<Identifier, ForgingRecipe> bySource = new LinkedHashMap<>();
        for (DefaultForgeProfiles.Entry entry : DefaultForgeProfiles.ENTRIES) {
            bySource.put(entry.sourceId(), entry.recipe());
        }
        for (RecipeHolder<ForgingRecipe> holder : recipes.byType(SfRecipes.FORGING_TYPE.get())) {
            bySource.put(holder.value().sourceRecipe(), holder.value());
        }
        int missingDelegate = 0;
        int noClassify = 0;
        for (ForgingRecipe profile : bySource.values()) {
            CraftingRecipe delegate = resolveDelegate(recipes, profile.sourceRecipe());
            if (delegate == null) {
                missingDelegate++;
                continue;
            }
            Ingredient blueprint = null;
            int materialCost = 0;
            List<Ingredient> extras = new ArrayList<>();
            for (Ingredient ingredient : delegate.placementInfo().ingredients()) {
                ICustomIngredient custom = ingredient.getCustomIngredient();
                if (custom instanceof BlueprintIngredient) {
                    blueprint = ingredient;
                } else if (custom instanceof PartMaterialIngredient) {
                    materialCost++;
                } else {
                    extras.add(ingredient);
                }
            }
            if (blueprint == null || materialCost == 0) {
                noClassify++;
                continue;
            }
            displays.add(new ForgingDisplay(blueprint, List.copyOf(extras), materialCost,
                    profile.target(), profile.range(), profile.rules(), delegate));
        }
        LOGGER.info("JEI tool forge: {} profiles, {} displays ({} missing delegate, {} unclassified)",
                bySource.size(), displays.size(), missingDelegate, noClassify);
        return displays;
    }

    public static ItemStack assemble(ForgingDisplay display, ItemStack material) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
        display.blueprint().items().findFirst()
                .ifPresent(holder -> stacks.set(0, new ItemStack(holder.value())));
        for (int i = 0; i < display.materialCost(); i++) {
            ItemStack stack = material.copyWithCount(1);
            stack.remove(SfDataComponents.FORGE_QUALITY.get());
            stacks.set(1 + i, stack);
        }
        List<Ingredient> extras = display.extras();
        for (int i = 0; i < extras.size(); i++) {
            int slot = 1 + display.materialCost() + i;
            extras.get(i).items().findFirst()
                    .ifPresent(holder -> stacks.set(slot, new ItemStack(holder.value())));
        }
        return display.recipe().assemble(CraftingInput.of(3, 3, stacks));
    }

    private static CraftingRecipe resolveDelegate(RecipeMap recipes, Identifier source) {
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, source);
        RecipeHolder<?> holder = recipes.byKey(key);
        if (holder == null) {
            return null;
        }
        Recipe<?> recipe = holder.value();
        if (recipe instanceof GatedPartRecipe gated) {
            return gated;
        }
        if (recipe instanceof ShapelessCompoundPartRecipe compound) {
            return compound;
        }
        return null;
    }
}
