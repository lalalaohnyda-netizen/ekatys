package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;

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

        // --- ЖЕЛЕЗНЫЙ INVENTORY WALK & AUTO-SPRINT ---
        boolean isInventory = mc.currentScreen != null && !(mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen);
        if (isInventory) {
            long handle = mc.getWindow().getHandle();
            mc.options.keyForward.setPressed(InputUtil.isKeyPressed(handle, mc.options.keyForward.getDefaultKey().getCode()));
            mc.options.keyBack.setPressed(InputUtil.isKeyPressed(handle, mc.options.keyBack.getDefaultKey().getCode()));
            mc.options.keyLeft.setPressed(InputUtil.isKeyPressed(handle, mc.options.keyLeft.getDefaultKey().getCode()));
            mc.options.keyRight.setPressed(InputUtil.isKeyPressed(handle, mc.options.keyRight.getDefaultKey().getCode()));
            mc.options.keyJump.setPressed(InputUtil.isKeyPressed(handle, mc.options.keyJump.getDefaultKey().getCode()));
        }

        if (mc.player.forwardSpeed > 0 && !mc.player.isSneaking()) {
            mc.player.setSprinting(true);
        }

        target = findTarget();

        if (target != null) {
            updateRotation();
            isRotating = true;
            
            // Удар: 0.92f для небольшого запаса по пингу
            if (mc.player.getAttackCooldownProgress(0.0f) >= 0.92f) {
                if (mc.player.fallDistance > 0.05f || mc.player.abilities.creativeMode) {
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
        animTicks += 0.4f;

        double diffX = target.getX() - mc.player.getX();
        double diffZ = target.getZ() - mc.player.getZ();
        // Рандомная высота (от пояса до груди)
        double diffY = (target.getY() + target.getHeight() * (0.4 + Math.sin(animTicks * 0.5) * 0.15)) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        // Синусоидная рандомизация (Jitter)
        targetYaw += (float) Math.sin(animTicks) * 1.5f;
        targetPitch += (float) Math.cos(animTicks) * 1.0f;

        float yawDelta = MathHelper.wrapDegrees(targetYaw - rotYaw);
        float pitchDelta = MathHelper.wrapDegrees(targetPitch - rotPitch);

        // Плавность (Exponential Smoothing)
        rotYaw += yawDelta * 0.25f;
        rotPitch += pitchDelta * 0.25f;

        // GCD FIX
        float f = (float) (mc.options.mouseSensitivity * 0.6F + 0.2F);
        float gcd = f * f * f * 1.2F;
        rotYaw -= (rotYaw - mc.player.yaw) % gcd;
        rotPitch -= (rotPitch - mc.player.pitch) % gcd;
    }

    private static LivingEntity findTarget() {
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof PlayerEntity && e != mc.player && e.isAlive() && mc.player.distanceTo(e) < 3.8) {
                return (LivingEntity) e;
            }
        }
        return null;
    }
}
