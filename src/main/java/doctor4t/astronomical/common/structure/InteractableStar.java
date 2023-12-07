package doctor4t.astronomical.common.structure;

import com.sammy.lodestone.systems.rendering.particle.Easing;
import doctor4t.astronomical.common.Astronomical;
import net.minecraft.item.SpyglassItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class InteractableStar extends Star {
	public boolean subjectForTermination = false;
	public int countDownThreeTwoOne = 87;
	public Consumer<World> crossFire;
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
		nbt.putInt("termina", countDownThreeTwoOne);
		nbt.putBoolean("pca", subjectForTermination);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		subjectForTermination = nbt.getBoolean("pca");
		countDownThreeTwoOne = nbt.getInt("termina");
	}

	@Override
	public float getSize() {
		float s = super.getSize();
		return subjectForTermination ? s*(1+Easing.BACK_IN.ease((87-countDownThreeTwoOne)/87f, 0, 1, 1)*2) : s;
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
