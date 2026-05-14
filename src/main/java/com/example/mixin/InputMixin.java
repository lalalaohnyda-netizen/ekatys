package com.example.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.gui.screen.Screen;

@Mixin(ClientPlayerEntity.class)
public class InputMixin {
    
    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private Screen bypassInventory(MinecraftClient client) {
        // Если открыт экран и это НЕ чат, возвращаем null (игра думает, что окон нет)
        if (client.currentScreen != null && !(client.currentScreen instanceof ChatScreen)) {
            return null;
        }
        return client.currentScreen;
    }
}
