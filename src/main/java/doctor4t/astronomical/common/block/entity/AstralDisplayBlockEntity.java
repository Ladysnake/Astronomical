package doctor4t.astronomical.common.block.entity;

import doctor4t.astronomical.common.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
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
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class AstralDisplayBlockEntity extends LootableContainerBlockEntity implements InventoryChangedListener {
	public static final int SIZE = 9;
	private SimpleInventory inventory = new SimpleInventory(SIZE);
	{
		this.inventory.addListener(this);
	}

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
	public int size() {
		return SIZE;
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

		this.inventory.readNbtList(nbt.getList("inventory", 10));
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);

		if (this.getParentPos() != null) {
			nbt.put("parentPos", NbtHelper.fromBlockPos(this.getParentPos()));
		}

		nbt.put("inventory", inventory.toNbtList());
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory.stacks;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> list) {
		if (list.size() != this.inventory.size()) {
			return;
		}

		for (int i = 0; i < SIZE; i++) {
			this.inventory.setStack(i, list.get(i));
		}
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
	}

	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.of(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		var nbt = super.toInitialChunkDataNbt();

		if (this.getParentPos() != null) {
			nbt.put("parentPos", NbtHelper.fromBlockPos(this.getParentPos()));
		}

		nbt.put("inventory", inventory.toNbtList());

		return nbt;
	}

	@Override
	public void onInventoryChanged(Inventory sender) {
		System.out.println("TEST");
		this.markDirty();
	}
}
