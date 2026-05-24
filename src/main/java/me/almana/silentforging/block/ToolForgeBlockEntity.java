package me.almana.silentforging.block;

import me.almana.silentforging.forge.Difficulty;
import me.almana.silentforging.forge.ForgeAction;
import me.almana.silentforging.forge.ForgeQuality;
import me.almana.silentforging.forge.ForgeStrikeBenchmark;
import me.almana.silentforging.forge.ForgeWork;
import me.almana.silentforging.menu.ToolForgeMenu;
import me.almana.silentforging.recipe.ForgeMaterialTags;
import me.almana.silentforging.recipe.ForgingRecipe;
import me.almana.silentforging.recipe.SilentGearPartRecipes;
import me.almana.silentforging.setup.SfBlockEntities;
import me.almana.silentforging.setup.SfRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ToolForgeBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_BLUEPRINT = 0;
    public static final int SLOT_MATERIAL = 1;
    public static final int SLOT_OUTPUT = 2;

    public static final int DATA_PROGRESS = 0;
    public static final int DATA_QUALITY = 1;
    public static final int DATA_PHASE = 2;
    public static final int DATA_STRIKES = 3;
    public static final int DATA_TARGET_MIN = 4;
    public static final int DATA_TARGET_MAX = 5;
    public static final int DATA_COUNT = 6;

    public static final int PHASE_FORGING = 0;

    private final SimpleContainer inventory = new SimpleContainer(3);
    private final Difficulty difficulty = Difficulty.NORMAL;

    private ForgeQuality quality = ForgeQuality.NORMAL;
    private int phase = PHASE_FORGING;
    private int syncedProgress;
    private int syncedStrikes;
    private int syncedTargetMin = difficulty.targetMin();
    private int syncedTargetMax = difficulty.targetMax();
    private ForgeWork work;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int id) {
            return switch (id) {
                case DATA_PROGRESS -> isClientData() ? syncedProgress : progress();
                case DATA_QUALITY -> quality.ordinal();
                case DATA_PHASE -> phase;
                case DATA_STRIKES -> isClientData() ? syncedStrikes : strikes();
                case DATA_TARGET_MIN -> isClientData() ? syncedTargetMin : targetMin();
                case DATA_TARGET_MAX -> isClientData() ? syncedTargetMax : targetMax();
                default -> 0;
            };
        }

        @Override
        public void set(int id, int value) {
            switch (id) {
                case DATA_PROGRESS -> syncedProgress = value;
                case DATA_QUALITY -> quality = ForgeQuality.byId(value);
                case DATA_PHASE -> phase = value;
                case DATA_STRIKES -> syncedStrikes = value;
                case DATA_TARGET_MIN -> syncedTargetMin = value;
                case DATA_TARGET_MAX -> syncedTargetMax = value;
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public ToolForgeBlockEntity(BlockPos pos, BlockState state) {
        super(SfBlockEntities.TOOL_FORGE.get(), pos, state);
    }

    public SimpleContainer inventory() {
        return inventory;
    }

    public ContainerData data() {
        return data;
    }

    public Optional<ActiveProfile> currentProfile() {
        if (!(level instanceof ServerLevel serverLevel) || !inventory.getItem(SLOT_OUTPUT).isEmpty()) {
            return Optional.empty();
        }
        ItemStack blueprint = inventory.getItem(SLOT_BLUEPRINT);
        ItemStack material = inventory.getItem(SLOT_MATERIAL);
        if (!ForgeMaterialTags.isIngotOrGem(material)) {
            return Optional.empty();
        }
        Optional<ActiveProfile> datapackProfile = serverLevel.recipeAccess()
                .recipeMap()
                .byType(SfRecipes.FORGING_TYPE.get())
                .stream()
                .filter(profile -> SilentGearPartRecipes.match(profile.value(), blueprint, material, serverLevel).isPresent())
                .map(profile -> new ActiveProfile(profile.id().identifier().toString(), profile.value()))
                .findFirst();
        return datapackProfile.or(() -> SilentGearPartRecipes.matchDefault(blueprint, material, serverLevel)
                .map(match -> new ActiveProfile(match.id(), match.recipe())));
    }

    public void applyAction(int actionId) {
        Optional<ActiveProfile> profile = currentProfile();
        if (phase != PHASE_FORGING || profile.isEmpty()) {
            return;
        }
        ForgeAction action = ForgeAction.byId(actionId);
        if (action == null) {
            return;
        }
        ForgingRecipe recipe = profile.get().recipe();
        String profileId = profile.get().id();
        if (work == null || !work.recipeId().equals(profileId)) {
            work = ForgeWork.started(profileId, recipe.target());
        }
        ForgeStrikeBenchmark benchmark = ForgeStrikeBenchmark.from(recipe);
        work = work.withAction(action);
        quality = benchmark.qualityFor(work.strikes());

        playSound(SoundEvents.ANVIL_LAND, 0.4f, 1.4f);
        if (benchmark.hasFailed(work.strikes())) {
            fail(recipe);
            playSound(SoundEvents.ITEM_BREAK, 0.8f, 1.0f);
        } else if (work.isComplete(recipe.range())) {
            finish(recipe, quality);
            playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 0.8f, 1.0f);
        }
        setChanged();
    }

    private void playSound(SoundEvent sound, float volume, float pitch) {
        if (level != null) {
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, volume, pitch);
        }
    }

    private void playSound(Holder<SoundEvent> sound, float volume, float pitch) {
        playSound(sound.value(), volume, pitch);
    }

    private void clearWork() {
        work = null;
        quality = ForgeQuality.NORMAL;
        phase = PHASE_FORGING;
    }

    public void resetProgress() {
        clearWork();
        setChanged();
    }

    private void finish(ForgingRecipe recipe, ForgeQuality quality) {
        SilentGearPartRecipes.match(recipe, inventory.getItem(SLOT_BLUEPRINT), inventory.getItem(SLOT_MATERIAL), level, quality)
                .ifPresent(match -> {
                    inventory.setItem(SLOT_OUTPUT, match.result());
                    inventory.getItem(SLOT_MATERIAL).shrink(match.materialCost());
                    clearWork();
                });
    }

    private void fail(ForgingRecipe recipe) {
        SilentGearPartRecipes.match(recipe, inventory.getItem(SLOT_BLUEPRINT), inventory.getItem(SLOT_MATERIAL), level)
                .ifPresent(match -> inventory.getItem(SLOT_MATERIAL).shrink(match.materialCost()));
        clearWork();
    }

    private int progress() {
        return work == null ? 0 : work.value();
    }

    private int strikes() {
        return work == null ? 0 : work.strikes();
    }

    private int targetMin() {
        return currentProfile()
                .map(profile -> profile.recipe().target() - profile.recipe().range())
                .orElseGet(() -> work == null ? difficulty.targetMin() : work.target() - 3);
    }

    private int targetMax() {
        return currentProfile()
                .map(profile -> profile.recipe().target() + profile.recipe().range())
                .orElseGet(() -> work == null ? difficulty.targetMax() : work.target() + 3);
    }

    private boolean isClientData() {
        return level != null && level.isClientSide();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.silentforging.tool_forge");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new ToolForgeMenu(id, playerInventory, this);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, inventory.getItems());
        output.putString("quality", quality.getSerializedName());
        output.putInt("phase", phase);
        if (work != null) {
            output.putString("recipe", work.recipeId());
            output.putInt("target", work.target());
            output.putInt("work", work.value());
            output.putInt("strikes", work.strikes());
            output.putIntArray("recent", work.recent().stream().mapToInt(ForgeAction::ordinal).toArray());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, inventory.getItems());
        quality = ForgeQuality.byName(input.getStringOr("quality", ForgeQuality.NORMAL.getSerializedName()));
        phase = input.getIntOr("phase", PHASE_FORGING);
        work = null;
        String recipeId = input.getStringOr("recipe", "");
        if (!recipeId.isEmpty()) {
            int[] recent = input.getIntArray("recent").orElseGet(() -> new int[0]);
            work = new ForgeWork(
                    recipeId,
                    input.getIntOr("target", difficulty.targetMax()),
                    input.getIntOr("work", 0),
                    recentActions(recent),
                    input.getIntOr("strikes", 0)
            );
        }
        if (phase != PHASE_FORGING) {
            clearWork();
        }
    }

    private static List<ForgeAction> recentActions(int[] ids) {
        List<ForgeAction> actions = new ArrayList<>();
        ForgeAction[] values = ForgeAction.values();
        for (int id : ids) {
            if (id >= 0 && id < values.length) {
                actions.add(values[id]);
            }
        }
        return actions;
    }

    public record ActiveProfile(String id, ForgingRecipe recipe) {
    }
}
