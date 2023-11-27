package doctor4t.astronomical.mixin;

import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.cca.world.AstraSkyComponent;
import static doctor4t.astronomical.common.Astronomical.UP;
import doctor4t.astronomical.common.structure.CelestialObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpyglassItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpyglassItem.class)
public class SpyglassItemMixin {
	@Inject(method = "use", at = @At("HEAD"))
	private void astra$spyglassSpawn(@NotNull World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
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
