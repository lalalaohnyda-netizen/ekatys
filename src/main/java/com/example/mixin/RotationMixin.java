@Inject(method = "sendMovementPackets", at = @At("HEAD"))
private void compensateBefore(CallbackInfo ci) {
    Killaura.onTick();

    if (Killaura.isRotating && Killaura.mc.player != null) {
        visualYaw = Killaura.mc.player.yaw;
        visualPitch = Killaura.mc.player.pitch;

        // Пакетные углы (плавающие)
        Killaura.mc.player.yaw = Killaura.rotYaw;
        Killaura.mc.player.pitch = Killaura.rotPitch;
        
        // Обновляем визуальную голову для сервера
        Killaura.mc.player.headYaw = Killaura.rotYaw;
        Killaura.mc.player.bodyYaw = Killaura.rotYaw;
    }
}
