package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Killaura {
    public static boolean enabled = true; // Включено по умолчанию
    private static MinecraftClient mc = MinecraftClient.getInstance();
    
    // Статичные углы для пакетов
    public static float serverYaw;
    public static float serverPitch;

    public static void onTick() {
        if (mc.player == null || mc.world == null) return;

        Entity target = null;
        double dist = 4.0;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player && entity.isAlive()) {
                double d = mc.player.distanceTo(entity);
                if (d < dist) { dist = d; target = entity; }
            }
        }

        if (target != null) {
            // ПЛАВНАЯ СИНХРОНИЗАЦИЯ: постоянно обновляем углы
            calculateRotation(target);
            
            // Шлем пакет поворота КАЖДЫЙ ТИК (чтобы сервер видел наводку всегда)
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(serverYaw, serverPitch, mc.player.isOnGround()));

            // УДАРЫ: добавим проверку на кулдаун (атака раз в ~0.6с)
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                if (mc.player.fallDistance > 0.1f || !mc.player.isOnGround()) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }

    private static void calculateRotation(Entity target) {
        double diffX = target.getX() - mc.player.getX();
        double diffY = (target.getY() + target.getEyeHeight(target.getPose())) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        // Обновляем серверные переменные
        serverYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        serverPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
    }
}
