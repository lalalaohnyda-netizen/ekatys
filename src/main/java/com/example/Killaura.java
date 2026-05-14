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
        if (!enabled || mc.player == null || mc.world == null) {
            isRotating = false;
            return;
        }

        // --- ЖЕЛЕЗНЫЙ АВТОСПРИНТ ---
        if (mc.player.forwardSpeed > 0 && !mc.player.isSneaking() && !mc.player.horizontalCollision) {
            mc.player.setSprinting(true);
        }

        // --- ФИКСАЦИЯ ЦЕЛИ (Target Focus) ---
        if (target == null || !target.isAlive() || mc.player.distanceTo(target) > 3.8) {
            target = findTarget();
        }

        if (target != null) {
            updateRotation();
            isRotating = true;
            
            // Криты + Тайминг удара
            if (mc.player.getAttackCooldownProgress(0.0f) >= 0.93f) {
                if (mc.player.fallDistance > 0.08f || mc.player.abilities.creativeMode) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
            // Визуал (свечение)
            target.setGlowing(true);
        } else {
            isRotating = false;
        }
    }

    private static void updateRotation() {
        animTicks += 1.0f;
        
        double diffX = target.getX() - mc.player.getX();
        double diffZ = target.getZ() - mc.player.getZ();
        // Рандомная точка (анти-флаг)
        double diffY = (target.getY() + target.getHeight() * (0.45 + Math.sin(animTicks * 0.1) * 0.1)) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float tYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float tPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        // Плавная доводка (Lerp)
        rotYaw = rotYaw + MathHelper.wrapDegrees(tYaw - rotYaw) * 0.3f;
        rotPitch = rotPitch + (tPitch - rotPitch) * 0.3f;

        // GCD Фикс
        float f = (float) (mc.options.mouseSensitivity * 0.6F + 0.2F);
        float gcd = f * f * f * 1.2F;
        rotYaw -= (rotYaw - mc.player.yaw) % gcd;
        rotPitch -= (rotPitch - mc.player.pitch) % gcd;
    }

    private static LivingEntity findTarget() {
        return mc.world.getEntitiesByClass(PlayerEntity.class, mc.player.getBoundingBox().expand(3.8), 
            e -> e != mc.player && e.isAlive())
            .stream()
            .min(Comparator.comparingDouble(mc.player::distanceTo))
            .orElse(null);
    }
}
