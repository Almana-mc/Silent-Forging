package me.almana.silentforging.client;

import me.almana.silentforging.block.ToolForgeBlockEntity;
import me.almana.silentforging.forge.ForgeAction;
import me.almana.silentforging.forge.ForgeQuality;
import me.almana.silentforging.menu.ToolForgeMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

public class ToolForgeScreen extends AbstractContainerScreen<ToolForgeMenu> {
    private static final int WIDTH = 248;
    private static final int HEIGHT = 238;
    private static final int MACHINE_BOTTOM = 146;
    private static final int INV_TOP = 150;

    private static final int FRAME_LIGHT = 0xFF7A6A55;
    private static final int FRAME_MID = 0xFF54483A;
    private static final int FRAME_DARK = 0xFF2C2419;
    private static final int FRAME_VDARK = 0xFF181107;
    private static final int PLATE = 0xFF3D3528;
    private static final int PLATE_DEEP = 0xFF272014;
    private static final int ACCENT = 0xFFE08A3A;
    private static final int ACCENT_HOT = 0xFFFFD35A;
    private static final int TARGET_BLUE = 0xFF3A86FF;
    private static final int INK = 0xFFF4E6C8;
    private static final int INK_DIM = 0xFFA89878;
    private static final int SLOT_INNER = 0xFF0C0805;
    private static final int BLUEPRINT_GREEN = 0xFF5CD66A;

    private static final int BAR_X = 12;
    private static final int BAR_Y = 78;
    private static final int BAR_W = 224;
    private static final int BAR_H = 12;

    private final Button[] actionButtons = new Button[ForgeAction.values().length];

    public ToolForgeScreen(ToolForgeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, WIDTH, HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        ForgeAction[] actions = ForgeAction.values();
        int btnW = 36;
        int gap = 2;
        int y = topPos + 116;
        for (int i = 0; i < actions.length; i++) {
            ForgeAction action = actions[i];
            int x = leftPos + 12 + i * (btnW + gap);
            int id = i;
            Button button = Button.builder(
                            Component.translatable("button.silentforging." + action.key()),
                            b -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id))
                    .bounds(x, y, btnW, 22)
                    .build();
            actionButtons[i] = button;
            addRenderableWidget(button);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (menu.canForge()) {
            int index = event.key() - GLFW.GLFW_KEY_1;
            if (index >= 0 && index < actionButtons.length) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    protected void containerTick() {
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
        forgeSlot(graphics, ox + 29, oy + 46, TARGET_BLUE);
        forgeSlot(graphics, ox + 85, oy + 46, INK_DIM);
        forgeSlot(graphics, ox + 141, oy + 46, BLUEPRINT_GREEN);
        drawGhostHints(graphics, ox, oy);
        graphics.text(font, "<", ox + 56, oy + 49, menu.getSlot(ToolForgeBlockEntity.SLOT_OUTPUT).hasItem() ? ACCENT_HOT : INK_DIM, false);
        graphics.text(font, "<", ox + 112, oy + 49, menu.phase() == ToolForgeBlockEntity.PHASE_FORGING ? ACCENT_HOT : INK_DIM, false);
    }

    private void drawGhostHints(GuiGraphicsExtractor graphics, int ox, int oy) {
        if (!menu.getSlot(ToolForgeBlockEntity.SLOT_OUTPUT).hasItem()) {
            ghostOutput(graphics, ox + 35, oy + 51);
        }
        if (!menu.getSlot(ToolForgeBlockEntity.SLOT_MATERIAL).hasItem()) {
            ghostMaterial(graphics, ox + 90, oy + 52);
        }
        if (!menu.getSlot(ToolForgeBlockEntity.SLOT_BLUEPRINT).hasItem()) {
            ghostBlueprint(graphics, ox + 146, oy + 51);
        }
    }

    private void ghostOutput(GuiGraphicsExtractor graphics, int x, int y) {
        int color = 0x553A86FF;
        graphics.fill(x, y, x + 8, y + 8, color);
        graphics.fill(x + 2, y + 2, x + 10, y + 10, color);
    }

    private void ghostMaterial(GuiGraphicsExtractor graphics, int x, int y) {
        int color = 0x66A89878;
        graphics.fill(x + 2, y, x + 12, y + 3, color);
        graphics.fill(x, y + 3, x + 14, y + 8, color);
        graphics.fill(x + 1, y + 8, x + 13, y + 11, color);
    }

    private void ghostBlueprint(GuiGraphicsExtractor graphics, int x, int y) {
        int color = 0x665CD66A;
        graphics.fill(x + 2, y, x + 10, y + 12, color);
        graphics.fill(x + 10, y + 3, x + 13, y + 12, color);
        graphics.fill(x + 9, y, x + 13, y + 4, 0x335CD66A);
        graphics.fill(x + 4, y + 4, x + 9, y + 5, SLOT_INNER);
        graphics.fill(x + 4, y + 7, x + 11, y + 8, SLOT_INNER);
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
        graphics.fill(tzL, y, tzL + 1, y + BAR_H, TARGET_BLUE);
        graphics.fill(tzR - 1, y, tzR, y + BAR_H, TARGET_BLUE);
        int tzC = (tzL + tzR) / 2;
        graphics.fill(tzC, y - 3, tzC + 1, y, TARGET_BLUE);

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
