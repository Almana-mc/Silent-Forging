package me.almana.silentforging.client;

import me.almana.silentforging.block.ToolForgeBlockEntity;
import me.almana.silentforging.forge.ForgeAction;
import me.almana.silentforging.forge.ForgeQuality;
import me.almana.silentforging.menu.ToolForgeMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ToolForgeScreen extends AbstractContainerScreen<ToolForgeMenu> {
    private static final int WIDTH = 248;
    private static final int HEIGHT = 238;
    private static final int MACHINE_BOTTOM = 146;
    private static final int INV_TOP = 150;
    private static final ForgeAction[] DISPLAYED_ACTIONS = {
            ForgeAction.BEND,
            ForgeAction.HIT,
            ForgeAction.DRAW,
            ForgeAction.FOLD,
            ForgeAction.EXTRUDE,
            ForgeAction.TEMPER
    };

    private static final int FRAME_LIGHT = 0xFF7A6A55;
    private static final int FRAME_MID = 0xFF54483A;
    private static final int FRAME_DARK = 0xFF2C2419;
    private static final int FRAME_VDARK = 0xFF181107;
    private static final int PLATE = 0xFF3D3528;
    private static final int PLATE_DEEP = 0xFF272014;
    private static final int ACCENT = 0xFFE08A3A;
    private static final int ACCENT_HOT = 0xFFFFD35A;
    private static final int BLUEPRINT_BLUE = 0xFF3A86FF;
    private static final int INK = 0xFFF4E6C8;
    private static final int INK_DIM = 0xFFA89878;
    private static final int SLOT_INNER = 0xFF0C0805;
    private static final int BLUEPRINT_GREEN = 0xFF5CD66A;

    private static final Identifier GHOST_BLUEPRINT = Identifier.fromNamespaceAndPath("silentgear", "textures/item/blueprint_package.png");
    private static final List<Identifier> GHOST_OUTPUT = List.of(
            Identifier.fromNamespaceAndPath("silentgear", "textures/item/sword/main_generic_hc.png"),
            Identifier.fromNamespaceAndPath("silentgear", "textures/item/axe/main_generic_hc.png"),
            Identifier.fromNamespaceAndPath("silentgear", "textures/item/shovel/main_generic_hc.png"),
            Identifier.fromNamespaceAndPath("silentgear", "textures/item/hoe/main_generic_hc.png")
    );
    private static final TagKey<Item> INGOTS_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots"));
    private static final TagKey<Item> GEMS_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "gems"));
    private static final float GHOST_ALPHA = 0.35F;
    private static final int GHOST_CYCLE_TICKS = 40;

    private static final int BAR_X = 12;
    private static final int BAR_Y = 78;
    private static final int BAR_W = 224;
    private static final int BAR_H = 12;

    private final Button[] actionButtons = new Button[ForgeAction.values().length];
    private int ticks;
    private List<Identifier> materialGhosts;

    public ToolForgeScreen(ToolForgeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        int btnW = 36;
        int gap = 2;
        int y = topPos + 116;
        for (int i = 0; i < DISPLAYED_ACTIONS.length; i++) {
            ForgeAction action = DISPLAYED_ACTIONS[i];
            int x = leftPos + 8 + i * (btnW + gap) + (i > 2 ? 14 : 0);
            int id = action.ordinal();
            Button button = Button.builder(
                            Component.translatable("button.silentforging." + action.key()),
                            b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id))
                    .bounds(x, y, btnW, 22)
                    .build();
            actionButtons[id] = button;
            addRenderableWidget(button);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (menu.canForge()) {
            int index = event.key() - GLFW.GLFW_KEY_1;
            if (index >= 0 && index < DISPLAYED_ACTIONS.length) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, DISPLAYED_ACTIONS[index].ordinal());
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    protected void containerTick() {
        ticks++;
        boolean canForge = menu.canForge();
        for (Button button : actionButtons) {
            button.active = canForge;
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = leftPos;
        int y = topPos;

        panel(graphics, x, y, WIDTH, MACHINE_BOTTOM, PLATE, true);
        drawTitleBar(graphics, x, y);
        drawSlots(graphics, x, y);
        drawProgress(graphics, x, y);
        drawActionsLabel(graphics, x, y);
        drawActionDivider(graphics, x, y);

        panel(graphics, x, y + INV_TOP, WIDTH, HEIGHT - INV_TOP, PLATE, true);
        drawPlayerSlots(graphics, x, y);
    }

    private void drawTitleBar(GuiGraphicsExtractor graphics, int ox, int oy) {
        int bx = ox + 6;
        int by = oy + 6;
        panel(graphics, bx, by, WIDTH - 12, 30, FRAME_DARK, true);
        graphics.text(font, Component.translatable("block.silentforging.tool_forge"), bx + 6, by + 5, ACCENT_HOT, true);
        graphics.text(font, "STRIKES " + pad2(menu.strikes()), bx + 6, by + 18, INK_DIM, false);
        drawQuality(graphics, ox + WIDTH - 110, by + 11);
    }

    private void drawQuality(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.text(font, "QUALITY", x, y, INK_DIM, false);
        graphics.text(font, menu.quality().displayName(), x + 54, y, qualityColor(), false);
    }

    private int qualityColor() {
        ForgeQuality quality = menu.quality();
        return switch (quality) {
            case CRUDE -> 0xFFFF6B55;
            case NORMAL -> INK;
            case PRISTINE -> 0xFF5CFF9A;
        };
    }

    private void drawSlots(GuiGraphicsExtractor graphics, int ox, int oy) {
        forgeSlot(graphics, ox + 29, oy + 46, BLUEPRINT_BLUE);
        forgeSlot(graphics, ox + 85, oy + 46, INK_DIM);
        forgeSlot(graphics, ox + 141, oy + 46, BLUEPRINT_GREEN);
        drawGhostHints(graphics, ox, oy);
        graphics.text(font, ">", ox + 56, oy + 49, menu.getSlot(ToolForgeBlockEntity.SLOT_BLUEPRINT).hasItem() ? ACCENT_HOT : INK_DIM, false);
        graphics.text(font, ">", ox + 112, oy + 49, menu.hasForgeInputs() ? ACCENT_HOT : INK_DIM, false);
    }

    private void drawGhostHints(GuiGraphicsExtractor graphics, int ox, int oy) {
        if (!menu.getSlot(ToolForgeBlockEntity.SLOT_BLUEPRINT).hasItem()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, GHOST_BLUEPRINT, ox + 30, oy + 47, 0.0F, 0.0F, 16, 16, 16, 16, ARGB.white(GHOST_ALPHA));
        }
        if (!menu.getSlot(ToolForgeBlockEntity.SLOT_MATERIAL).hasItem()) {
            cycleGhost(graphics, materialGhosts(), ox + 86, oy + 47);
        }
        if (!menu.getSlot(ToolForgeBlockEntity.SLOT_OUTPUT).hasItem()) {
            cycleGhost(graphics, GHOST_OUTPUT, ox + 142, oy + 47);
        }
    }

    private void cycleGhost(GuiGraphicsExtractor graphics, List<Identifier> textures, int x, int y) {
        if (textures.isEmpty()) {
            return;
        }
        Identifier texture = textures.get((ticks / GHOST_CYCLE_TICKS) % textures.size());
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, 16, 16, 16, 16, ARGB.white(GHOST_ALPHA));
    }

    private List<Identifier> materialGhosts() {
        if (materialGhosts == null) {
            materialGhosts = itemTextures(INGOTS_TAG);
            materialGhosts.addAll(itemTextures(GEMS_TAG));
        }
        return materialGhosts;
    }

    private static List<Identifier> itemTextures(TagKey<Item> tag) {
        List<Identifier> textures = new ArrayList<>();
        BuiltInRegistries.ITEM.get(tag).ifPresent(holders -> holders.forEach(holder -> {
            Item item = holder.value();
            if (item instanceof BlockItem) {
                return;
            }
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            textures.add(Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/item/" + id.getPath() + ".png"));
        }));
        return textures;
    }

    private void forgeSlot(GuiGraphicsExtractor graphics, int x, int y, int accent) {
        slotBg(graphics, x, y);
        graphics.fill(x - 1, y - 1, x + 4, y, accent);
        graphics.fill(x - 1, y - 1, x, y + 4, accent);
        graphics.fill(x + 14, y + 18, x + 19, y + 19, accent);
        graphics.fill(x + 18, y + 14, x + 19, y + 19, accent);
    }

    private void slotBg(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, SLOT_INNER);
        graphics.fill(x, y, x + 18, y + 1, FRAME_VDARK);
        graphics.fill(x, y, x + 1, y + 18, FRAME_VDARK);
        graphics.fill(x, y + 17, x + 18, y + 18, FRAME_LIGHT);
        graphics.fill(x + 17, y, x + 18, y + 18, FRAME_LIGHT);
    }

    private void drawPlayerSlots(GuiGraphicsExtractor graphics, int ox, int oy) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                slotBg(graphics, ox + 7 + col * 18, oy + 155 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            slotBg(graphics, ox + 7 + col * 18, oy + 215);
        }
    }

    private void drawProgress(GuiGraphicsExtractor graphics, int ox, int oy) {
        int x = ox + BAR_X;
        int y = oy + BAR_Y;
        panel(graphics, x - 2, y - 2, BAR_W + 4, BAR_H + 4, PLATE_DEEP, false);
        graphics.fill(x, y, x + BAR_W, y + BAR_H, FRAME_VDARK);

        int targetMin = menu.targetMin();
        int targetMax = menu.targetMax();
        int tzL = x + BAR_W * targetMin / 100;
        int tzR = x + BAR_W * targetMax / 100;
        graphics.fill(tzL, y, tzR, y + BAR_H, 0x553A86FF);
        graphics.fill(tzL, y, tzL + 1, y + BAR_H, BLUEPRINT_BLUE);
        graphics.fill(tzR - 1, y, tzR, y + BAR_H, BLUEPRINT_BLUE);
        int tzC = (tzL + tzR) / 2;
        graphics.fill(tzC, y - 3, tzC + 1, y, BLUEPRINT_BLUE);

        int prog = Math.max(0, Math.min(100, menu.progress()));
        int fillW = BAR_W * prog / 100;
        if (fillW > 0) {
            graphics.fill(x, y, x + fillW, y + BAR_H, ACCENT);
            graphics.fill(x, y, x + fillW, y + 2, ACCENT_HOT);
            graphics.fill(x + fillW - 1, y, x + fillW, y + BAR_H, ACCENT_HOT);
        }
        int markerX = x + BAR_W * prog / 100;
        graphics.fill(markerX - 1, y - 2, markerX + 2, y + BAR_H + 2, INK);

        graphics.text(font, Component.translatable("gui.silentforging.progress"), x, y + BAR_H + 3, INK_DIM, false);
        String pct = menu.progress() + "% / " + (targetMin + targetMax) / 2 + "%";
        graphics.text(font, pct, x + BAR_W - font.width(pct), y + BAR_H + 3, INK, false);
    }

    private void drawActionsLabel(GuiGraphicsExtractor graphics, int ox, int oy) {
        graphics.text(font, Component.translatable("gui.silentforging.actions"), ox + 12, oy + 106, INK_DIM, false);
    }

    private void drawActionDivider(GuiGraphicsExtractor graphics, int ox, int oy) {
        graphics.text(font, "|", ox + 126, oy + 122, INK_DIM, false);
    }

    private void panel(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int base, boolean out) {
        int light = out ? FRAME_LIGHT : FRAME_VDARK;
        int dark = out ? FRAME_VDARK : FRAME_LIGHT;
        graphics.fill(x, y, x + w, y + h, base);
        graphics.fill(x, y, x + w, y + 2, light);
        graphics.fill(x, y, x + 2, y + h, light);
        graphics.fill(x, y + h - 2, x + w, y + h, dark);
        graphics.fill(x + w - 2, y, x + w, y + h, dark);
        graphics.fill(x + 2, y + 2, x + w - 2, y + 4, FRAME_MID);
        graphics.fill(x + 2, y + 2, x + 4, y + h - 2, FRAME_MID);
    }

    private static String pad2(int value) {
        return value < 10 ? "0" + value : Integer.toString(value);
    }
}
