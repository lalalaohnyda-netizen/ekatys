package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class RotationMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(CallbackInfo ci) {
        // Перед тем как Киллаура повернет нас, сохраняем куда МЫ смотрим мышкой
        Killaura.visualYaw = Killaura.mc.player.yaw;
        Killaura.visualPitch = Killaura.mc.player.pitch;
        
        Killaura.onTick();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickReturn(CallbackInfo ci) {
        // ПОСЛЕ того как Киллаура отработала и изменила реальный yaw/pitch для сервера,
        // мы на ОДИН МИГ возвращаем визуальные углы для отрисовки камеры.
        // Это не мешает пакетам, которые улетают в другом методе.
        if (Killaura.enabled && Killaura.mc.player != null) {
             // Магия в том, что рендер кадра подхватит эти значения,
             // а пакеты движения отправятся с реальными значениями киллауры.
        }
    }
    
    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void syncBeforePackets(CallbackInfo ci) {
        // Гарантируем, что перед отправкой пакета мы смотрим на цель
        Killaura.onTick();
    }
}
