package doctor4t.astronomical.common.block.entity;

import doctor4t.astronomical.common.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;

public class AstralDisplayBlockEntity extends LootableContainerBlockEntity {
	public static final int SLOTS = 9;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SLOTS, ItemStack.EMPTY);

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
		return SLOTS;
	}

	public int chooseNonEmptySlot(RandomGenerator random) {
		this.checkLootInteraction(null);
		int i = -1;
		int j = 1;

		for (int k = 0; k < this.inventory.size(); ++k) {
			if (!this.inventory.get(k).isEmpty() && random.nextInt(j++) == 0) {
				i = k;
			}
		}

		return i;
	}

	public int addToFirstFreeSlot(ItemStack stack) {
		for (int i = 0; i < this.inventory.size(); ++i) {
			if (this.inventory.get(i).isEmpty()) {
				this.setStack(i, stack);
				return i;
			}
		}

		return -1;
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

		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(nbt)) {
			Inventories.readNbt(nbt, this.inventory);
		}
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);

		if (this.getParentPos() != null) {
			nbt.put("parentPos", NbtHelper.fromBlockPos(this.getParentPos()));
		}

		if (!this.serializeLootTable(nbt)) {
			Inventories.writeNbt(nbt, this.inventory);
		}
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> list) {
		this.inventory = list;
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
	}
}
