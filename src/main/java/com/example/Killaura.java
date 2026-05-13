package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Killaura {
    public static boolean enabled = false;
    private static MinecraftClient mc = MinecraftClient.getInstance();
    
    // Храним углы для сервера
    public static float serverYaw;
    public static float serverPitch;
    public static boolean isRotating = false;

    public static void onTick() {
        if (!enabled || mc.player == null || mc.world == null) {
            isRotating = false;
            return;
        }

        Entity target = null;
        double dist = 4.0;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player && entity.isAlive()) {
                double d = mc.player.distanceTo(entity);
                if (d < dist) {
                    dist = d;
                    target = entity;
                }
            }
        }

        if (target != null) {
            // Считаем углы Silent наводки
            calculateSilentRotation(target);
            isRotating = true;

            // AUTO-CRIT логика: бьем только в падении
            if (mc.player.fallDistance > 0.05f && !mc.player.isOnGround()) {
                if (mc.player.getAttackCooldownProgress(0.5f) >= 0.9f) {
                    // Перед ударом сервер должен думать, что мы смотрим на цель
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(serverYaw, serverPitch, mc.player.isOnGround()));
                    
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        } else {
            isRotating = false;
        }
    }

    private static void calculateSilentRotation(Entity entity) {
        Vec3d targetPos = entity.getEyePos();
        Vec3d playerPos = mc.player.getEyePos();
        
        double diffX = targetPos.x - playerPos.x;
        double diffY = targetPos.y - playerPos.y;
        double diffZ = targetPos.z - playerPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        serverYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        serverPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
    }
}
