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

            // СИЛОВАЯ НАВОДКА: Шлем пакет поворота в каждом тике
            // Сервер БУДЕТ видеть, что ты смотришь на цель, но твоя камера не шелохнется
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(
                serverYaw, 
                serverPitch, 
                mc.player.isOnGround()
            ));

            // Логика удара
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                if (mc.player.fallDistance > 0.05f && !mc.player.isOnGround()) {
                    // Машем рукой и бьем
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        } else {
            isRotating = false;
        }
    }

    private static Entity findTarget() {
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player && entity.isAlive()) {
                if (mc.player.distanceTo(entity) <= 4.2) return entity;
            }
        }
        return null;
    }

    private static void calculateSilentRotation(Entity target) {
        double diffX = target.getX() - mc.player.getX();
        double diffY = (target.getY() + target.getEyeHeight(target.getPose())) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        serverYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        serverPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
    }
}
