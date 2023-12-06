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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
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
public class SpyglassItemMixin {
	@Inject(method = "use", at = @At("HEAD"))
	private void astra$spyglassSpawn(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		boolean bl = world.isClient;
		if(true) {
			Quaternion rotation = Vec3f.POSITIVE_Z.getDegreesQuaternion(world.getSkyAngle(1) * 360.0F);
			//rotation.normalize();
			Vec3d vec = user.getRotationVector();
			AstraSkyComponent c = world.getComponent(AstraCardinalComponents.SKY);
			CelestialObject supernovad = null;
			for (CelestialObject obj : c.getCelestialObjects().stream().filter(CelestialObject::canInteract).toList()) {
				Vec3d rotatedVec = rotateViaQuat(obj.getDirectionVector(), rotation).normalize();
				double h = vec.x * rotatedVec.x + vec.y * rotatedVec.y + vec.z * rotatedVec.z;
				if (h > 0.998) {
					if(!bl) {
						Vec3d pos = user.getPos().add(world.random.nextGaussian()*16, 0, world.random.nextGaussian()*16);
						world.getComponent(AstraCardinalComponents.FALL).addFall(rotatedVec, new Vec3d(pos.x, world.getTopY(Heightmap.Type.MOTION_BLOCKING, MathHelper.floor(pos.x), MathHelper.floor(pos.z)), pos.z));
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
