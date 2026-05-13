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
        Killaura.onTick();

        if (Killaura.isRotating && Killaura.mc.player != null) {
            visualYaw = Killaura.mc.player.yaw;
            visualPitch = Killaura.mc.player.pitch;

            // Ставим углы из киллауры для пакетов
            Killaura.mc.player.yaw = Killaura.rotYaw;
            Killaura.mc.player.pitch = Killaura.rotPitch;
            
            Killaura.mc.player.rotationYawHead = Killaura.rotYaw;
            Killaura.mc.player.renderYawOffset = Killaura.rotYaw;
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("RETURN"))
    private void compensateAfter(CallbackInfo ci) {
        if (Killaura.isRotating && Killaura.mc.player != null) {
            // Возвращаем визуальный вид игроку
            Killaura.mc.player.yaw = visualYaw;
            Killaura.mc.player.pitch = visualPitch;
        }
    }
}
