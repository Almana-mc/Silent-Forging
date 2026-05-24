package me.almana.silentforging.setup;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.menu.ToolForgeMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SfMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Silentforging.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ToolForgeMenu>> TOOL_FORGE =
            MENUS.register("tool_forge", () -> IMenuTypeExtension.create(ToolForgeMenu::new));

    private SfMenus() {
    }
}
