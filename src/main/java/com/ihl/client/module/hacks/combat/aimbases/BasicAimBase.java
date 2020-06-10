package com.ihl.client.module.hacks.combat.aimbases;

import com.ihl.client.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;

public class BasicAimBase {
    private static final Minecraft mc;
    static {
        mc = Minecraft.getMinecraft();
    }
    public static float[] getAimTo(int limit, String aimWhere, double custom) {
        EntityPlayerSP p = mc.thePlayer;
        return getAimTo(limit, aimWhere, custom, new float[]{p.rotationYaw, p.rotationPitch});
    }
    public static float[] getAimTo(int limit, String aimWhere, double custom, float[] from) {
        EntityLivingBase target = TargetUtil.target;
        if (target == null)
            return new float[2];
        float[] to;
        switch (aimWhere) {
            case "top":
                to = RUtils.getNeededRotations(RUtils.getTop(target.getEntityBoundingBox()), true);
                break;
            case "head":
                to = RUtils.getNeededRotations(RUtils.getHead(target), true);
                break;
            case "center":
                to = RUtils.getNeededRotations(RUtils.getCenter(target.getEntityBoundingBox()), true);
                break;
            case "feet":
                to = RUtils.getNeededRotations(RUtils.getBottom(target.getEntityBoundingBox()), true);
                break;
            case "fromTop":
                to = RUtils.getNeededRotations(RUtils.getFromTop(target.getEntityBoundingBox(), custom), true);
                break;
            case "auto":
                to = RUtils.getNeededRotations(RUtils.searchCenter(target.getEntityBoundingBox(), false, false, true, false).getVec(), true);
                break;
            default:
                to = RUtils.getNeededRotations(RUtils.getFromBottom(target.getEntityBoundingBox(), custom), true);
        }
        float[] r = RUtils.limitAngleChange(from, to, limit, limit);
        return new float[]{RUtils.angleDifference(from[0], r[0]), RUtils.angleDifference(from[1], r[1])};
    }
}