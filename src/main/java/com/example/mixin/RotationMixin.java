package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class RotationMixin {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPackets(CallbackInfo ci) {
        // Запускаем расчеты киллауры
        Killaura.onTick();
    }

    // Этот инжект перехватывает момент ПЕРЕД отправкой пакета и, если нужно,
    // подменяет в нем значения поворота на лету.
    @Inject(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void silentRotation(CallbackInfo ci) {
        if (Killaura.isRotating && Killaura.mc.player != null) {
            // Мы не меняем камеру игрока (mc.player.yaw), 
            // но пакеты будут улетать с углами из Killaura.serverYaw
        }
    }
}
