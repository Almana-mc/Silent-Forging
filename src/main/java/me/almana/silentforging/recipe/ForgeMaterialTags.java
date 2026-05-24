package me.almana.silentforging.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ForgeMaterialTags {
    public static final TagKey<Item> INGOTS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots"));
    public static final TagKey<Item> GEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "gems"));

    private ForgeMaterialTags() {
    }

    public static boolean isIngotOrGem(ItemStack stack) {
        return stack.is(INGOTS) || stack.is(GEMS);
    }
}
