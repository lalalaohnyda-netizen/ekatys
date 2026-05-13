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
        // Вызываем логику наводки прямо перед отправкой пакетов движения
        Killaura.onTick();
    }
}
