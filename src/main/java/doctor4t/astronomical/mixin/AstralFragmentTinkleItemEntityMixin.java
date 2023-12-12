package doctor4t.astronomical.mixin;

import com.sammy.lodestone.setup.LodestoneParticles;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.init.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class AstralFragmentTinkleItemEntityMixin extends Entity {
	@Shadow
	@Final
	public float uniqueOffset;

	public AstralFragmentTinkleItemEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	public abstract int getItemAge();

	@Shadow
	public abstract ItemStack getStack();

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (this.getStack().isOf(ModItems.ASTRAL_FRAGMENT)) {
			RandomGenerator random = RandomGenerator.createLegacy();

			if (random.nextInt(5) == 0) {
				float l = MathHelper.sin(((float) this.getItemAge() + MinecraftClient.getInstance().getTickDelta()) / 10.0F + this.uniqueOffset) * 0.1F + 0.1F;

				ParticleBuilders.create(LodestoneParticles.TWINKLE_PARTICLE)
					.setScale(.05f + random.nextFloat() * .1f)
					.setColor(Astronomical.STAR_PURPLE, Astronomical.STAR_PURPLE)
					.setAlpha(0, 1f, 0)
					.setAlphaEasing(Easing.QUAD_OUT, Easing.SINE_OUT)
					.enableNoClip()
					.setLifetime(20)
					.setSpinOffset(random.nextFloat() * 360f)
					.setSpin((float) (random.nextGaussian() / 20f))
					.randomOffset(.2f)
					.spawn(this.world, this.getX() + (l + 0.25F * .1f), this.getY() + .4f, this.getZ());
			}
		}
	}
}
