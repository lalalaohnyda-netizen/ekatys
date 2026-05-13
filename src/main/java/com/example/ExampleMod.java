package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class ExampleMod implements ModInitializer {

    private boolean rPressed = false;

    @Override
    public void onInitialize() {
        // Регистрация события тика клиента
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Проверка нажатия клавиши R через GLFW
            long window = client.getWindow().getHandle();
            boolean isKeyDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;

            // Логика переключения (Toggle), чтобы не мигало 20 раз в секунду
            if (isKeyDown && !rPressed) {
                Killaura.enabled = !Killaura.enabled;
                rPressed = true;
                
                // Вывод сообщения в чат о состоянии (как в читах)
                String status = Killaura.enabled ? "§aEnabled" : "§cDisabled";
                client.player.sendMessage(new LiteralText("§7[§6Killaura§7] " + status), true);
            } else if (!isKeyDown) {
                rPressed = false;
            }

            // Вызов самой логики ауры каждый тик
            if (Killaura.enabled) {
                Killaura.onTick();
            }
        });
    }
}
