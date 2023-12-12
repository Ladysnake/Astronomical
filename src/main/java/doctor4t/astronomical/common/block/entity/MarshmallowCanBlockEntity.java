package doctor4t.astronomical.common.block.entity;

import doctor4t.astronomical.common.block.MarshmallowCanBlock;
import doctor4t.astronomical.common.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class MarshmallowCanBlockEntity extends BlockEntity {
	public int marshmallowCount;

	public MarshmallowCanBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MARSHMALLOW_CAN, pos, state);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("marshmallowCount")) {
			this.marshmallowCount = nbt.getInt("marshmallowCount");
		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("marshmallowCount", this.getMarshmallowCount());
	}

	public int getMarshmallowCount() {
		return marshmallowCount;
	}

	public void setMarshmallowCount(int marshmallowCount) {
		this.marshmallowCount = marshmallowCount;
	}

	public int incrementMarshmallowCount(int count) {
		if (this.marshmallowCount + count <= 64) {
			this.marshmallowCount += count;
			this.updateBlockState();
			return count;
		} else {
			int ret = 64 - marshmallowCount;
			this.marshmallowCount = 64;
			this.updateBlockState();
			return ret;
		}
	}

	public boolean incrementMarshmallowCount() {
		if (this.marshmallowCount < 64) {
			this.marshmallowCount += 1;
			this.updateBlockState();
			return true;
		}
		return false;
	}

	public boolean decrementMarshmallowCount() {
		if (this.marshmallowCount > 0) {
			this.marshmallowCount -= 1;
			this.updateBlockState();
			return true;
		}
		return false;
	}

	public void updateBlockState() {
		this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(MarshmallowCanBlock.LEVEL, (int) Math.ceil((double) this.marshmallowCount / 8)));
	}
}
