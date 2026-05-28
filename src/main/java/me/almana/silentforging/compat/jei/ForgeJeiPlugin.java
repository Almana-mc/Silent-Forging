package me.almana.silentforging.compat.jei;

import com.mojang.logging.LogUtils;
import me.almana.silentforging.Silentforging;
import me.almana.silentforging.setup.SfItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.util.List;

@JeiPlugin
public class ForgeJeiPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier ID = Identifier.fromNamespaceAndPath(Silentforging.MODID, "jei");

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ForgingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<ForgingDisplay> displays = ForgingDisplays.build();
        registration.addRecipes(ForgingRecipeCategory.TYPE, displays);
        LOGGER.info("Registered {} tool forge recipes with JEI", displays.size());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(ForgingRecipeCategory.TYPE, SfItems.TOOL_FORGE.get());
    }
}
