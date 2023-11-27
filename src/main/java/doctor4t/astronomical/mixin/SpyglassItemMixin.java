package doctor4t.astronomical.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sammy.lodestone.systems.rendering.particle.ParticleBuilders;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.cca.world.AstraSkyComponent;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
import doctor4t.astronomical.common.init.ModParticles;
import doctor4t.astronomical.common.structure.CelestialObject;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpyglassItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
			if(bl)
				ParticleBuilders.create(ModParticles.ORB)
				.setScale((0.2f))
				.setColor(1, 0, 0)
				.setAlpha(0, 1f, 0)
				.enableNoClip()
				.setLifetime(20)
				.setSpin(-world.random.nextFloat() / 7f)
				.spawn(world, user.getX()+vec.x*5, user.getEyeY()+vec.y*5,user.getZ()+vec.z*5);
			for (CelestialObject obj : c.getCelestialObjects().stream().filter(CelestialObject::canInteract).toList()) {
				Vec3d rotatedVec = rotateViaQuat(obj.getDirectionVector(), rotation);
				if(bl)
					ParticleBuilders.create(ModParticles.ORB)
					.setScale((0.2f))
					.setAlpha(0, 1f, 0)
					.enableNoClip()
					.setLifetime(20)
					.setSpin(-world.random.nextFloat() / 7f)
					.spawn(world, user.getX()+rotatedVec.x*5, user.getEyeY()+rotatedVec.y*5,user.getZ()+rotatedVec.z*5);
				if (vec.dotProduct(obj.getDirectionVector()) > 0.95f && !bl) {
					//this doesn't work. for some reason. will have to just send a packet from cli instead
						world.getComponent(AstraCardinalComponents.FALL).addFall(rotatedVec, user.getPos());
				}
			}
		}
	}

}
