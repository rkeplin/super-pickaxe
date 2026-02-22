package com.example.superpickaxe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

public class SuperPickaxeItem extends DiggerItem {
    private static final String BLOCKS_MINED_KEY = "BlocksMined";
    private static final int TORCH_DROP_INTERVAL = 18;

    public SuperPickaxeItem(Properties properties) {
        super(5.0F, -2.0F, Tiers.DIAMOND, Tags.Blocks.ORES, properties);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer serverPlayer) {
            // Get the list of blocks to mine in 3x3 area
            List<BlockPos> blocksToMine = getBlocksToMine(pos, serverPlayer);

            // Track blocks destroyed
            int blocksDestroyed = 1; // Count the main block

            // Mine each block
            for (BlockPos blockPos : blocksToMine) {
                if (!blockPos.equals(pos)) { // Skip the original block as it's already being mined
                    BlockState blockState = level.getBlockState(blockPos);

                    // Check if the block can be mined with a diamond pickaxe
                    if (isCorrectToolForDrops(blockState) && blockState.getDestroySpeed(level, blockPos) >= 0) {
                        // Break the block
                        level.destroyBlock(blockPos, true, serverPlayer);
                        blocksDestroyed++;
                    }
                }
            }

            // Update blocks mined counter and give torches
            updateBlocksMined(stack, serverPlayer, blocksDestroyed);
        }

        return super.mineBlock(stack, level, state, pos, entity);
    }

    private void updateBlocksMined(ItemStack stack, ServerPlayer player, int blocksDestroyed) {
        // Get or create NBT data
        CompoundTag tag = stack.getOrCreateTag();
        int totalBlocksMined = tag.getInt(BLOCKS_MINED_KEY) + blocksDestroyed;

        // Calculate how many torches to give
        int torchesToGive = totalBlocksMined / TORCH_DROP_INTERVAL;

        if (torchesToGive > 0) {
            // Give torches to player
            ItemStack torches = new ItemStack(Items.TORCH, torchesToGive);
            player.addItem(torches);

            // Update counter (keep remainder)
            totalBlocksMined = totalBlocksMined % TORCH_DROP_INTERVAL;
        }

        // Save updated count
        tag.putInt(BLOCKS_MINED_KEY, totalBlocksMined);
    }

    private List<BlockPos> getBlocksToMine(BlockPos centerPos, ServerPlayer player) {
        List<BlockPos> positions = new ArrayList<>();

        // Always mine in a 3x3 cube around the target block
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    positions.add(centerPos.offset(x, y, z));
                }
            }
        }

        return positions;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        // Can mine the same blocks as a diamond pickaxe
        return state.is(Tags.Blocks.ORES) ||
               state.is(net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // Don't increase speed for bedrock (it's indestructible anyway)
        if (state.is(net.minecraft.world.level.block.Blocks.BEDROCK)) {
            return super.getDestroySpeed(stack, state);
        }
        // Significantly increase mining speed for all other blocks
        return super.getDestroySpeed(stack, state) * 3.0F;
    }
}
