package doctor4t.astronomical.common.structure;

import doctor4t.astronomical.common.Astronomical;
import net.minecraft.item.SpyglassItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class InteractableStar extends Star {
	public static final Identifier INTERACTABLE_TEX = Astronomical.id("textures/vfx/interactable.png");

	public InteractableStar(Vec3d vec, float size, float heat) {
		super(vec, size, heat);
	}
	public InteractableStar() {

	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putString("id", getId().toString());
	}

	@Override
	public boolean canInteract() {
		return true;
	}

	private static final Identifier ID = Astronomical.id("supernoveable");

	@Override
	public Identifier getId() {
		return ID;
	}
}
