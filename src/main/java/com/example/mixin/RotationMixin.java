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

    // Подменяем Yaw ПРЯМО ПЕРЕД отправкой пакета, обращаясь к полю напрямую
    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;yaw:F", opcode = 180)) // 180 = GETFIELD
    private float redirectYaw(ClientPlayerEntity player) {
        return Killaura.isRotating ? Killaura.serverYaw : player.yaw;
    }

    // Подменяем Pitch ПРЯМО ПЕРЕД отправкой пакета
    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;pitch:F", opcode = 180))
    private float redirectPitch(ClientPlayerEntity player) {
        return Killaura.isRotating ? Killaura.serverPitch : player.pitch;
    }
}
