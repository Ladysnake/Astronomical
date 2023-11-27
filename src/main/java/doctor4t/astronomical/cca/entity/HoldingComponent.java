package doctor4t.astronomical.cca.entity;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class HoldingComponent implements AutoSyncedComponent {
	private final PlayerEntity player;
	private boolean holding = false;

	public HoldingComponent(PlayerEntity player) {
		this.player = player;
	}

	public void sync() {
		AstraCardinalComponents.HOLDING.sync(this.player);
	}

	public boolean isHolding() {
		return this.holding;
	}

	public void setHolding(boolean holding) {
		this.holding = holding;
		this.sync();
	}

	@Override
	public void readFromNbt(@NotNull NbtCompound tag) {
		this.holding = tag.getBoolean("holding");
	}

	@Override
	public void writeToNbt(@NotNull NbtCompound tag) {
		tag.putBoolean("holding", this.holding);
	}
}
