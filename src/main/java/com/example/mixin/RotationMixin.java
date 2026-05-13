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
        // Обновляем логику киллауры перед отправкой пакетов
        Killaura.onTick();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;yaw:F"))
    private float redirectYaw(ClientPlayerEntity player) {
        // Если киллаура нацелена, подсовываем серверу Silent Yaw
        return Killaura.isRotating ? Killaura.serverYaw : player.yaw;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;pitch:F"))
    private float redirectPitch(ClientPlayerEntity player) {
        // То же самое для Pitch
        return Killaura.isRotating ? Killaura.serverPitch : player.pitch;
    }
}
