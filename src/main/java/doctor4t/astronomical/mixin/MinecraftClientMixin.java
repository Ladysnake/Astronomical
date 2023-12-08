package doctor4t.astronomical.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import doctor4t.astronomical.common.Astronomical;
import doctor4t.astronomical.common.item.MarshmallowStickItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Final
	public GameOptions options;
	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Unique
	private boolean astronomical$holding = false;

	@WrapOperation(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"))
	private void astronomical$holding(MinecraftClient instance, boolean bl, Operation<Void> original) {
		var holding = this.options.attackKey.isPressed();
		if (holding != this.astronomical$holding) {
			this.astronomical$holding = holding;
			var buf = PacketByteBufs.create();
			buf.writeBoolean(holding);
			ClientPlayNetworking.send(Astronomical.HOLDING_PACKET, buf);
		}
		if (this.player == null || !(this.player.getMainHandStack().getItem() instanceof MarshmallowStickItem)) {
			original.call(instance, bl);
		}
	}
}
