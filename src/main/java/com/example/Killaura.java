package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import java.util.Comparator;

public class Killaura {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static LivingEntity target;
    public static boolean enabled = true;
    
    public static float rotYaw, rotPitch;
    public static boolean isRotating = false;
    private static float animTicks = 0;

    public static void onTick() {
        if (!enabled || mc.player == null) return;

        // Жесткий автоспринт (пакетный)
        if (mc.player.forwardSpeed > 0 && !mc.player.isSneaking()) {
            mc.player.setSprinting(true);
        }

        // Фиксация цели: если текущая цель жива и рядом — не меняем её
        if (target == null || !target.isAlive() || mc.player.distanceTo(target) > 4.0) {
            target = findTarget();
        }

        if (target != null) {
            updateRotation();
            isRotating = true;
            
            // Удар (крит-тайминг)
            if (mc.player.getAttackCooldownProgress(0.0f) >= 0.95f) {
                if (mc.player.fallDistance > 0.05f) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        } else {
            isRotating = false;
        }
    }

    private static void updateRotation() {
        animTicks += 1.0f;
        
        double diffX = target.getX() - mc.player.getX();
        double diffZ = target.getZ() - mc.player.getZ();
        double diffY = (target.getY() + target.getHeight() * 0.5) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float tYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float tPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        // Bypass: Плавное экспоненциальное сглаживание (античит не видит резких шагов)
        rotYaw = lerpAngle(rotYaw, tYaw, 0.2f + (float)Math.random() * 0.1f);
        rotPitch = lerpAngle(rotPitch, tPitch, 0.2f + (float)Math.random() * 0.1f);

        // Накладываем микро-движения (Noise)
        rotYaw += Math.sin(animTicks * 0.2) * 0.5;
        rotPitch += Math.cos(animTicks * 0.2) * 0.5;
    }

    private static float lerpAngle(float from, float to, float pct) {
        float d = MathHelper.wrapDegrees(to - from);
        return from + d * pct;
    }

    private static LivingEntity findTarget() {
        return mc.world.getEntitiesByClass(PlayerEntity.class, mc.player.getBoundingBox().expand(3.8), 
            e -> e != mc.player && e.isAlive())
            .stream().min(Comparator.comparingDouble(mc.player::distanceTo)).orElse(null);
    }
}
