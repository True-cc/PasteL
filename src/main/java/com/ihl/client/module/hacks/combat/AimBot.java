package com.ihl.client.module.hacks.combat;

import com.ihl.client.event.*;
import com.ihl.client.module.*;
import com.ihl.client.module.hacks.combat.aimbases.*;
import com.ihl.client.module.option.Option;
import com.ihl.client.util.TargetUtil;
import net.minecraft.util.MovingObjectPosition;

@EventHandler(events = {EventMouseMove.class, EventRender.class})
public class AimBot extends Module {

    public AimBot() {
        super("AimBot", "Aims at stuff", Category.COMBAT, "NONE");
        addChoice("Aim When", "Aim when", "always", "mouse");
        Option aW = addChoice("Aim Where", "Aim where", "top", "head", "centre", "feet", "fromTop", "fromBottom", "auto");
        aW.addDouble("Custom", "Value from  top for custom.", 0.4, 0, 3, 0.1);
        addChoice("Mouse Mode", "How it overrides your mouse", "add", "complete");
        addChoice("Priority", "Switch target selection mode", "distance", "health", "direction");
        addDouble("Distance", "Distance to attack entities within", 3.6, 0, 10, 0.1);
        addDouble("Range", "View range to attack entities within", 180, 0, 180, 1);
        addBoolean("Invert yaw", "Enable or Disable if turning the wrong way.", true);
        addBoolean("Invert pitch", "Enable or Disable if turning the wrong way.", false);
        addDouble("Predict", "Amount to predict. 0 is none, 1 is motion, 2 double that, and so on.", 1, 0, 10, 0.1);
        addDouble("Yaw Speed", "Speed to aim towards the target", 30, 0, 300, 1);
        addDouble("Yaw Speed Random", "Speed alters", 5, 0, 180, 1);
        addDouble("Yaw Speed Increase", "The increase speed over time", 5, 0, 180, 1);
        addDouble("Pitch Speed", "Speed to aim towards the target", 30, 0, 300, 1);
        addDouble("Pitch Speed Random", "Speed alters", 5, 0, 180, 1);
        addDouble("Pitch Speed Increase", "The increase speed over time", 5, 0, 180, 1);
        addInteger("Maximum overshoot", "Maximum turn the aimbot is allowed to overshoot", 10, 0, 180);
        initCommands(name.toLowerCase().replaceAll(" ", ""));
    }

    public void disable() {
        super.disable();
        mc().mouseHelper.overrideMode = 0;
    }

    public void enable() {
        super.enable();
        /*if (!HelperUtil.inGame()) {
            return;
        }*/
    }

    private int noAim;

    protected void onEvent(Event event) {
        if (event instanceof EventMouseMove) {
            String aimWhen = STRING("aimwhen");
            String mouseMode = STRING("mousemode");
            String priority = STRING("priority");
            String aimWhere = STRING("aimwhere");
            int ya = INTEGER("yawSpeedRandom");
            int yaw = (int) (INTEGER("yawSpeed") + (Math.random() * ya) - ya / 2) + (noAim * INTEGER("yawSpeedIncrease") / 50);
            int pa = INTEGER("pitchSpeedRandom");
            int pitch = (int) (INTEGER("pitchSpeed") + (Math.random() * pa) - pa / 2) + (noAim * INTEGER("pitchSpeedIncrease") / 50);
            double distance = DOUBLE("distance");
            double custom = DOUBLE("aimwhere", "custom");
            double predict = DOUBLE("predict");
            int range = INTEGER("range");
            int maxOvershoot = INTEGER("maximumOvershoot");
            boolean invertYaw = BOOLEAN("invertyaw");
            boolean invertPitch = BOOLEAN("invertpitch");
            if (mouseMode.equalsIgnoreCase("add"))
                mc().mouseHelper.overrideMode = 2;
            else if (mouseMode.equalsIgnoreCase("complete"))
                mc().mouseHelper.overrideMode = 1;

            if (aimWhen.equalsIgnoreCase("mouse") && !mc().mouseHelper.moving)
                return;
            MouseAimBase.updateRotations(aimWhere, custom, predict);
            MouseAimBase.mouseRots = new int[]{yaw, pitch};
            int[] changeMouse = MouseAimBase.getNextRotations(priority, distance, range, aimWhere, custom, invertYaw, invertPitch, maxOvershoot, 1, 1, new float[]{player().rotationYaw, player().rotationPitch}, predict);
            if (TargetUtil.target == null) {
                mc().mouseHelper.overrideMode = 0;
                return;
            }

            mc().mouseHelper.overrideX = changeMouse[0];
            mc().mouseHelper.overrideY = changeMouse[1];

            if (!mc().objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY)) noAim++;
            else noAim = 0;

            /*float[] rotations = RUtils.limitAngleChange(new float[]{p.rotationYaw, p.rotationPitch}, to, turnSpeedYaw, turnSpeedPitch);

            final float f = mc().gameSettings.mouseSensitivity * 0.6F + 0.2F;
            final float gcd = f * f * f * 1.2F;

            rotations[0] -= rotations[0] % gcd;
            rotations[1] -= rotations[1] % gcd;*/

            //System.out.printf("%s|%s%n", Arrays.toString(to), Arrays.toString(rotations));

            //p.rotationYaw = rotations[0];
            //p.rotationPitch = rotations[1];
        } else if (event instanceof EventRender) {
            RenderBase.render((EventRender) event);
        }
    }
}
