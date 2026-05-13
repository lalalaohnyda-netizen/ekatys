package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class RotationMixin {

    @Shadow public float yaw;
    @Shadow public float pitch;

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void beforeSendPackets(CallbackInfo ci) {
        Killaura.onTick();
    }

    // ПОДМЕНА ДЛЯ СЕРВЕРА (Silent Rotation)
    // Мы перехватываем момент отправки данных в пакет
    
    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;yaw:F", opcode = 180)) // GETFIELD
    private float getYawForServer(ClientPlayerEntity player) {
        return Killaura.isRotating ? Killaura.serverYaw : player.yaw;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;pitch:F", opcode = 180)) // GETFIELD
    private float getPitchForServer(ClientPlayerEntity player) {
        return Killaura.isRotating ? Killaura.serverPitch : player.pitch;
    }
}
