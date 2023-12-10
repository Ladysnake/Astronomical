package doctor4t.astronomical.mixin;

import doctor4t.astronomical.client.sound.FallenStarSoundInstance;
import doctor4t.astronomical.common.entity.FallenStarEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class PlayFallenStarSoundInstanceClientPlayNetworkHandlerMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "playEntitySpawnSound", at = @At("TAIL"))
	private void playEntitySpawnSound(Entity entity, CallbackInfo ci) {
		if (entity instanceof FallenStarEntity fallenStarEntity) {
			this.client.getSoundManager().playNextTick(new FallenStarSoundInstance(fallenStarEntity));
		}
	}
}
