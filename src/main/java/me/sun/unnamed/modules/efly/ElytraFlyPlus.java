package me.sun.unnamed.modules.efly;

import me.sun.unnamed.Addon;
import me.sun.unnamed.modules.efly.impl.ElytraFlyPlusBoost;
import me.sun.unnamed.modules.efly.impl.ElytraFlyPlusSimple;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class ElytraFlyPlus extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgSimple = this.settings.createGroup("Simple");

    public final Setting<ElytraFlyPlusModes> flightMode = sgGeneral.add(new EnumSetting.Builder<ElytraFlyPlusModes>()
        .name("mode")
        .description("The method of applying speed.")
        .defaultValue(ElytraFlyPlusModes.Simple)
        .onModuleActivated(flightMode -> onModeChanged(flightMode.get()))
        .onChanged(this::onModeChanged)
        .build()
    );

    private ElytraFlyPlusMode currentMode;

    private void onModeChanged(ElytraFlyPlusModes mode) {
        switch (mode) {
            case Boost -> currentMode = new ElytraFlyPlusBoost();
            case Simple -> currentMode = new ElytraFlyPlusSimple();
        }
    }

    public final Setting<Double> verticalAngle = sgSimple.add(new DoubleSetting.Builder()
        .name("Vertical Angle")
        .description("How much to affect aim when going vertically.")
        .defaultValue(20.0d)
        .min(10.0d)
        .sliderMax(30.0d)
        .visible(() -> flightMode.get() == ElytraFlyPlusModes.Simple)
        .build()
    );

    public final Setting<Double> verticalSpeed = sgSimple.add(new DoubleSetting.Builder()
        .name("Vertical Speed")
        .description("How much to affect aim when going vertically.")
        .defaultValue(0.015d)
        .range(0.005d, 0.05d)
        .visible(() -> flightMode.get() == ElytraFlyPlusModes.Simple)
        .build()
    );

    public final Setting<Double> horizontalAcceleration = sgSimple.add(new DoubleSetting.Builder()
        .name("Horizontal Acceleration")
        .description("The speed of acceleration.")
        .defaultValue(0.035d)
        .range(0.01d, 0.075d)
        .visible(() -> flightMode.get() == ElytraFlyPlusModes.Simple)
        .build()
    );

    public final Setting<Double> maxSpeedSimple = sgSimple.add(new DoubleSetting.Builder()
        .name("Max Speed")
        .description("Max BPS.")
        .defaultValue(48.0d)
        .min(10.0d)
        .sliderMax(100.0d)
        .visible(() -> flightMode.get() == ElytraFlyPlusModes.Simple)
        .build()
    );

    public ElytraFlyPlus() {
        super(Addon.CATEGORY, "elytra-fly-plus", "Better elytrafly");
    }

    double frozenMotionX, frozenMotionY, frozenMotionZ;
    boolean isFrozen = false;

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        currentMode.onPreTick(event);
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        currentMode.onSendPacket(event);
    }

    @Override
    public String getInfoString() {
        return currentMode.getHudString();
    }

}
