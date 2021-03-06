package com.ihl.client.gui.ring;

import com.ihl.client.Helper;
import com.ihl.client.event.EventRing;
import com.ihl.client.gui.Gui;
import com.ihl.client.module.Module;
import com.ihl.client.module.option.*;
import com.ihl.client.util.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class RingSubOption extends Ring {

    private final Module module;
    private final Option option;

    public RingSubOption(List<Option> list, Module module, Option option) {
        super(list);
        this.module = module;
        this.option = option;

        for (int i = 0; i < list.size(); i++) {
            Option subOption = list.get(i);
            hasSettings[i] = subOption.options != null && !subOption.options.isEmpty();
        }
    }

    @Override
    public void tick() {
        if (visibleList.isEmpty()) {
            List<Option> list = Arrays.asList(module.options.values().toArray(new Option[0]));
            list.sort(Comparator.comparingInt(o -> o.weight));
            if (!list.isEmpty()) {
                Gui.changeRing(new RingOption(list, module));
                return;
            }
        }
        super.tick();
    }

    @Override
    public void mouseClicked(int button) {
        if (mouseOver && button == 0) {
            Option subOption = (Option) visibleList.get(selected);
            //System.out.println(subOption.name + "  " + mouseOverSettings + "  " + hasSettings[selected]);
            if (mouseOverSettings) {
                if (subOption.options != null && !subOption.options.isEmpty()) {
                    List<Option> list = Arrays.asList(subOption.options.values().toArray(new Option[0]));
                    list.sort(Comparator.comparingInt(o -> o.weight));
                    if (!list.isEmpty()) {
                        Gui.changeRing(new RingSubOption(list, module, subOption));
                    }
                }
            } else {
                switch (subOption.type) {
                    case BOOLEAN:
                        subOption.setValue(!subOption.BOOLEAN());
                        break;
                    case CHOICE:
                        Gui.changeRing(new RingChoice(module, subOption, Arrays.asList(((ValueChoice) subOption.getTValue()).list)));
                        break;
                    case KEYBIND:
                        Gui.changeRing(new RingKeybind(module, subOption, Arrays.asList(subOption.name)));
                        break;
                    case LIST:
                        Gui.changeRing(new RingList(module, subOption, (List<String>) (subOption.getValue())));
                        break;
                    case RANGE:
                    case NUMBER:
                        Gui.changeRing(new RingSlider(module, subOption, Arrays.asList(subOption.name)));
                        break;
                    case OTHER:
                        List<Option> list = Arrays.asList(subOption.options.values().toArray(new Option[0]));
                        list.sort(Comparator.comparingInt(o -> o.weight));
                        if (!list.isEmpty()) {
                            Gui.changeRing(new RingSubOption(list, module, subOption));
                        }
                        break;
                    case STRING:
                        Gui.changeRing(new RingString(module, subOption, Arrays.asList(option.name)));
                        break;
                }
            }
        } else {
            Gui.prevRing();
            /*List<String> list = Arrays.asList(module.options.keySet().toArray(new String[0]));
            if (!list.isEmpty()) {
                Gui.changeRing(new RingOption(list, module));
            }*/
        }
    }

    @Override
    public Ring reset() {
        module.rendeRing(new EventRing(this, option));
        this.visibleList = new ArrayList<>(this.list);
        for (Object obj : this.list) {
            if (obj != null && !((Option) obj).visible())
                this.visibleList.remove(obj);
        }
        return super.reset();
    }

    @Override
    public void render() {
        super.render();
        double iconSize = 40;

        if (visibleList.isEmpty())
            Gui.prevRing();

        for (int i = 0; i < visibleList.size(); i++) {
            Option subOption = (Option) visibleList.get(i);

            if (subOption == null) {
                Gui.prevRing();
                return;
            }

            double iX = x + Math.cos(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            double iY = y + Math.sin(((360f / (visibleList.size() * 2)) * ((i + 0.5) * 2)) * Math.PI / 180) * (sizeR - (width / 2));
            iY -= RenderUtil.fontTiny[0].getHeight() / 2;

            switch (subOption.type) {
                case BOOLEAN:
                    if (subOption.BOOLEAN()) {
                        RenderUtil2D.donutSeg(x, y, sizeR + (settingSlider[i] * settingSliderWidth) + 20, sizeR + (settingSlider[i] * settingSliderWidth) + 10, i, visibleList.size(), optionPadding, ColorUtil.transparency(subOption.color, alpha[1]));
                    }
                    break;
                case CHOICE:
                    break;
                case KEYBIND:
                    break;
                case LIST:
                    break;
                case NUMBER:
                    ValueDouble val = (ValueDouble) subOption.getTValue();
                    double[] limits = val.limit.clone();
                    double valNeg = 0;
                    if (limits[0] < 0) {
                        valNeg = -limits[0];
                        limits[1] += -limits[0];
                        limits[0] = 0;
                    }
                    double frac = (1 / limits[1]) * (((double) val.getValue() + valNeg) - limits[0]);
                    RenderUtil2D.donutSegFrac(x, y, sizeR + (settingSlider[i] * settingSliderWidth) + 20, sizeR + (settingSlider[i] * settingSliderWidth) + 10, i, visibleList.size(), frac, optionPadding, ColorUtil.transparency(subOption.color, alpha[1]));
                    break;
                case OTHER:
                    break;
                case STRING:
                    break;
            }

            Helper.mc().getTextureManager().bindTexture(subOption.icon);
            RenderUtil2D.texturedRect(iX - (iconSize / 2), iY - (iconSize / 2), iX + (iconSize / 2), iY + (iconSize / 2), ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]));
            RenderUtil2D.string(RenderUtil.fontTiny[1], subOption.name, iX, iY + (iconSize / 2), ColorUtil.transparency(selected == i && mouseOver ? white : guicolor, alpha[1]), 0, 0, false);
        }

        if (selected != -1) {
            Option subOption = (Option) visibleList.get((int) Math.floor(selected));
            RenderUtil2D.string(RenderUtil.fontLarge[1], subOption.name, x, y, ColorUtil.transparency(white, alpha[0] * alpha[1]), 0, 0, false);
            RenderUtil2D.string(RenderUtil.fontTiny[1], subOption.desc, x, y + (RenderUtil.fontLarge[1].getHeight() / 2) + 2, ColorUtil.transparency(gray, alpha[0] * alpha[1]), 0, 1, false);
            RenderUtil2D.string(RenderUtil.fontSmall[1], StringUtils.capitalize(subOption.STRING()), x, y + (RenderUtil.fontLarge[1].getHeight() / 2) + RenderUtil.fontTiny[1].getHeight() + 2, ColorUtil.transparency(white, alpha[0] * alpha[1]), 0, 1, false);
        }
    }
}
