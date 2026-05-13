package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class VisualsMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void renderTargetEffect(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (Killaura.target != null && entity.equals(Killaura.target)) {
            // Здесь можно добавить код для рендера частиц или подсветки
            // Для начала просто заставим его "светиться" (ESP-like эффект)
            entity.setGlowing(true); 
        } else if (entity.isGlowing() && !entity.equals(Killaura.target)) {
            entity.setGlowing(false);
        }
    }
}

