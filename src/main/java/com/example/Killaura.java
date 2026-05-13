package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

public class Killaura {
    public static boolean enabled = false;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void onTick() {
        if (!enabled || mc.player == null || mc.world == null) return;

        // Ищем цель в радиусе 3.5 блока
        Entity target = null;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player && entity.isAlive()) {
                if (mc.player.distanceTo(entity) <= 3.5f) {
                    target = entity;
                    break;
                }
            }
        }

        if (target != null) {
            // Считаем ротации
            float[] rots = getRotations(target);

            // SILENT ROTATION: отправляем пакет поворота серверу
            // Благодаря этому сервер и другие игроки видят, что ты повернут, а у тебя камера не дергается
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(
                    rots[0], rots[1], mc.player.isOnGround()
            ));

            // Бьем, если КД прошел
            if (mc.player.getAttackCooldownProgress(0) >= 1) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private static float[] getRotations(Entity e) {
        double dX = e.getX() - mc.player.getX();
        double dY = e.getEyeY() - (mc.player.getY() + mc.player.getStandingEyeHeight());
        double dZ = e.getZ() - mc.player.getZ();
        double dist = Math.sqrt(dX * dX + dZ * dZ);
        float yaw = (float) (Math.atan2(dZ, dX) * 180 / Math.PI) - 90;
        float pitch = (float) -(Math.atan2(dY, dist) * 180 / Math.PI);
        return new float[]{yaw, pitch};
    }
}

