package com.example.mixin;

import com.example.Killaura;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class VisualsMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void renderTargetEffect(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Подсвечиваем только живую цель киллауры
        if (Killaura.target != null && entity == Killaura.target) {
            entity.setGlowing(true);
        } else if (entity.isGlowing()) {
            // Убираем свечение, если это больше не цель
            entity.setGlowing(false);
        }
    }
}
