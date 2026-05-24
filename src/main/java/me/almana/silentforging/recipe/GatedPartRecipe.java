package me.almana.silentforging.recipe;

import me.almana.silentforging.setup.SfRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.crafting.recipe.ShapelessCompoundPartRecipe;

import java.util.List;

public final class GatedPartRecipe implements CraftingRecipe {
    private final ShapelessCompoundPartRecipe delegate;

    public GatedPartRecipe(ShapelessCompoundPartRecipe delegate) {
        this.delegate = delegate;
    }

    public ShapelessCompoundPartRecipe delegate() {
        return delegate;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (hasGatedMaterial(input)) {
            return false;
        }
        return delegate.matches(input, level);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return delegate.assemble(input);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return delegate.showNotification();
    }

    @Override
    public String group() {
        return delegate.group();
    }

    @Override
    public CraftingBookCategory category() {
        return delegate.category();
    }

    @Override
    public PlacementInfo placementInfo() {
        return delegate.placementInfo();
    }

    @Override
    public List<RecipeDisplay> display() {
        return delegate.display();
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return delegate.recipeBookCategory();
    }

    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return SfRecipes.GATED_PART.get();
    }

    private static boolean hasGatedMaterial(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            if (ForgeMaterialTags.isIngotOrGem(input.getItem(i))) {
                return true;
            }
        }
        return false;
    }
}
