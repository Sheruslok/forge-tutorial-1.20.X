package net.leo.tutorialmod.item.custom;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MetalDetectorItem extends Item {
    public MetalDetectorItem(Properties pProperties) {
        super(pProperties);
    }

    @Override // Override already present useON function
    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide()) { // ensure this is only occurring for serverside, not clientside
            BlockPos positionClicked = pContext.getClickedPos(); // Get block position object
            Player player = pContext.getPlayer(); // Get player object of player who began interaction
            boolean foundBlock = false;

            for (int i = positionClicked.getY(); i >= -64; i--){ // Loop from block posn to bottom of world
                // BlockState is all properties of block in question
                BlockState state = pContext.getLevel().getBlockState(positionClicked.below(i));

                if (isValuableBlock(state)){
                    outputValuableCoordinates(positionClicked.below(i), player, state.getBlock());
                    foundBlock = true;

                    break;
                }
            }

            if (!foundBlock){
                player.sendSystemMessage(Component.literal("No valuable blocks found"));
            }
        }

        pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(),
                player -> player.broadcastBreakEvent(player.getUsedItemHand()));

        return InteractionResult.SUCCESS;
    }

    private void outputValuableCoordinates(BlockPos blockpos, Player player, Block block) {
        player.sendSystemMessage(Component.literal("Found " + I18n.get(block.getDescriptionId()) + " at " +
                "(" + blockpos.getX() + "," + blockpos.getY() + ")"));
    }

    // Checks if block is a block listed below
    private boolean isValuableBlock(BlockState state) {
        return state.is(Blocks.IRON_ORE) || state.is(Blocks.GOLD_ORE) || state.is(Blocks.DIAMOND_ORE);
    }
}
