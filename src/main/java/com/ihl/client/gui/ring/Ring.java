package com.ihl.client.gui.ring;

import com.ihl.client.Helper;
import com.ihl.client.gui.*;
import com.ihl.client.input.InputUtil;
import com.ihl.client.util.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;

import java.util.List;

public class Ring extends Component {

    public static double size = 256;
    public static double width = 96;
    //protected final int borderColor = 0xFF000000, centerColor = 0x80FFFFFF, white = 0xFFFFFFFF, gray = 0xFFCCCCCC;
    public static int borderColor = 0xFF000000, centerColor = 0x80222222, white = 0xFFEEEEEE, gray = 0xFFCDCDCD, guicolor = 0x9A9A9A; // TODO: 2020-06-05 Customizable + looks hot
    protected final double segPadding = 0;
    protected final double modulePadding = 0;
    protected final double optionPadding = 0;
    protected final double settingsPadding = 0.5;
    protected final double settingSliderWidth = 32;
    protected double x, y;
    protected int selected = -1;
    protected double selectedR = selected;
    protected boolean mouseOver, mouseOverSettings;
    protected boolean[] hasSettings;
    protected double[] settingSlider;
    protected ResourceLocation settingsIcon = new ResourceLocation("client/icons/settings.png");

    protected double[] alpha = new double[]{0, 0};
    protected List<?> visibleList;
    protected List<?> list;

    protected double sizeR;

    public Ring(List<?> list) {
        this.visibleList = list;
        this.list = list;
        x = Display.getWidth() / 2F;
        y = Display.getHeight() / 2F;
        sizeR = size + width;
        settingSlider = new double[list.size()];
        hasSettings = new boolean[list.size()];
    }

    public Ring reset() {
        x = Display.getWidth() / 2F;
        y = Display.getHeight() / 2F;
        sizeR = size + width;
        settingSlider = new double[visibleList.size()];
        hasSettings = new boolean[visibleList.size()];
        alpha = new double[]{0, 0};
        settingsIcon = new ResourceLocation("client/icons/settings.png");
        selected = -1;
        return this;
    }

    @Override
    public void tick() {
        size = Math.min(Display.getHeight() / 3, 256);
        width = size / (256 / 96d);
        double dist = MathUtil.distTo(x, y, InputUtil.mouse[0], InputUtil.mouse[1]);
        if (dist > size - width) {
            double mang = MathUtil.dirTo(x, y, InputUtil.mouse[0], InputUtil.mouse[1]) + 180;
            if (!visibleList.isEmpty())
                selected = (int) Math.floor((visibleList.size() / 360D) * mang) % visibleList.size();
            else
                selected = -1;
            mouseOver = true;
            if (alpha[0] == 0) {
                selectedR = selected;
            }
            mouseOverSettings = dist > size;
        } else {
            mouseOver = false;
            mouseOverSettings = false;
        }

        double a = 2;
        double b = 2;
        double c = 2;
        double d = 0.4;

        for (int i = 0; i < settingSlider.length; i++) {
            settingSlider[i] += ((mouseOverSettings && selected == i && hasSettings[i] ? 1 : 0) - settingSlider[i]) / a;
        }

        selectedR += (((((selected - selectedR) % visibleList.size()) + (visibleList.size() * 1.5D)) % visibleList.size()) - (visibleList.size() * 0.5D)) / b;
        sizeR += (size - sizeR) / c;
        alpha[0] += (mouseOver ? d : -d);
        alpha[1] += ((Helper.mc().currentScreen instanceof GuiHandle ? 1 : 0) - alpha[1]) / b;

        for (int i = 0; i < alpha.length; i++) {
            alpha[i] = Math.min(Math.max(0, alpha[i]), 1);
        }
    }

    @Override
    public void keyPress(int k, char c) {
    }

    @Override
    public void keyRelease(int k, char c) {
    }

    @Override
    public void mouseClicked(int button) {
    }

    @Override
    public void mouseReleased(int button) {
    }

    @Override
    public void render() {
        RenderUtil2D.circleOutline(x, y, sizeR + 1, ColorUtil.transparency(borderColor, alpha[1]), 2f);
        RenderUtil2D.circleOutline(x, y, sizeR - width - 1, ColorUtil.transparency(borderColor, alpha[1]), 2f);

        for (int i = 0; i < visibleList.size(); i++) {
            RenderUtil2D.donutSeg(x, y, sizeR + (settingSlider[i] * settingSliderWidth) + 2, sizeR, i, visibleList.size(), settingsPadding - ((1 / (360D / visibleList.size())) * 5.5), ColorUtil.transparency(borderColor, alpha[1]));
        }

        RenderUtil2D.donut(x, y, sizeR, sizeR - width, ColorUtil.transparency(centerColor, alpha[1] / 2));
        RenderUtil2D.donutSeg(x, y, sizeR, sizeR - width, selectedR, visibleList.size(), segPadding, ColorUtil.transparency(guicolor, alpha[0] * alpha[1]));
        RenderUtil2D.circle(x, y, sizeR - width, ColorUtil.transparency(borderColor, alpha[1] / 2));

        double iconSize = 20;
        for (int i = 0; i < visibleList.size(); i++) {
            RenderUtil2D.donutSeg(x, y, sizeR + (settingSlider[i] * settingSliderWidth), sizeR, i, visibleList.size(), settingsPadding - ((1 / (360D / visibleList.size())) * 5), ColorUtil.transparency(guicolor, alpha[1]));

            double ang = ((360D / visibleList.size()) * (i + 0.5));
            double rad = (sizeR + (settingSlider[i] * (settingSliderWidth / 2)));
            double iX = x + Math.cos(ang * Math.PI / 180D) * rad;
            double iY = y + Math.sin(ang * Math.PI / 180D) * rad;

            Helper.mc().getTextureManager().bindTexture(settingsIcon);
            RenderUtil2D.texturedRect(iX - (iconSize / 2), iY - (iconSize / 2), iX + (iconSize / 2), iY + (iconSize / 2), ColorUtil.transparency(white, settingSlider[i] * alpha[1]));
        }
    }
}
