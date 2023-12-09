package doctor4t.astronomical.common.block;

import doctor4t.astronomical.common.block.entity.MarshmallowCanBlockEntity;
import doctor4t.astronomical.common.init.ModBlocks;
import doctor4t.astronomical.common.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MarshmallowCanBlock extends BlockWithEntity {
	public static final IntProperty LEVEL = IntProperty.of("level", 0, 8);

	public MarshmallowCanBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 8));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MarshmallowCanBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof MarshmallowCanBlockEntity marshmallowCanBlockEntity) {
			if (player.getStackInHand(hand).isOf(Items.STICK)) {
				if (marshmallowCanBlockEntity.decrementMarshmallowCount()) {
					if (player.getStackInHand(hand).getCount() == 1) {
						player.setStackInHand(hand, new ItemStack(ModItems.MARSHMALLOW_STICK));
					} else {
						player.getStackInHand(hand).decrement(1);
						player.giveItemStack(new ItemStack(ModItems.MARSHMALLOW_STICK));
					}
					return ActionResult.SUCCESS;
				}
			} else if (player.getStackInHand(hand).isOf(ModItems.MARSHMALLOW)) {
				int i = marshmallowCanBlockEntity.incrementMarshmallowCount(player.getStackInHand(hand).getCount());
				if (i > 0) {
					player.getStackInHand(hand).decrement(i);
					return ActionResult.SUCCESS;
				}
			}
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	protected void appendProperties(StateManager.@NotNull Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
		return this.getDefaultState().with(LEVEL, ctx.getStack().getOrCreateNbt().getInt("marshmallowCount") / 8);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof MarshmallowCanBlockEntity marshmallowCanBlockEntity && !player.isCreative()) {
			ItemStack itemStack = new ItemStack(ModBlocks.MARSHMALLOW_CAN.asItem());
			if (!world.isClient && marshmallowCanBlockEntity.getMarshmallowCount() > 0) {
				itemStack.getOrCreateNbt().putInt("marshmallowCount", marshmallowCanBlockEntity.getMarshmallowCount());
			}
			ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, itemStack);
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
		}

		super.onBreak(world, pos, state, player);
	}

}
