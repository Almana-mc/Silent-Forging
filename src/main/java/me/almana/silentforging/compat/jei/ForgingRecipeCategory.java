package me.almana.silentforging.compat.jei;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.forge.ForgeRule;
import me.almana.silentforging.recipe.ForgeMaterialTags;
import me.almana.silentforging.setup.SfItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class ForgingRecipeCategory implements IRecipeCategory<ForgingDisplay> {
    public static final IRecipeType<ForgingDisplay> TYPE =
            IRecipeType.create(Silentforging.MODID, "forging", ForgingDisplay.class);

    private static final int WIDTH = 150;
    private static final int HEIGHT = 70;
    private static final int SLOT_Y = 2;
    private static final int TEXT_TARGET = 0xFF3A3025;
    private static final int TEXT_RULE = 0xFF5A4A38;

    private static List<ItemStack> materialCache;

    private final IDrawable icon;
    private final IDrawable arrow;
    private final Font font;

    public ForgingRecipeCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(SfItems.TOOL_FORGE.get()));
        this.arrow = helper.getRecipeArrow();
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public IRecipeType<ForgingDisplay> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.silentforging.forging");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ForgingDisplay display, IFocusGroup focuses) {
        builder.addInputSlot(0, SLOT_Y).setStandardSlotBackground().add(display.blueprint());
        builder.addInputSlot(20, SLOT_Y).setStandardSlotBackground().addItemStacks(materials());
        List<Ingredient> extras = display.extras();
        for (int i = 0; i < extras.size(); i++) {
            builder.addInputSlot(40 + i * 20, SLOT_Y).setStandardSlotBackground().add(extras.get(i));
        }
        ItemStack material = focuses.getItemStackFocuses(RecipeIngredientRole.INPUT)
                .map(focus -> focus.getTypedValue().getIngredient())
                .filter(ForgeMaterialTags::isIngotOrGem)
                .findFirst()
                .orElse(new ItemStack(Items.IRON_INGOT));
        ItemStack output = ForgingDisplays.assemble(display, material);
        if (!output.isEmpty()) {
            builder.addOutputSlot(outputX(display), SLOT_Y).setOutputSlotBackground().add(output);
        }
    }

    @Override
    public void draw(ForgingDisplay display, IRecipeSlotsView slotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, arrowX(display), SLOT_Y);
        int y = 26;
        graphics.text(font, Component.translatable("jei.silentforging.target", display.target(), display.range()), 0, y, TEXT_TARGET, false);
        y += 12;
        for (ForgeRule rule : display.rules()) {
            graphics.text(font, Component.translatable("jei.silentforging.rule." + rule.getSerializedName()), 0, y, TEXT_RULE, false);
            y += 10;
        }
    }

    private int arrowX(ForgingDisplay display) {
        return 40 + display.extras().size() * 20;
    }

    private int outputX(ForgingDisplay display) {
        return arrowX(display) + arrow.getWidth() + 4;
    }

    private static List<ItemStack> materials() {
        if (materialCache == null) {
            List<ItemStack> stacks = new ArrayList<>();
            addTag(stacks, ForgeMaterialTags.INGOTS);
            addTag(stacks, ForgeMaterialTags.GEMS);
            materialCache = stacks;
        }
        return materialCache;
    }

    private static void addTag(List<ItemStack> stacks, TagKey<Item> tag) {
        BuiltInRegistries.ITEM.get(tag).ifPresent(holders ->
                holders.forEach(holder -> stacks.add(new ItemStack(holder.value()))));
    }
}
