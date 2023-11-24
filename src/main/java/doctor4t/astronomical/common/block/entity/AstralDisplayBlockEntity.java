package doctor4t.astronomical.common.block.entity;

import carpet.script.language.Sys;
import doctor4t.astronomical.common.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

public class AstralDisplayBlockEntity extends BlockEntity {
	BlockPos parentPos;

	public AstralDisplayBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ASTRAL_DISPLAY, pos, state);
	}

	public BlockPos getParentPos() {
		return this.parentPos;
	}

	public void setParentPos(BlockPos parentPos) {
		this.parentPos = parentPos;
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		if (this.getParentPos() != null) {
			nbt.put("parentPos", NbtHelper.fromBlockPos(this.getParentPos()));
		}

		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		if (nbt.contains("parentPos")) {
			this.setParentPos(NbtHelper.toBlockPos(nbt.getCompound("parentPos")));
		}
	}
}
