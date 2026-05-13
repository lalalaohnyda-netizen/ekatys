package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class RotationMixin {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPackets(CallbackInfo ci) {
        Killaura.onTick();
    }

    // Подменяем Yaw ПРЯМО ПЕРЕД отправкой пакета на сервер
    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float redirectYaw(ClientPlayerEntity player) {
        return Killaura.isRotating ? Killaura.serverYaw : player.getYaw();
    }

    // Подменяем Pitch ПРЯМО ПЕРЕД отправкой пакета на сервер
    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float redirectPitch(ClientPlayerEntity player) {
        return Killaura.isRotating ? Killaura.serverPitch : player.getPitch();
    }
}
