package com.example.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public class InventoryMoveMixin {
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private net.minecraft.client.gui.screen.Screen stopInventoryStop(MinecraftClient client) {
        // Если открыт чат — стопим движение. Если инвентарь — делаем вид, что ничего не открыто.
        if (client.currentScreen instanceof ChatScreen) return client.currentScreen;
        return null; 
    }
}
