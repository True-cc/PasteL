package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.option.*;

import java.util.*;

@EventHandler(events = {EventPlayerAttack.class})
public class AntiBot extends Module {
    public static boolean ground;
    public static boolean air;
    public static boolean tab;
    public static boolean tabEquals;
    public static boolean dupTab;
    public static boolean dupWorld;
    public static boolean color;
    public static boolean hit;
    public static Set<Integer> hitted = new HashSet<>();
    private static AntiBot instance;

    public AntiBot() {
        super("AntiBot", "Ignores bots", Category.COMBAT, "NONE");
        options.put("ground", new Option("Ground", "Ignores grounded", new ValueBoolean(false), Option.Type.BOOLEAN));
        options.put("air", new Option("Air", "Ignores aired", new ValueBoolean(false), Option.Type.BOOLEAN));
        options.put("tab", new Option("Tab", "Only allows in tab", new ValueBoolean(false), Option.Type.BOOLEAN,
          new Option("TabEquals", "Checks if its equals instead of contains.", new ValueBoolean(false), Option.Type.BOOLEAN)));
        options.put("duptab", new Option("Duplicate Tab", "Ignores duplicates in tab.", new ValueBoolean(false), Option.Type.BOOLEAN));
        options.put("dupworld", new Option("Duplicate World", "Ignores duplicates in world.", new ValueBoolean(false), Option.Type.BOOLEAN));
        options.put("color", new Option("Color", "Ignores names with color.", new ValueBoolean(false), Option.Type.BOOLEAN));
        options.put("hit", new Option("Hit", "Only targets hit entities.", new ValueBoolean(false), Option.Type.BOOLEAN));
        initCommands(name.toLowerCase().replaceAll(" ", ""));
        instance = this;
        update();
    }

    public static void update() {
        ground = instance.options.get("ground").BOOLEAN();
        air = instance.options.get("air").BOOLEAN();
        tab = instance.options.get("tab").BOOLEAN();
        tabEquals = instance.options.get("tab").options.get("tabequals").BOOLEAN();
        dupTab = instance.options.get("duptab").BOOLEAN();
        dupWorld = instance.options.get("dupworld").BOOLEAN();
    }

    public void enable() {
        super.enable();
        update();
        disable();
    }

    protected void onEvent(Event event) {
        if (event instanceof EventPlayerAttack) {
            hitted.add(((EventPlayerAttack) event).target.getEntityId());
        }
    }
}
