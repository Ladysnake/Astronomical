package doctor4t.astronomical.common.block.entity;

import doctor4t.astronomical.common.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class AstralDisplayBlockEntity extends BlockEntity {
	public AstralDisplayBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ASTRAL_DISPLAY, pos, state);
	}


}
