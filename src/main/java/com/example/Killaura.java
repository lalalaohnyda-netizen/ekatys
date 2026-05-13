package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

public class Killaura {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static LivingEntity target;
    public static Vector2f rotateVector = new Vector2f(0, 0);
    public static boolean isRotating = false;

    // Скорость поворота (можно менять)
    private static final float speed = 15.0F; 

    public static void onTick() {
        if (mc.player == null || mc.world == null) return;

        target = findTarget();

        if (target != null) {
            updateRotation();
            isRotating = true;
            
            // Механика удара (Криты)
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f && mc.player.fallDistance > 0) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
            }
        } else {
            isRotating = false;
            // Плавно возвращаем вектор к обычному взгляду, чтобы не было рывка при включении
            rotateVector = new Vector2f(mc.player.yaw, mc.player.pitch);
        }
    }

    private static void updateRotation() {
        // Расчет углов на центр хитбокса (как в скрипте)
        double diffX = target.getX() - mc.player.getX();
        double diffY = (target.getY() + target.getHeight() * 0.5) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        float yawDelta = MathHelper.wrapDegrees(targetYaw - rotateVector.x);
        float pitchDelta = MathHelper.wrapDegrees(targetPitch - rotateVector.y);

        // Плавность (Clamping)
        float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 1.0F), speed);
        float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 1.0F), speed);

        float newYaw = rotateVector.x + (yawDelta > 0 ? clampedYaw : -clampedYaw);
        float newPitch = MathHelper.clamp(rotateVector.y + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -90, 90);

        // Настройка чувствительности (GCD Fix), чтобы не палил античит
        float f = (float) (mc.options.mouseSensitivity * 0.6F + 0.2F);
        float gcd = f * f * f * 1.2F;
        
        newYaw -= (newYaw - rotateVector.x) % gcd;
        newPitch -= (newPitch - rotateVector.y) % gcd;

        rotateVector = new Vector2f(newYaw, newPitch);
    }

    private static LivingEntity findTarget() {
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof PlayerEntity && e != mc.player && e.isAlive() && mc.player.distanceTo(e) < 4.5) {
                return (LivingEntity) e;
            }
        }
        return null;
    }
}
