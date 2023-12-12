package doctor4t.astronomical.common.structure;

import com.sammy.lodestone.systems.rendering.particle.Easing;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModStatusEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class InteractableStar extends Star {
	public static final Identifier INTERACTABLE_TEX = Astronomical.id("textures/vfx/interactable.png");
	public static final Identifier SUPERNOVA_TEX = Astronomical.id("textures/vfx/supernova.png");
	public static final Identifier SUPERNOVA_DUST_TEX = Astronomical.id("textures/vfx/supernova_dust.png");
	private static final Identifier ID = Astronomical.id("supernoveable");
	public static int FULL_COLLAPSE_TICKS = 100;
	public static int HOLD_COLLAPSE_TICKS = FULL_COLLAPSE_TICKS + 10;
	public static int EXPLOSION_TICKS = HOLD_COLLAPSE_TICKS + 1000;
	public static int START_FADE_OUT_TICKS = HOLD_COLLAPSE_TICKS + 800;
	public static int STARFALL_TICKS = HOLD_COLLAPSE_TICKS + 200;
	public boolean subjectForTermination = false;
	public int supernovaTicks = 0;
	public Consumer<World> crossFire;

	public InteractableStar(Vec3d vec, float size, float alpha, int color, int randomOffset) {
		super(vec, size, alpha, color, randomOffset);
	}

	public InteractableStar() {

	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);

		nbt.putString("id", getId().toString());
		nbt.putInt("supernovaTicks", supernovaTicks);
		nbt.putBoolean("subjectForTermination", subjectForTermination);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		subjectForTermination = nbt.getBoolean("subjectForTermination");
		supernovaTicks = nbt.getInt("supernovaTicks");
	}

	@Override
	public float getSize() {
		float size = super.getSize();
		float ret = size;

		if (subjectForTermination && supernovaTicks >= 0) {
			if (supernovaTicks <= FULL_COLLAPSE_TICKS) {
				ret = MathHelper.lerp(Easing.EXPO_IN.ease((float) supernovaTicks / FULL_COLLAPSE_TICKS, 0f, 1f, 1f), size, 0.1f);
			} else if (supernovaTicks <= HOLD_COLLAPSE_TICKS) {
				ret = 0.1f;
			} else if (supernovaTicks <= EXPLOSION_TICKS) {
				ret = MathHelper.lerp(Easing.EXPO_OUT.ease((float) (supernovaTicks - HOLD_COLLAPSE_TICKS) / (EXPLOSION_TICKS - HOLD_COLLAPSE_TICKS), 0f, 1f, 1f), 0, size * 2f);
			} else {
				ret = 0f;
			}
		}

		return ret;
	}

	@Override
	public float getAlpha() {
		float ret = MinecraftClient.getInstance().player.hasStatusEffect(ModStatusEffects.STARGAZING) ? 1f : super.getAlpha();

		if (subjectForTermination && supernovaTicks >= 0) {
			if (supernovaTicks > HOLD_COLLAPSE_TICKS && supernovaTicks <= START_FADE_OUT_TICKS) {
				ret = 1f;
			} else if (supernovaTicks >= START_FADE_OUT_TICKS && supernovaTicks <= EXPLOSION_TICKS) {
				ret = MathHelper.lerp(Easing.SINE_OUT.ease((float) (supernovaTicks - START_FADE_OUT_TICKS) / (EXPLOSION_TICKS - START_FADE_OUT_TICKS), 0f, 1f, 1f), 1f, 0);
			} else if (supernovaTicks > EXPLOSION_TICKS) {
				ret = 0;
			}
		}

		return ret;
	}

	@Override
	public boolean canInteract() {
		return true;
	}

	@Override
	public Identifier getId() {
		return ID;
	}
}
