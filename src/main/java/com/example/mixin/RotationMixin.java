package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class RotationMixin {

    private float visualYaw;
    private float visualPitch;

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void compensateBefore(CallbackInfo ci) {
        Killaura.onTick(); // Запускаем логику киллауры

        if (Killaura.isRotating) {
            // 1. Сохраняем то, что ты видишь мышкой
            visualYaw = Killaura.mc.player.yaw;
            visualPitch = Killaura.mc.player.pitch;

            // 2. В ПАКЕТЫ ПИШЕМ КИЛЛАУРУ
            Killaura.mc.player.yaw = Killaura.rotateVector.x;
            Killaura.mc.player.pitch = Killaura.rotateVector.y;
            
            // Также обновляем углы головы (чтобы другие видели плавный поворот)
            Killaura.mc.player.rotationYawHead = Killaura.rotateVector.x;
            Killaura.mc.player.renderYawOffset = Killaura.rotateVector.x;
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("RETURN"))
    private void compensateAfter(CallbackInfo ci) {
        if (Killaura.isRotating) {
            // 3. ВОЗВРАЩАЕМ ВИЗУАЛ ОБРАТНО ТЕБЕ
            // Сразу после того как пакет улетел, ставим твои углы от мышки.
            // Рендер кадра происходит после этого, поэтому ты не увидишь рывка.
            Killaura.mc.player.yaw = visualYaw;
            Killaura.mc.player.pitch = visualPitch;
        }
    }
}
