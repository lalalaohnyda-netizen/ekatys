package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;

public class Killaura {
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static LivingEntity target;
    public static boolean enabled = true; // Вернул переменную для ExampleMod
    
    public static float rotYaw, rotPitch;
    public static boolean isRotating = false;

    private static final float speed = 15.0F; 

    public static void onTick() {
        if (!enabled || mc.player == null || mc.world == null) {
            isRotating = false;
            return;
        }

        target = findTarget();

        if (target != null) {
            updateRotation();
            isRotating = true;
            
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f && mc.player.fallDistance > 0) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        } else {
            isRotating = false;
            rotYaw = mc.player.yaw;
            rotPitch = mc.player.pitch;
        }
    }

    private static void updateRotation() {
        double diffX = target.getX() - mc.player.getX();
        double diffY = (target.getY() + target.getHeight() * 0.5) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffZ = target.getZ() - mc.player.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        float yawDelta = MathHelper.wrapDegrees(targetYaw - rotYaw);
        float pitchDelta = MathHelper.wrapDegrees(targetPitch - rotPitch);

        float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 1.0F), speed);
        float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 1.0F), speed);

        rotYaw += (yawDelta > 0 ? clampedYaw : -clampedYaw);
        rotPitch = MathHelper.clamp(rotPitch + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -90, 90);

        // GCD Фикс
        float f = (float) (mc.options.mouseSensitivity * 0.6F + 0.2F);
        float gcd = f * f * f * 1.2F;
        
        rotYaw -= (rotYaw - (rotYaw - yawDelta)) % gcd;
        rotPitch -= (rotPitch - (rotPitch - pitchDelta)) % gcd;
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
