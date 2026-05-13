package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class Killaura {
    public static boolean enabled = true; 
    private static MinecraftClient mc = MinecraftClient.getInstance();
    
    // Переменные для Silent Rotation (их будет забирать миксин)
    public static float serverYaw;
    public static float serverPitch;
    public static boolean isRotating = false;

    public static void onTick() {
        // Если выключено или мы не в мире — сбрасываем ротацию
        if (!enabled || mc.player == null || mc.world == null) {
            isRotating = false;
            return;
        }

        Entity target = findTarget();

        if (target != null) {
            // 1. Рассчитываем углы для сервера
            calculateSilentRotation(target);
            isRotating = true; // Миксин теперь начнет подменять пакеты

            // 2. Логика удара (Auto-Crit)
            // Бьем только когда полоска атаки полная (>= 1.0)
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                // Бьем только если мы в падении (крит)
                if (mc.player.fallDistance > 0.05f && !mc.player.isOnGround()) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        } else {
            // Если цели нет — выключаем подмену углов
            isRotating = false;
        }
    }

    private static Entity findTarget() {
        Entity bestTarget = null;
        double shortestDist = 4.2; // Дистанция удара

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player && entity.isAlive()) {
                double dist = mc.player.distanceTo(entity);
                if (dist < shortestDist) {
                    shortestDist = dist;
                    bestTarget = entity;
                }
            }
        }
        return bestTarget;
    }

    private static void calculateSilentRotation(Entity target) {
        // Координаты глаз цели
        double diffX = target.getX() - mc.player.getX();
        double diffY = (target.getY() + target.getEyeHeight(target.getPose())) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        // Математика поворота
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        serverYaw = yaw;
        serverPitch = pitch;
    }
}
