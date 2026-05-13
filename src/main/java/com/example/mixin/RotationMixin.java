package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class RotationMixin {

    // Перехватываем создание пакета движения с поворотом
    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float redirectYaw(ClientPlayerEntity player) {
        // Если киллаура работает, отдаем серверу Silent Yaw, иначе обычный
        return Killaura.isRotating ? Killaura.serverYaw : player.getYaw();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float redirectPitch(ClientPlayerEntity player) {
        // Отдаем Silent Pitch
        return Killaura.isRotating ? Killaura.serverPitch : player.getPitch();
    }
}
