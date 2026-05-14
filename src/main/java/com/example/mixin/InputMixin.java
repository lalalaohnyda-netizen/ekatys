package com.example.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class InputMixin {
    // Этот редирект говорит игре, что экрана НЕТ, если это не чат.
    // Это открывает возможность ходить в любом GUI (инвентарь, сундуки).
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private net.minecraft.client.gui.screen.Screen bypassInventory(MinecraftClient client) {
        if (client.currentScreen instanceof ChatScreen) {
            return client.currentScreen;
        }
        return null; 
    }
}
