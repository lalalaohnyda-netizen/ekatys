package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class Killaura {
    public static boolean enabled = false;
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static void onTick() {
        if (!enabled || mc.player == null || mc.world == null) return;

        // Ищем ближайшую цель (игрока) в радиусе 4 блоков
        Entity target = null;
        double shortestDistance = 4.0;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player && entity.isAlive()) {
                double dist = mc.player.distanceTo(entity);
                if (dist < shortestDistance) {
                    shortestDistance = dist;
                    target = entity;
                }
            }
        }

        if (target != null) {
            // 1. ЖЕСТКАЯ НАВОДКА (ты будешь видеть, как голова крутится за целью)
            lookAtEntity(target);

            // 2. ЛОГИКА КРИТОВ (Auto-Crit)
            // Бьем только если мы падаем (fallDistance > 0) или не на земле, чтобы прошел крит
            if (mc.player.fallDistance > 0.0f || !mc.player.isOnGround()) {
                if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) { // Ждем отката удара
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }

    private static void lookAtEntity(Entity entity) {
        Vec3d targetPos = entity.getEyePos();
        Vec3d playerPos = mc.player.getEyePos();
        
        double diffX = targetPos.x - playerPos.x;
        double diffY = targetPos.y - playerPos.y;
        double diffZ = targetPos.z - playerPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        // Устанавливаем углы поворота самому игроку (будет видно всем и тебе)
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }
}
