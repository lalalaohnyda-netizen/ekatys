package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class Killaura {
    public static boolean enabled = true;
    public static MinecraftClient mc = MinecraftClient.getInstance();
    
    // Храним твои визуальные углы, чтобы камера не дергалась
    public static float visualYaw;
    public static float visualPitch;

    public static void onTick() {
        if (!enabled || mc.player == null || mc.world == null) return;

        Entity target = findTarget();

        if (target != null) {
            // Реально поворачиваем игрока на цель (центр хитбокса)
            lookAtCenter(target);

            // Один четкий удар в падении
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                if (mc.player.fallDistance > 0.05f || !mc.player.isOnGround()) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }

    private static Entity findTarget() {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player && entity.isAlive()) {
                if (mc.player.distanceTo(entity) <= 4.0) return entity;
            }
        }
        return null;
    }

    private static void lookAtCenter(Entity target) {
        double diffX = target.getX() - mc.player.getX();
        double diffY = (target.getY() + target.getHeight() / 2.0) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        // Устанавливаем РЕАЛЬНЫЕ углы (их увидит сервер)
        mc.player.yaw = yaw;
        mc.player.pitch = pitch;
    }
}
