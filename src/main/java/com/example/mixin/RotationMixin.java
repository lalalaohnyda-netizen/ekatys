package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class RotationMixin {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPackets(CallbackInfo ci) {
        // Просто запускаем логику киллауры. Подмена пакетов пойдет из самого класса Killaura.
        Killaura.onTick();
    }
}
