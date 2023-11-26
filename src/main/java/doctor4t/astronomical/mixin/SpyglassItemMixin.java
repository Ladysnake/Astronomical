package doctor4t.astronomical.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.cca.world.AstraSkyComponent;
import doctor4t.astronomical.client.render.world.AstraSkyRenderer;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static doctor4t.astronomical.common.Astronomical.*;

@Mixin(SpyglassItem.class)
public class SpyglassItemMixin {
	@Inject(method = "use", at = @At("HEAD"))
	private void astra$spyglassSpawn(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		//TODO THIS DOESN'T WORK YET
		if(!world.isClient) {
			//Quaternion rotation = Vec3f.POSITIVE_Z.getDegreesQuaternion(world.getSkyAngle(1) * 360.0F);
			Vec3d vec = user.getRotationVector();//rotateViaQuat(user.getRotationVector(), invert(rotation));
			AstraSkyComponent c = world.getComponent(AstraCardinalComponents.SKY);
			for (CelestialObject obj : c.getCelestialObjects().stream().filter(CelestialObject::canInteract).toList()) {
				Vec3d rotatedVec = obj.getDirectionVector();
				if (vec.dotProduct(obj.getDirectionVector()) > 0.9f) {
					if (rotatedVec.dotProduct(UP) > 0) {
						world.getComponent(AstraCardinalComponents.FALL).addFall(rotatedVec, user.getPos());
					}
				}
			}
		}
	}

}
