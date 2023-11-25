package doctor4t.astronomical.common.screen;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

public class AstralDisplayScreenHandler extends ScreenHandler {
	public Inventory inventory;
	public AstralDisplayBlockEntity entity;

	public AstralDisplayScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, new SimpleInventory(5));
	}

	public AstralDisplayScreenHandler(int syncId, @NotNull PlayerInventory playerInventory, Inventory inventory) {
		super(Astronomical.ASTRAL_DISPLAY_SCREEN_HANDLER, syncId);
		this.inventory = inventory;
		this.inventory.onOpen(playerInventory.player);
		if (this.inventory instanceof AstralDisplayBlockEntity blockEntity) {
			this.entity = blockEntity;
		}
		for (var i = 0; i < 5; i++) {
			this.addSlot(new Slot(inventory, i, 44 + i * 18, 17));
		}
		for (var i = 0; i < 3; i++) {
			for (var j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (var i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	public AstralDisplayBlockEntity entity() {
		return this.entity;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.inventory.onClose(player);
	}

	@Override
	public ItemStack quickTransfer(PlayerEntity player, int index) {
		var itemStack = ItemStack.EMPTY;
		var slot = this.slots.get(index);
		if (slot.hasStack()) {
			var itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index < 5 ? !this.insertItem(itemStack2, 5, 41, true) : !this.insertItem(itemStack2, 0, 5, false)) {
				return ItemStack.EMPTY;
			}
			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}
		return itemStack;
	}
}
