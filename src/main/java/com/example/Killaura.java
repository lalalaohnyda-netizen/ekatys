package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import java.util.Random;

public class Killaura {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static LivingEntity target;
    public static boolean enabled = true;
    
    public static float rotYaw, rotPitch;
    public static boolean isRotating = false;
    private static final Random rnd = new Random();

    // Настройки обхода
    private static final float speed = 18.0F; 
    private static float lastRandomYaw, lastRandomPitch;

    public static void onTick() {
        if (!enabled || mc.player == null || mc.world == null) {
            isRotating = false;
            return;
        }

        // --- АВТОБЕГ (Sprint Helper) ---
        // Если зажата кнопка вперед и нет помех (вода, голод, приседание)
        if (mc.player.forwardSpeed > 0 && !mc.player.isSneaking() && !mc.player.horizontalCollision) {
            mc.player.setSprinting(true);
        }

        target = findTarget();

        if (target != null) {
            updateRotation();
            isRotating = true;
            
            // Удар только если кулдаун прошел (крит)
            if (mc.player.getAttackCooldownProgress(0.5f) >= 0.95f) {
                // Если мы в падении — бьем
                if (mc.player.fallDistance > 0.0f || mc.player.abilities.creativeMode) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        } else {
            isRotating = false;
            rotYaw = mc.player.yaw;
            rotPitch = mc.player.pitch;
        }
    }

    private static void updateRotation() {
        // Добавляем небольшой рандом к центру хитбокса (bypass)
        if (mc.player.age % 5 == 0) {
            lastRandomYaw = (rnd.nextFloat() - 0.5f) * 1.2f;
            lastRandomPitch = (rnd.nextFloat() - 0.5f) * 1.2f;
        }

        double diffX = target.getX() - mc.player.getX();
        // Наводимся чуть выше ног, но ниже головы + рандом
        double diffY = (target.getY() + target.getHeight() * 0.45) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F + lastRandomYaw;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) + lastRandomPitch;

        float yawDelta = MathHelper.wrapDegrees(targetYaw - rotYaw);
        float pitchDelta = MathHelper.wrapDegrees(targetPitch - rotPitch);

        // Плавность с микро-рывками для имитации руки
        float currentSpeed = speed + (rnd.nextFloat() * 2.0f);
        float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 1.2F), currentSpeed);
        float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 1.2F), currentSpeed);

        rotYaw += (yawDelta > 0 ? clampedYaw : -clampedYaw);
        rotPitch = MathHelper.clamp(rotPitch + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -90, 90);

        // GCD (Mouse Sensitivity) Bypass
        float f = (float) (mc.options.mouseSensitivity * 0.6F + 0.2F);
        float gcd = f * f * f * 1.2F;
        rotYaw -= (rotYaw - (rotYaw - yawDelta)) % gcd;
        rotPitch -= (rotPitch - (rotPitch - pitchDelta)) % gcd;
    }

    private static LivingEntity findTarget() {
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof PlayerEntity && e != mc.player && e.isAlive() && mc.player.distanceTo(e) < 4.2) {
                return (LivingEntity) e;
            }
        }
        return null;
    }
}
