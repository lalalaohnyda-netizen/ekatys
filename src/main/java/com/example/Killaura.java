package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class Killaura {
    public static boolean enabled = true;
    public static MinecraftClient mc = MinecraftClient.getInstance();
    
    public static Entity targetEntity = null;
    public static float serverYaw, serverPitch;
    public static boolean isRotating = false;

    public static void onTick() {
        if (!enabled || mc.player == null || mc.world == null) {
            targetEntity = null;
            isRotating = false;
            return;
        }

        if (targetEntity != null && (!targetEntity.isAlive() || mc.player.distanceTo(targetEntity) > 4.5)) {
            targetEntity = null;
        }

        if (targetEntity == null) {
            targetEntity = findBestTarget();
        }

        if (targetEntity != null) {
            updateRotation(targetEntity);
            isRotating = true;

            // 1 удар, строго крит, строго по КД
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                if (mc.player.fallDistance > 0.05f && !mc.player.isOnGround()) {
                    mc.interactionManager.attackEntity(mc.player, targetEntity);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        } else {
            isRotating = false;
        }
    }

    private static Entity findBestTarget() {
        Entity best = null;
        double dist = 4.2;
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof PlayerEntity && e != mc.player && e.isAlive()) {
                double d = mc.player.distanceTo(e);
                if (d < dist) { dist = d; best = e; }
            }
        }
        return best;
    }

    private static void updateRotation(Entity target) {
        double diffX = target.getX() - mc.player.getX();
        double diffY = (target.getY() + target.getHeight() / 2.0) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        // Плавная доводка
        serverYaw = interpolate(serverYaw, targetYaw, 0.25f);
        serverPitch = interpolate(serverPitch, targetPitch, 0.25f);
    }

    private static float interpolate(float current, float target, float speed) {
        float diff = ((target - current + 180) % 360) - 180;
        return current + diff * speed;
    }
}
