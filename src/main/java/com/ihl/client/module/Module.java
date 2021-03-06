package com.ihl.client.module;

import com.ihl.client.Helper;
import com.ihl.client.commands.*;
import com.ihl.client.event.*;
import com.ihl.client.module.hacks.combat.*;
import com.ihl.client.module.hacks.misc.*;
import com.ihl.client.module.hacks.movement.*;
import com.ihl.client.module.hacks.player.*;
import com.ihl.client.module.hacks.render.*;
import com.ihl.client.module.hacks.world.AntiCactus;
import com.ihl.client.module.option.*;
import com.ihl.client.module.option.options.OptNoS;
import com.ihl.client.util.*;
import joptsimple.internal.Strings;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class Module extends Helper {

    public static Map<String, Module> modules = new LinkedHashMap<>();
    protected static int currentId = 1;
    protected static RUtils rUtils;
    public int id;
    public String name, desc;
    public Category category;
    public int color;
    public boolean active;
    //        name     option   Note: Name also refers to the icon.
    public Map<String, Option> options = new LHM(this);
    public ResourceLocation icon;
    private int weight;

    private Module() {
        this.name = "";
        this.desc = "";
        this.category = Category.MISC;
    }
    public Module(String name, String desc, Category category, String keybind) {
        this.id = currentId++;
        this.name = name;
        this.desc = desc;
        this.category = category;

        addOption(new Option("Keybind", "Module toggle keybind", new ValueString(keybind), Option.Type.KEYBIND));

        String base = name.toLowerCase().replaceAll(" ", "");
        modules.put(base, this);

        icon = new ResourceLocation("client/icons/module/" + base + ".png");
    }

    public static void init() {
        rUtils = new RUtils();
        //MOVEMENT
        new Longjump();
        //COMBAT
        new AimAssist();
        new AimBot();
        new AimTest();
        new AntiBot();
        new Aura();
        new AutoClicker();
        //MISC
        //PLAYER
        new Antivoid();
        new Nofall();
        //RENDER
        //WORLD
        new AntiCactus();
        new AutoPotion();
        new Blink();
        new CameraClip();
        new Chams();
        new Commands();
        new Console();
        new CPS();
        new Criticals();
        new Damage();
        new Debugger();
        new Distance();
        new ESP();
        new Example();
        new FastClimb();
        new FastUse();
        new Fly();
        new Freecam();
        new Friends();
        new Fullbright();
        new Glide();
        new GUI();
        new Jesus();
        new MenuWalk();
        new Nametags();
        new Noclip();
        new NoSlow();
        new NoView();
        new Plugins();
        new Phase();
        new PacketFucker();
        new Sneak();
        new Speed();
        new SkinDerp();
        new Sprint();
        new Step();
        new StorageESP();
        new Swing();
        new Teleport();
        new Tracer();
        new TriggerBot();
        new VClip();
        new Velocity();
    }

    public static Module get(String key) {
        Module module = modules.get(key);
        if (module == null)
            module = new Module();
        return module;
    }

    public static List<String> enabled() {
        List<String> list = new ArrayList<>();
        for (String s : modules.keySet()) {
            Module module = get(s);
            if (module.active) {
                list.add(s);
            }
        }
        return list;
    }

    public static List<String> category(Category category) {
        List<String> list = new ArrayList<>();
        for (String s : modules.keySet()) {
            Module module = get(s);
            if (module.category == category) {
                list.add(s);
            }
        }
        return list;
    }

    public static void tickMods() {
        for (String key : modules.keySet()) {
            Module module = modules.get(key);
            module.tick();
        }
    }
    //protected Class[] events = new Class[]{};

    public static void event(Event event, boolean reverse) {
        if (Helper.player() == null) {
            return;
        }
        if (event instanceof EventPacket)
            rUtils.onSentPacket((EventPacket) event);
        else if (event instanceof EventPlayerUpdate)
            rUtils.onUpdate();
        List<String> enabled = enabled();
        for (int i = reverse ? enabled.size() - 1 : 0; reverse ? i >= 0 : i < enabled.size(); i += reverse ? -1 : 1) {
            Module module = get(enabled.get(i));
            Class<? extends Module> clazz = module.getClass();
            if (clazz.isAnnotationPresent(EventHandler.class)) {
                EventHandler handler = clazz.getAnnotation(EventHandler.class);
                for (Class<? extends Event> type : handler.events()) {
                    if (type == event.getClass()) {
                        module.onEvent(event);
                        break;
                    }
                }
            }
        }
    }

    public static void optionChange(EventOption eventOption) {
        event(eventOption, false);
        for (String key : modules.keySet()) {
            Module module = modules.get(key);
            if (eventOption.module != null && eventOption.module.name.equals(module.name))
                module.optionChanged(eventOption);
        }
    }

    protected void initCommands(String base) {
        List<String> usages = new ArrayList<>();
        usages.add(base);
        for (String key : options.keySet()) {
            Option option = Option.get(options, key);
            initCommands0(usages, option, "");
            usages.add((name.replaceAll(" ", "") + " " + option.name.replaceAll(" ", "") + " " + (option.type == Option.Type.CHOICE ? String.format(option.type.usage, Strings.join(((ValueChoice) option.getTValue()).list, "|")) : option.type.usage)).toLowerCase());
        }

        Command.commands.put(base, new CommandModule(base, usages));
    }

    private void initCommands0(List<String> usages, Option option, String names) {
        for (String key2 : option.options.keySet()) {
            Option option2 = option.getOption(key2);
            initCommands0(usages, option2, option.name.replaceAll(" ", "") + " ");
            usages.add((name.replaceAll(" ", "") + " " + names
              + option.name.replaceAll(" ", "") + " " + option2.name.replaceAll(" ", "") + " " + (option2.type == Option.Type.CHOICE ? String.format(option2.type.usage, Strings.join(((ValueChoice) option2.getTValue()).list, "|")) : option2.type.usage)).toLowerCase());
        }
    }

    public void enable() {
        color = ColorUtil.rainbow((long) (Math.random() * 10000000000D), 1f).getRGB();
        active = true;
    }

    public void disable() {
        active = false;
    }

    public void toggle() {
        if (active) {
            disable();
        } else {
            enable();
        }
    }

    public String getDisplay() {
        return name;
    }

    protected void tick() {
        for (String key : options.keySet()) {
            Option option = Option.get(options, key);
            limitOption(option, true);
            if (!option.options.isEmpty()) {
                for (String subKey : option.options.keySet()) {
                    Option subOption = Option.get(options, key, subKey);
                    limitOption(subOption, true);
                }
            }
        }
    }

    protected void onEvent(Event event) {
    }

    private void limitOption(Option option, boolean... disable) {
        if (option.type == Option.Type.NUMBER) {
            if (option.getTValue() instanceof ValueDouble) {
                ValueDouble value = (ValueDouble) option.getTValue();
                double val = Math.min(Math.max(value.limit[0], (double) value.getValue()), value.limit[1]);
                val = MathUtil.roundInc(val, value.step);
                if (disable.length > 0 && disable[0])
                    option.setValueNoTrigger(val);
                else
                    option.setValue(val);
            } else if (option.getTValue() instanceof ValueRange) {
                ValueRange value = (ValueRange) option.getTValue();
                double[] v = (double[]) value.getValue();
                double max = Math.min(Math.max(value.limit[0], v[1]), value.limit[1]);
                double min = Math.min(Math.max(value.limit[0], v[0]), value.limit[1]);
                min = MathUtil.roundInc(min, value.step);
                if (disable.length > 0 && disable[0])
                    option.setValueNoTrigger(min);
                else
                    option.setValue(min);
                max = MathUtil.roundInc(max, value.step);
                if (disable.length > 0 && disable[0])
                    option.setValueNoTrigger(max);
                else
                    option.setValue(max);
            }
        }
    }

    public void optionChanged(EventOption eventOption) {
    }

    public void rendeRing(EventRing ring) {
    }

    // Option Setters
    // name, description, defaultValue
    // Returns created option.
    public Option addBoolean(String name, String description, boolean defaultValue) {
        Option opt = new Option(name, description, new ValueBoolean(defaultValue), Option.Type.BOOLEAN);
        opt.module = this;
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.weight = weight++;
        return opt;
    }

    public Option addInteger(String name, String description, int defaultValue, int min, int max) {
        return addDouble(name, description, defaultValue, min, max, 1);
    }

    public Option addDouble(String name, String description, double defaultValue, double min, double max, double increments) {
        Option opt = new Option(name, description, new ValueDouble(defaultValue, new double[]{min, max}, increments), Option.Type.NUMBER);
        opt.module = this;
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.weight = weight++;
        return opt;
    }

    public Option addString(String name, String description, String defaultValue) {
        Option opt = new Option(name, description, new ValueString(defaultValue), Option.Type.STRING);
        opt.module = this;
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.weight = weight++;
        return opt;
    }

    public Option addChoice(String name, String description, String... values) {
        Option opt = new Option(name, description, new ValueChoice(0, values), Option.Type.CHOICE);
        opt.module = this;
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.weight = weight++;
        return opt;
    }

    public Option addOther(String name, String description) {
        Option opt = new OptNoS(this, name, description, new ValueString(""), Option.Type.OTHER);
        opt.module = this;
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.weight = weight++;
        return opt;
    }

    public OptNoS addBooleanNoS(String name, String description, boolean defaultValue) {
        OptNoS opt = new OptNoS(this, name, description, new ValueBoolean(defaultValue), OptNoS.Type.BOOLEAN);
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.module = this;
        opt.weight = weight++;
        return opt;
    }

    public OptNoS addIntegerNoS(String name, String description, int defaultValue, int min, int max) {
        return addDoubleNoS(name, description, defaultValue, min, max, 1);
    }

    public OptNoS addDoubleNoS(String name, String description, double defaultValue, double min, double max, double increments) {
        OptNoS opt = new OptNoS(this, name, description, new ValueDouble(defaultValue, new double[]{min, max}, increments), OptNoS.Type.NUMBER);
        opt.module = this;
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.weight = weight++;
        return opt;
    }

    public OptNoS addStringNoS(String name, String description, String defaultValue) {
        OptNoS opt = new OptNoS(this, name, description, new ValueString(defaultValue), OptNoS.Type.STRING);
        opt.module = this;
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.weight = weight++;
        return opt;
    }

    public OptNoS addChoiceNoS(String name, String description, String... values) {
        OptNoS opt = new OptNoS(this, name, description, new ValueChoice(0, values), OptNoS.Type.CHOICE);
        opt.module = this;
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.weight = weight++;
        return opt;
    }

    public OptNoS addOtherNoS(String name, String description) {
        OptNoS opt = new OptNoS(this, name, description, new ValueString(""), OptNoS.Type.OTHER);
        opt.module = this;
        options.put(name.toLowerCase().replaceAll(" ", ""), opt);
        opt.weight = weight++;
        return opt;
    }

    public void addOptions(Option... options) {
        for (Option option : options) {
            addOption(option);
        }
    }

    public Option addOption(Option option) {
        option.module = this;
        option.parent = null;
        options.put(option.name.toLowerCase().replaceAll(" ", ""), option);
        resetOptionMap();
        option.weight = weight++;
        return option;
    }

    public Option addOptionIfAbsent(Option option) {
        option.module = this;
        option.parent = null;
        options.putIfAbsent(option.name.toLowerCase().replaceAll(" ", ""), option);
        resetOptionMap();
        option.weight = weight++;
        return option;
    }

    public void resetOptionMap() {
        Map<String, Option> optionMap = new HashMap<>();
        for (Map.Entry<String, Option> entry : options.entrySet()) {
            optionMap.put(entry.getValue().name.toLowerCase().replaceAll(" ", ""), entry.getValue());
        }
        options = optionMap;
    }

    //Option Getters.
    // names
    public Object OBJECT(String... names) {
        for (int i = 0; i < names.length; i++)
            names[i] = names[i].toLowerCase().replaceAll(" ", "").trim();
        Option currentOpt = options.get(names[0]);
        if (currentOpt == null) return null;
        for (int i = 1; i < names.length; i++) {
            currentOpt = currentOpt.options.get(names[i]);
            if (currentOpt == null) return null;
        }
        return currentOpt.getValue();
    }

    public boolean BOOLEAN(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return false;
        return (boolean) OBJECT(names);
    }

    public int INTEGER(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return 0;
        return ((Double) OBJECT(names)).intValue();
    }

    public float FLOAT(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return 0;
        return ((Double) OBJECT(names)).floatValue();
    }

    public double DOUBLE(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return 0;
        return (double) OBJECT(names);
    }

    public String STRING(String... names) {
        Object obj = OBJECT(names);
        if (obj == null) return "";
        return (String) OBJECT(names);
    }
}
