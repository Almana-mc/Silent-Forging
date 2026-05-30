package me.almana.silentforging.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.almana.silentforging.forge.ForgeQuality;
import me.almana.silentforging.forge.ForgeRule;
import me.almana.silentforging.setup.SfDataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;
import net.silentchaos512.gear.crafting.recipe.ShapelessCompoundPartRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class SilentGearPartRecipes {
    private static final Map<Identifier, CapturedRecipe> PART_RECIPES = new HashMap<>();
    private static final Set<String> CRAFTABLE_PARTS = Set.of("part/tip", "part/coating");

    private SilentGearPartRecipes() {
    }

    public static void captureAndRemove(ModifyRecipeJsonsEvent event) {
        PART_RECIPES.clear();
        Map<Identifier, JsonElement> rewrites = new HashMap<>();
        for (Map.Entry<Identifier, JsonElement> entry : event.getRecipeJsons().entrySet()) {
            Identifier id = entry.getKey();
            if (!isSilentGearCompoundRecipeId(id)) {
                continue;
            }
            JsonElement original = entry.getValue();
            Optional<Recipe<?>> decoded = Recipe.CODEC.parse(event.getOps(), original).result();
            if (decoded.isPresent() && decoded.get() instanceof ShapelessCompoundPartRecipe recipe) {
                ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);
                PART_RECIPES.put(id, new CapturedRecipe(
                        new RecipeHolder<>(key, recipe),
                        materialCost(original),
                        staticIngredients(event, original)
                ));
                if (!CRAFTABLE_PARTS.contains(id.getPath())) {
                    rewrites.put(id, wrap(original));
                }
            }
        }
        event.getRecipeJsons().putAll(rewrites);
    }

    private static JsonObject wrap(JsonElement original) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "silentforging:gated_part");
        json.add("delegate", original);
        return json;
    }

    public static Optional<Match> match(ForgingRecipe profile, ItemStack blueprint, ItemStack material, Level level) {
        return match(profile, blueprint, material, level, ForgeQuality.NORMAL);
    }

    public static Optional<Match> match(ForgingRecipe profile, ItemStack blueprint, ItemStack material, Level level, ForgeQuality quality) {
        CapturedRecipe captured = PART_RECIPES.get(profile.sourceRecipe());
        if (captured == null || blueprint.isEmpty() || material.isEmpty() || material.getCount() < captured.materialCost()) {
            return Optional.empty();
        }
        CraftingInput input = input(blueprint, material, captured.materialCost(), captured.staticIngredients(), quality);
        ShapelessCompoundPartRecipe recipe = captured.holder().value();
        if (recipe.matches(input, level)) {
            ItemStack result = recipe.assemble(input);
            if (!result.isEmpty()) {
                return Optional.of(new Match(result, captured.materialCost()));
            }
        }
        return Optional.empty();
    }

    public static Optional<ProfileMatch> matchDefault(ItemStack blueprint, ItemStack material, Level level) {
        for (DefaultForgeProfiles.Entry entry : DefaultForgeProfiles.ENTRIES) {
            ForgingRecipe recipe = entry.recipe();
            if (match(recipe, blueprint, material, level).isPresent()) {
                return Optional.of(new ProfileMatch(entry.profileId().toString(), recipe));
            }
        }
        for (Identifier sourceId : PART_RECIPES.keySet()) {
            ForgingRecipe recipe = fallbackRecipe(sourceId);
            if (match(recipe, blueprint, material, level).isPresent()) {
                return Optional.of(new ProfileMatch("silentforging:forging/" + sourceId.getPath(), recipe));
            }
        }
        return Optional.empty();
    }

    private static ForgingRecipe fallbackRecipe(Identifier sourceId) {
        return new ForgingRecipe(sourceId, 64, 5, List.of(ForgeRule.HIT_LAST));
    }

    private static boolean isSilentGearCompoundRecipeId(Identifier id) {
        return id.getNamespace().equals("silentgear")
                && (id.getPath().startsWith("gear/") || id.getPath().startsWith("part/"));
    }

    private static int materialCost(JsonElement json) {
        int count = 0;
        for (JsonElement ingredient : json.getAsJsonObject().getAsJsonArray("ingredients")) {
            if (isIngredientType(ingredient, "silentgear:material")) {
                count++;
            }
        }
        return count;
    }

    private static List<Ingredient> staticIngredients(ModifyRecipeJsonsEvent event, JsonElement json) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (JsonElement ingredientJson : json.getAsJsonObject().getAsJsonArray("ingredients")) {
            if (isIngredientType(ingredientJson, "silentgear:blueprint")
                    || isIngredientType(ingredientJson, "silentgear:material")) {
                continue;
            }
            Ingredient.CODEC.parse(event.getOps(), ingredientJson)
                    .result()
                    .ifPresent(ingredients::add);
        }
        return ingredients;
    }

    private static boolean isIngredientType(JsonElement ingredient, String type) {
        if (!ingredient.isJsonObject()) {
            return false;
        }
        JsonObject object = ingredient.getAsJsonObject();
        return object.has("neoforge:ingredient_type")
                && object.get("neoforge:ingredient_type").getAsString().equals(type);
    }

    private static CraftingInput input(ItemStack blueprint, ItemStack material, int materialCount, List<Ingredient> staticIngredients, ForgeQuality quality) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
        stacks.set(0, blueprint.copyWithCount(1));
        for (int i = 0; i < materialCount; i++) {
            ItemStack materialStack = material.copyWithCount(1);
            if (quality == ForgeQuality.NORMAL) {
                materialStack.remove(SfDataComponents.FORGE_QUALITY);
            } else {
                materialStack.set(SfDataComponents.FORGE_QUALITY, quality);
            }
            stacks.set(i + 1, materialStack);
        }
        for (int i = 0; i < staticIngredients.size(); i++) {
            int slot = i + materialCount + 1;
            staticIngredients.get(i).items()
                    .findFirst()
                    .ifPresent(item -> stacks.set(slot, new ItemStack(item.value())));
        }
        return CraftingInput.of(3, 3, stacks);
    }

    private record CapturedRecipe(
            RecipeHolder<ShapelessCompoundPartRecipe> holder,
            int materialCost,
            List<Ingredient> staticIngredients
    ) {
    }

    public record Match(ItemStack result, int materialCost) {
    }

    public record ProfileMatch(String id, ForgingRecipe recipe) {
    }
}
