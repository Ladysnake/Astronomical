package doctor4t.astronomical.common.screen;

import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.block.entity.AstralDisplayBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.NotNull;

public class RingColorScreenHandler extends ScreenHandler {
	public AstralDisplayBlockEntity entity;

	public RingColorScreenHandler(int syncId, @NotNull PlayerInventory playerInventory) {
		super(Astronomical.RING_COLOR_SCREEN_HANDLER, syncId);
	}

	public AstralDisplayBlockEntity entity() {
		return this.entity;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack quickTransfer(PlayerEntity player, int index) {
		return player.getInventory().getStack(index);
	}
}
