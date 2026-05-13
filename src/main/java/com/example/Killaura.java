package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
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

    // Параметры для "плавания" прицела
    private static float animTicks = 0;

    public static void onTick() {
        if (!enabled || mc.player == null || mc.world == null) {
            isRotating = false;
            return;
        }

        // --- INVENTORY WALK (Ходьба в инвентаре) ---
        if (mc.currentScreen instanceof InventoryScreen) {
            // Разрешаем управление кнопками движения, пока открыт инвентарь
            mc.options.keyForward.setPressed(mc.options.keyForward.isPressed());
            mc.options.keyBack.setPressed(mc.options.keyBack.isPressed());
            mc.options.keyLeft.setPressed(mc.options.keyLeft.isPressed());
            mc.options.keyRight.setPressed(mc.options.keyRight.isPressed());
            mc.options.keyJump.setPressed(mc.options.keyJump.isPressed());
            mc.options.keySprint.setPressed(true); // Автобег в инвентаре
        }

        // --- AUTO SPRINT ---
        if (mc.player.forwardSpeed > 0 && !mc.player.isSneaking()) {
            mc.player.setSprinting(true);
        }

        target = findTarget();

        if (target != null) {
            updateRotation();
            isRotating = true;
            
            // Удар (Криты)
            if (mc.player.getAttackCooldownProgress(0.5f) >= 0.92f) {
                if (mc.player.fallDistance > 0.05f || mc.player.abilities.creativeMode) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        } else {
            isRotating = false;
            animTicks = 0;
            rotYaw = mc.player.yaw;
            rotPitch = mc.player.pitch;
        }
    }

    private static void updateRotation() {
        animTicks += 0.5f; // Скорость "плавания"

        // Базовые углы на цель
        double diffX = target.getX() - mc.player.getX();
        double diffZ = target.getZ() - mc.player.getZ();
        // Наводимся в район груди
        double diffY = (target.getY() + target.getHeight() * 0.55) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        // --- ЛОГИКА ПЛАВАНИЯ (Bypass) ---
        // Создаем восьмерку или эллипс внутри хитбокса с помощью sin и cos
        float swimYaw = (float) Math.sin(animTicks * 0.4) * 1.5f; 
        float swimPitch = (float) Math.cos(animTicks * 0.3) * 1.2f;

        targetYaw += swimYaw;
        targetPitch += swimPitch;

        float yawDelta = MathHelper.wrapDegrees(targetYaw - rotYaw);
        float pitchDelta = MathHelper.wrapDegrees(targetPitch - rotPitch);

        // Плавная доводка
        float speed = 16.0f + (float)Math.sin(animTicks) * 2.0f; 
        float clampedYaw = Math.min(Math.max(Math.abs(yawDelta), 1.0F), speed);
        float clampedPitch = Math.min(Math.max(Math.abs(pitchDelta), 1.0F), speed);

        rotYaw += (yawDelta > 0 ? clampedYaw : -clampedYaw);
        rotPitch = MathHelper.clamp(rotPitch + (pitchDelta > 0 ? clampedPitch : -clampedPitch), -90, 90);

        // GCD Fix (чтобы сервер думал, что работает мышка)
        float f = (float) (mc.options.mouseSensitivity * 0.6F + 0.2F);
        float gcd = f * f * f * 1.2F;
        rotYaw -= (rotYaw - (rotYaw - yawDelta)) % gcd;
        rotPitch -= (rotPitch - (rotPitch - pitchDelta)) % gcd;
    }

    private static LivingEntity findTarget() {
        for (Entity e : mc.world.getEntities()) {
            if (e instanceof PlayerEntity && e != mc.player && e.isAlive() && mc.player.distanceTo(e) < 4.1) {
                return (LivingEntity) e;
            }
        }
        return null;
    }
}
