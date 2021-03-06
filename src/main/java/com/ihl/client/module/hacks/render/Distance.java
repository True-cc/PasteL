package com.ihl.client.module.hacks.render;

import com.ihl.client.Helper;
import com.ihl.client.event.EventHandler;
import com.ihl.client.module.*;
import com.ihl.client.module.option.*;
import org.lwjgl.input.Mouse;

@EventHandler(events = {})
public class Distance extends Module {

    public Distance() {
        super("Distance", "Change the third person camera distance", Category.RENDER, "NONE");
        options.put("distance", new Option("Distance", "Change the third person view distance", new ValueDouble(10, new double[]{4, 100}, 0.1), Option.Type.NUMBER));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    protected void tick() {
        super.tick();
        if (active && Helper.mc().currentScreen == null) {
            Option option = Option.get(options, "distance");
            option.setValue((double) (option.getValue()) - (Mouse.getDWheel() / 120d));
        }
        Helper.mc().entityRenderer.thirdPersonDistance += ((active ? Option.get(options, "distance").DOUBLE() : 4) - Helper.mc().entityRenderer.thirdPersonDistance) / 4;
    }
}
