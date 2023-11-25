package doctor4t.astronomical.common.block.entity;

import doctor4t.astronomical.common.init.ModBlockEntities;
import doctor4t.astronomical.common.screen.AstralDisplayScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class AstralDisplayBlockEntity extends LockableContainerBlockEntity implements InventoryChangedListener {
	public static final int SIZE = 9;
	private final SimpleInventory inventory = new SimpleInventory(SIZE);
	private BlockPos parentPos;
	// yLevel, rotSpeed and spin are all values between 0 and 1, scale accordingly
	public double yLevel = 0.5;
	public double rotSpeed = 0.5;
	public double spin = 0.5;

	public AstralDisplayBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ASTRAL_DISPLAY, pos, state);
		this.inventory.addListener(this);
	}

	public BlockPos getParentPos() {
		return this.parentPos;
	}

	public void setParentPos(BlockPos parentPos) {
		this.parentPos = parentPos;
	}

	@Override
	public int size() {
		return SIZE;
	}

	@Override
	public boolean isEmpty() {
		return this.inventory.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.inventory.getStack(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
        return this.inventory.removeStack(slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.inventory.removeStack(slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
        this.inventory.setStack(slot, stack);
		if (stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		if (this.world != null && this.world.getBlockEntity(this.pos) != this) {
			return false;
		}
		return !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.astral_display");
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("parentPos")) {
            this.setParentPos(NbtHelper.toBlockPos(nbt.getCompound("parentPos")));
		}
		this.inventory.clear();
		this.inventory.readNbtList(nbt.getList("inventory", 10));
		this.yLevel = nbt.getDouble("yLevel");
		this.rotSpeed = nbt.getDouble("rotSpeed");
		this.spin = nbt.getDouble("spin");
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (this.getParentPos() != null) {
			nbt.put("parentPos", NbtHelper.fromBlockPos(this.getParentPos()));
		}
		nbt.put("inventory", this.inventory.toNbtList());
		nbt.putDouble("yLevel", this.yLevel);
		nbt.putDouble("rotSpeed", this.rotSpeed);
		nbt.putDouble("spin", this.spin);
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new AstralDisplayScreenHandler(syncId, playerInventory, this);
	}

	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.of(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		var nbt = super.toInitialChunkDataNbt();
		this.writeNbt(nbt);
		return nbt;
	}

	@Override
	public void onInventoryChanged(Inventory sender) {
		if (this.world != null && !this.world.isClient()) {
			var state = this.world.getBlockState(this.pos);
			this.world.updateListeners(this.pos, state, state, Block.NOTIFY_LISTENERS);
			this.markDirty();
		}
	}
}
