package tech.mmmax.kami.api.utils.player;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class PlayerUtils implements IMinecraft {

    public static void setSpeed(EntityLivingBase entity, double speed) {
        double[] dir = forward(speed);

        entity.motionX = dir[0];
        entity.motionZ = dir[1];
    }

    public static double getDefaultMoveSpeed() {
        double baseSpeed = 0.2873D;

        if (PlayerUtils.mc.player != null && PlayerUtils.mc.player.isPotionActive(Potion.getPotionById(1))) {
            int amplifier = PlayerUtils.mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();

            baseSpeed *= 1.0D + 0.2D * (double) (amplifier + 1);
        }

        return baseSpeed;
    }

    public static boolean isMoving(EntityLivingBase entity) {
        return entity.moveForward != 0.0F || entity.moveStrafing != 0.0F;
    }

    public static double[] forward(double speed) {
        float forward = PlayerUtils.mc.player.movementInput.moveForward;
        float side = PlayerUtils.mc.player.movementInput.moveStrafe;
        float yaw = PlayerUtils.mc.player.prevRotationYaw + (PlayerUtils.mc.player.rotationYaw - PlayerUtils.mc.player.prevRotationYaw) * PlayerUtils.mc.getRenderPartialTicks();

        if (forward != 0.0F) {
            if (side > 0.0F) {
                yaw += (float) (forward > 0.0F ? -45 : 45);
            } else if (side < 0.0F) {
                yaw += (float) (forward > 0.0F ? 45 : -45);
            }

            side = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double sin = Math.sin(Math.toRadians((double) (yaw + 90.0F)));
        double cos = Math.cos(Math.toRadians((double) (yaw + 90.0F)));
        double posX = (double) forward * speed * cos + (double) side * speed * sin;
        double posZ = (double) forward * speed * sin - (double) side * speed * cos;

        return new double[] { posX, posZ};
    }

    public static double[] radians() {
        float forward = PlayerUtils.mc.player.movementInput.moveForward;
        float side = PlayerUtils.mc.player.movementInput.moveStrafe;
        float yaw = PlayerUtils.mc.player.prevRotationYaw + (PlayerUtils.mc.player.rotationYaw - PlayerUtils.mc.player.prevRotationYaw) * PlayerUtils.mc.getRenderPartialTicks();

        if (forward != 0.0F) {
            if (side > 0.0F) {
                yaw += (float) (forward > 0.0F ? -45 : 45);
            } else if (side < 0.0F) {
                yaw += (float) (forward > 0.0F ? 45 : -45);
            }

            side = 0.0F;
            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double sin = Math.sin(Math.toRadians((double) (yaw + 90.0F)));
        double cos = Math.cos(Math.toRadians((double) (yaw + 90.0F)));
        double posX = (double) forward * cos + (double) side * sin;
        double posZ = (double) forward * sin - (double) side * cos;

        return new double[] { posX, posZ};
    }
}
