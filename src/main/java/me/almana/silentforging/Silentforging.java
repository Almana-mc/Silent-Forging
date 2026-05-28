package me.almana.silentforging;

import me.almana.silentforging.compat.jei.ForgeRecipeSnapshot;
import me.almana.silentforging.data.SfDataGenerators;
import me.almana.silentforging.recipe.SilentGearPartRecipes;
import me.almana.silentforging.setup.SfBlockEntities;
import me.almana.silentforging.setup.SfBlocks;
import me.almana.silentforging.setup.SfCreativeTabs;
import me.almana.silentforging.setup.SfDataComponents;
import me.almana.silentforging.setup.SfItems;
import me.almana.silentforging.setup.SfMaterialModifiers;
import me.almana.silentforging.setup.SfMenus;
import me.almana.silentforging.setup.SfRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

@Mod(Silentforging.MODID)
public class Silentforging {
    public static final String MODID = "silentforging";

    public Silentforging(IEventBus modEventBus) {
        SfDataComponents.DATA_COMPONENTS.register(modEventBus);
        SfBlocks.BLOCKS.register(modEventBus);
        SfItems.ITEMS.register(modEventBus);
        SfBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        SfMenus.MENUS.register(modEventBus);
        SfRecipes.RECIPE_TYPES.register(modEventBus);
        SfRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        SfMaterialModifiers.MATERIAL_MODIFIERS.register(modEventBus);
        SfCreativeTabs.TABS.register(modEventBus);
        modEventBus.addListener(SfDataGenerators::gatherData);
        NeoForge.EVENT_BUS.addListener(SilentGearPartRecipes::captureAndRemove);
        if (FMLLoader.getCurrent().getDist().isClient()) {
            ForgeRecipeSnapshot.register();
        }
    }
}
