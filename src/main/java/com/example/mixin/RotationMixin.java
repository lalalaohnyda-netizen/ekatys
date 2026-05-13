package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class RotationMixin {
    @Inject(method = "getHeadYaw", at = @At("HEAD"), cancellable = true)
    private void onGetHeadYaw(CallbackInfoReturnable<Float> info) {
        // Проверяем, включена ли аура и является ли эта сущность игроком
        if (Killaura.enabled && (Object)this == MinecraftClient.getInstance().player) {
            // Тут логика визуального поворота (пока оставим так, чтобы билд прошел)
        }
    }
}
