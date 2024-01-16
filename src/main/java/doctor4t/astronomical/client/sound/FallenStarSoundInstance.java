package doctor4t.astronomical.client.sound;

import doctor4t.astronomical.common.entity.FallenStarEntity;
import doctor4t.astronomical.common.init.ModSoundEvents;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

public class FallenStarSoundInstance extends MovingSoundInstance {
	protected final FallenStarEntity fallenStar;

	public FallenStarSoundInstance(FallenStarEntity entity) {
		super(ModSoundEvents.STAR_AMBIENT, SoundCategory.AMBIENT, SoundInstance.createRandom());
		this.fallenStar = entity;
		this.x = (float) entity.getX();
		this.y = (float) entity.getY();
		this.z = (float) entity.getZ();
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 2.5F;
	}

	@Override
	public void tick() {
		if (!this.fallenStar.isRemoved()) {
			this.x = (float) this.fallenStar.getX();
			this.y = (float) this.fallenStar.getY();
			this.z = (float) this.fallenStar.getZ();

			this.pitch = 1.0F;
			this.volume = 2.5F;
		} else {
			this.setDone();
		}
	}

	@Override
	public boolean shouldAlwaysPlay() {
		return true;
	}

	@Override
	public boolean canPlay() {
		return !this.fallenStar.isSilent();
	}
}
