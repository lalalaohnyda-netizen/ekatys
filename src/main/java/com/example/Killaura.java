package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Killaura {
    public static boolean enabled = true;
    private static MinecraftClient mc = MinecraftClient.getInstance();
    
    public static float serverYaw;
    public static float serverPitch;
    public static boolean isRotating = false;

    public static void onTick() {
        if (!enabled || mc.player == null || mc.world == null) {
            isRotating = false;
            return;
        }

        Entity target = findTarget();

        if (target != null) {
            calculateSilentRotation(target);
            isRotating = true;

            // ЖЕСТКАЯ СИНХРОНИЗАЦИЯ
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                if (mc.player.fallDistance > 0.05f && !mc.player.isOnGround()) {
                    
                    // Сохраняем твой реальный взгляд, чтобы экран не дернулся
                    float realYaw = mc.player.yaw;
                    float realPitch = mc.player.pitch;

                    // На ОДИН кадр ставим серверные углы
                    mc.player.yaw = serverYaw;
                    mc.player.pitch = serverPitch;

                    // Бьем (теперь игра думает, что ты смотришь в центр хитбокса)
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);

                    // Мгновенно возвращаем твой взгляд назад
                    mc.player.yaw = realYaw;
                    mc.player.pitch = realPitch;
                }
            }
        } else {
            isRotating = false;
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

    private static void calculateSilentRotation(Entity target) {
        // Наводка строго в центр хитбокса (Y + высота глаз / 1.5)
        double diffX = target.getX() - mc.player.getX();
        double diffY = (target.getY() + target.getEyeHeight(target.getPose()) / 1.5) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        serverYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        serverPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
    }
}
