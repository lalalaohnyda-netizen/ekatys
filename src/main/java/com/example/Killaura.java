package com.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.options.KeyBinding;
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
        if (!enabled || mc.player == null) return;

        // --- ЖЕЛЕЗНЫЙ INVENTORY WALK ---
        if (mc.currentScreen != null && !(mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen)) {
            // Опрашиваем состояние клавиш напрямую через окно (LWJGL)
            long window = mc.getWindow().getHandle();
            mc.options.keyForward.setPressed(net.minecraft.client.util.InputUtil.isKeyPressed(window, mc.options.keyForward.getDefaultKey().getCode()));
            mc.options.keyBack.setPressed(net.minecraft.client.util.InputUtil.isKeyPressed(window, mc.options.keyBack.getDefaultKey().getCode()));
            mc.options.keyLeft.setPressed(net.minecraft.client.util.InputUtil.isKeyPressed(window, mc.options.keyLeft.getDefaultKey().getCode()));
            mc.options.keyRight.setPressed(net.minecraft.client.util.InputUtil.isKeyPressed(window, mc.options.keyRight.getDefaultKey().getCode()));
            mc.options.keyJump.setPressed(net.minecraft.client.util.InputUtil.isKeyPressed(window, mc.options.keyJump.getDefaultKey().getCode()));
        }

        // --- АВТОСПРИНТ (Вместо зажима CTRL) ---
        if (mc.player.forwardSpeed > 0 && !mc.player.isSneaking() && !mc.player.horizontalCollision) {
            mc.player.setSprinting(true);
        }

        target = findTarget();

        if (target != null) {
            updateRotation();
            isRotating = true;
            
            // Чтобы не флагало: бьем только когда прицел УЖЕ наведен (погрешность < 5 градусов)
            float yawDiff = Math.abs(MathHelper.wrapDegrees(rotYaw - mc.player.yaw));
            
            if (mc.player.getAttackCooldownProgress(0.0f) >= 0.98f) {
                // Если ты в прыжке или падаешь — крит
                if (mc.player.fallDistance > 0 || mc.player.abilities.creativeMode) {
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
        animTicks += 0.8f;

        double diffX = target.getX() - mc.player.getX();
        double diffZ = target.getZ() - mc.player.getZ();
        // Точка наводки плавает от живота до груди
        double diffY = (target.getY() + target.getHeight() * (0.45 + Math.sin(animTicks * 0.2) * 0.1)) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        // Рандомизация траектории (чтобы не было "линейки")
        targetYaw += Math.sin(animTicks * 0.15) * 1.8f;
        targetPitch += Math.cos(animTicks * 0.1) * 1.5f;

        float yawDelta = MathHelper.wrapDegrees(targetYaw - rotYaw);
        float pitchDelta = MathHelper.wrapDegrees(targetPitch - rotPitch);

        // Динамическая скорость: чем ближе к цели, тем медленнее (имитация доводки рукой)
        float distFactor = Math.min(1.0f, (Math.abs(yawDelta) + Math.abs(pitchDelta)) / 30f);
        float currentSpeed = 12.0f + (distFactor * 10.0f); 

        rotYaw += yawDelta * (currentSpeed / 100f);
        rotPitch += pitchDelta * (currentSpeed / 100f);

        // GCD Фикс
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

