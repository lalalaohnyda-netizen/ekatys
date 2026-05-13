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
        // Сначала считаем углы
        Killaura.onTick();
    }
}

// ВТОРОЙ МИКСИН ДЛЯ ПЕРЕХВАТА ПАКЕТОВ
@Mixin(PlayerMoveC2SPacket.class)
abstract class PlayerMoveC2SPacketMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (Killaura.isRotating) {
            // Нагло подменяем yaw/pitch прямо в конструкторе пакета
            // Используем аксессоры или миксины на поля пакета
            // Но чтобы не усложнять, давай поправим это в Killaura через пакеты напрямую
        }
    }
}
