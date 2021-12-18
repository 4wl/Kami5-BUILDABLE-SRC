package tech.mmmax.kami.api.utils.player;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import tech.mmmax.kami.api.management.FriendManager;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class TargetUtils implements IMinecraft {

    public static EntityLivingBase getTarget(double targetRange) {
        return (EntityLivingBase) TargetUtils.mc.world.getLoadedEntityList().stream().filter((entity) -> {
            return entity instanceof EntityPlayer;
        }).filter(TargetUtils::isAlive).filter((entity) -> {
            return entity.getEntityId() != TargetUtils.mc.player.getEntityId();
        }).filter((entity) -> {
            return !FriendManager.INSTANCE.isFriend(entity);
        }).filter((entity) -> {
            return (double) TargetUtils.mc.player.getDistance(entity) <= targetRange;
        }).min(Comparator.comparingDouble((entity) -> {
            return (double) TargetUtils.mc.player.getDistance(entity);
        })).orElse((Object) null);
    }

    public static boolean isAlive(Entity entity) {
        return isLiving(entity) && !entity.isDead && ((EntityLivingBase) entity).getHealth() > 0.0F;
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }
}
