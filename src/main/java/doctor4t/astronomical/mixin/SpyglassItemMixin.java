package doctor4t.astronomical.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.cca.world.AstraSkyComponent;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.InteractableStar;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpyglassItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedList;

import static doctor4t.astronomical.common.Astronomical.*;

@Mixin(SpyglassItem.class)
public abstract class SpyglassItemMixin extends Item {
	public SpyglassItemMixin(Settings settings) {
		super(settings);
	}

	private float astra$getSkyAngle(long time) {
		double d = MathHelper.fractionalPart((double)time / 24000.0 - 0.25);
		double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
		return (float)(d * 2.0 + e) / 3.0F;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		boolean bl = world.isClient;
		if(true) {
			Quaternion rotation = Vec3f.POSITIVE_Z.getDegreesQuaternion(world.getSkyAngle(1) * 360.0F);
			//rotation.normalize();
			Vec3d vec = user.getRotationVector();
			AstraSkyComponent c = world.getComponent(AstraCardinalComponents.SKY);
			CelestialObject supernovad = null;
			for (CelestialObject obj : c.getCelestialObjects().stream().filter(g -> g.canInteract() && !((InteractableStar) g).subjectForTermination).toList()) {
				Vec3d rotatedVec = rotateViaQuat(obj.getDirectionVector(), rotation).normalize();
				double h = vec.x * rotatedVec.x + vec.y * rotatedVec.y + vec.z * rotatedVec.z;
				if (h > 0.99995 && rotatedVec.dotProduct(new Vec3d(0, 1, 0)) > 0) {
					if(!bl) {
						Vec3d pos = user.getPos().add(world.random.nextGaussian()*64, 0, world.random.nextGaussian()*64);
						Quaternion rotationTwo = Vec3f.POSITIVE_Z.getDegreesQuaternion(astra$getSkyAngle(world.getLunarTime() + 90) * 360.0F);
						world.getComponent(AstraCardinalComponents.FALL).addFall(rotateViaQuat(obj.getDirectionVector(), rotationTwo).normalize(), new Vec3d(pos.x, world.getTopY(Heightmap.Type.MOTION_BLOCKING, MathHelper.floor(pos.x), MathHelper.floor(pos.z)), pos.z));
						supernovad = obj;
						break;
					}
				}
			}
			if(supernovad != null) {
				((InteractableStar) supernovad).subjectForTermination = true;
				AstraCardinalComponents.SKY.sync(world);
			}
		}
	}
}
