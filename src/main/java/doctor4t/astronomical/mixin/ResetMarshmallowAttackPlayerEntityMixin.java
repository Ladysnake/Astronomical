package doctor4t.astronomical.mixin;

import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.common.item.MarshmallowStickItem;
import doctor4t.astronomical.common.util.PlayerAttackHeld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ResetMarshmallowAttackPlayerEntityMixin extends LivingEntity implements PlayerAttackHeld {
	protected ResetMarshmallowAttackPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean astronomical$isHoldingAttack() {
		return AstraCardinalComponents.HOLDING.get(this).isHolding();
	}

	@Override
	public void astronomical$setHoldingAttack(boolean attackHeld) {
		AstraCardinalComponents.HOLDING.get(this).setHolding(attackHeld);
	}

	@Inject(method = "resetLastAttackedTicks", at = @At("HEAD"), cancellable = true)
	private void astronomical$resetMarshmallowAttack(CallbackInfo ci) {
		if (this.getMainHandStack().getItem() instanceof MarshmallowStickItem) {
			ci.cancel();
		}
	}
}
