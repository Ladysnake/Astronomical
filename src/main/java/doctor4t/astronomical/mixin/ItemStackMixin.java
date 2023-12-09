package doctor4t.astronomical.mixin;

import doctor4t.astronomical.common.init.ModItems;
import doctor4t.astronomical.common.item.MarshmallowStickItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	public abstract Item getItem();

	@Inject(method = "getEatSound", at = @At("HEAD"), cancellable = true)
	private void astronomical$burnt(CallbackInfoReturnable<SoundEvent> cir) {
		if (this.getItem() == ModItems.MARSHMALLOW_STICK) {
			cir.setReturnValue(((MarshmallowStickItem) ModItems.MARSHMALLOW_STICK).getEatSound((ItemStack) (Object) this));
		}
	}
}
