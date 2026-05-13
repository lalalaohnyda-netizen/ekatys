package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class RotationMixin {

    private float tempYaw;
    private float tempPitch;

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo ci) {
        Killaura.onTick(); // Считаем плавную наводку

        if (Killaura.isRotating && Killaura.mc.player != null) {
            // Сохраняем твои настоящие углы (куда ты смотришь мышкой)
            tempYaw = Killaura.mc.player.yaw;
            tempPitch = Killaura.mc.player.pitch;

            // ПОДМЕНЯЕМ углы на читерские прямо в игроке
            Killaura.mc.player.yaw = Killaura.serverYaw;
            Killaura.mc.player.pitch = Killaura.serverPitch;
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("RETURN"))
    private void onSendMovementPacketsReturn(CallbackInfo ci) {
        if (Killaura.isRotating && Killaura.mc.player != null) {
            // Возвращаем твои углы обратно СРАЗУ после отправки пакетов
            // Твой экран даже не успеет понять, что углы менялись
            Killaura.mc.player.yaw = tempYaw;
            Killaura.mc.player.pitch = tempPitch;
        }
    }
}
