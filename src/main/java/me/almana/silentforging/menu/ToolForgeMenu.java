package me.almana.silentforging.menu;

import me.almana.silentforging.block.ToolForgeBlockEntity;
import me.almana.silentforging.forge.ForgeAction;
import me.almana.silentforging.forge.ForgeQuality;
import me.almana.silentforging.recipe.ForgeMaterialTags;
import me.almana.silentforging.setup.SfBlocks;
import me.almana.silentforging.setup.SfMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.item.blueprint.IBlueprint;

public class ToolForgeMenu extends AbstractContainerMenu {
    private static final int FORGE_SLOTS = 3;

    private final ToolForgeBlockEntity forge;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public ToolForgeMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(id, playerInventory, resolve(playerInventory, buf.readBlockPos()));
    }

    public ToolForgeMenu(int id, Inventory playerInventory, ToolForgeBlockEntity forge) {
        super(SfMenus.TOOL_FORGE.get(), id);
        this.forge = forge;
        this.data = forge.data();
        this.access = ContainerLevelAccess.create(forge.getLevel(), forge.getBlockPos());

        Container container = forge.inventory();
        addSlot(new Slot(container, ToolForgeBlockEntity.SLOT_BLUEPRINT, 30, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof IBlueprint;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                forge.resetProgress();
            }
        });
        addSlot(new Slot(container, ToolForgeBlockEntity.SLOT_MATERIAL, 86, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ForgeMaterialTags.isIngotOrGem(stack);
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                forge.resetProgress();
            }
        });
        addSlot(new Slot(container, ToolForgeBlockEntity.SLOT_OUTPUT, 142, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return hasItem();
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                forge.resetProgress();
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 156 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 216));
        }

        addDataSlots(data);
    }

    public boolean hasMaterial() {
        return slots.get(ToolForgeBlockEntity.SLOT_MATERIAL).hasItem();
    }

    public boolean hasForgeInputs() {
        return slots.get(ToolForgeBlockEntity.SLOT_MATERIAL).hasItem()
                && slots.get(ToolForgeBlockEntity.SLOT_BLUEPRINT).hasItem();
    }

    public boolean canForge() {
        return phase() == ToolForgeBlockEntity.PHASE_FORGING
                && hasForgeInputs()
                && !slots.get(ToolForgeBlockEntity.SLOT_OUTPUT).hasItem();
    }

    public int progress() {
        return data.get(ToolForgeBlockEntity.DATA_PROGRESS);
    }

    public ForgeQuality quality() {
        return ForgeQuality.byId(data.get(ToolForgeBlockEntity.DATA_QUALITY));
    }

    public int phase() {
        return data.get(ToolForgeBlockEntity.DATA_PHASE);
    }

    public int strikes() {
        return data.get(ToolForgeBlockEntity.DATA_STRIKES);
    }

    public int targetMin() {
        return data.get(ToolForgeBlockEntity.DATA_TARGET_MIN);
    }

    public int targetMax() {
        return data.get(ToolForgeBlockEntity.DATA_TARGET_MAX);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < ForgeAction.values().length) {
            forge.applyAction(id);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < FORGE_SLOTS) {
                if (!moveItemStackTo(stack, FORGE_SLOTS, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.getItem() instanceof IBlueprint) {
                if (!moveItemStackTo(stack, ToolForgeBlockEntity.SLOT_BLUEPRINT, ToolForgeBlockEntity.SLOT_BLUEPRINT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (ForgeMaterialTags.isIngotOrGem(stack)) {
                if (!moveItemStackTo(stack, ToolForgeBlockEntity.SLOT_MATERIAL, ToolForgeBlockEntity.SLOT_MATERIAL + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (index < FORGE_SLOTS) {
                forge.resetProgress();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, SfBlocks.TOOL_FORGE.get());
    }

    private static ToolForgeBlockEntity resolve(Inventory playerInventory, BlockPos pos) {
        return (ToolForgeBlockEntity) playerInventory.player.level().getBlockEntity(pos);
    }
}
