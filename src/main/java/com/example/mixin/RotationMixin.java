package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class RotationMixin {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPackets(CallbackInfo ci) {
        Killaura.onTick();
    }

    @Inject(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCameraVar()Z"), cancellable = true)
    private void silentRotationManual(CallbackInfo ci) {
        // Это заставит сервер получать углы из Killaura.serverYaw/Pitch 
        // Если ты используешь метод с подменой yaw внутри Killaura (как выше),
        // то этот миксин просто вызывает тик.
    }
}
