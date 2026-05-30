package me.almana.silentforging.setup;

import guideme.Guides;
import me.almana.silentforging.Silentforging;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SfCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Silentforging.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SILENTFORGING = TABS.register(
            "silentforging",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.silentforging"))
                    .icon(() -> SfItems.TOOL_FORGE.toStack())
                    .displayItems((params, output) -> {
                        output.accept(SfItems.TOOL_FORGE.get());
                        output.accept(Guides.createGuideItem(Identifier.parse("silentforging:guide")));
                    })
                    .build()
    );

    private SfCreativeTabs() {
    }
}
