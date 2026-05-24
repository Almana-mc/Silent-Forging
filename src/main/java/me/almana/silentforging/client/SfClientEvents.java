package me.almana.silentforging.client;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.setup.SfMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Silentforging.MODID, value = Dist.CLIENT)
public final class SfClientEvents {
    private SfClientEvents() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(SfMenus.TOOL_FORGE.get(), ToolForgeScreen::new);
    }
}
