package tech.mmmax.kami.api.utils.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.Explosion;
import tech.mmmax.kami.api.wrapper.IMinecraft;
import tech.mmmax.kami.mixin.mixins.access.ICPacketUseEntity;

public class CrystalUtil implements IMinecraft {

    public static List hitCrystals = new ArrayList();
    public static List placedPositions = new ArrayList();

    public static EntityEnderCrystal getCrystalToBreak(boolean inhibit, double range) {
        return (EntityEnderCrystal) CrystalUtil.mc.world.loadedEntityList.stream().filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).filter(test<invokedynamic>(inhibit)).min(Comparator.comparingDouble(applyAsDouble<invokedynamic>())).orElse((Object) null);
    }

    public static CrystalUtil.Crystal getPlacePos(Entity target, double range, double wallRange, boolean oneThirteen, double moveFactor, boolean antiSuicide, double antiSuicideFactor, double minDamage, double maxSelfDamage, boolean placeInhibit, boolean placeBlocks, int raytraceHits, double shrinkFactor) {
        ArrayList crystals = new ArrayList();
        Iterator iterator = getAvailablePositions(range, wallRange, oneThirteen, placeBlocks, raytraceHits, shrinkFactor).iterator();

        while (iterator.hasNext()) {
            BlockPos pos = (BlockPos) iterator.next();

            crystals.add(new CrystalUtil.Crystal(pos, target, moveFactor));
        }

        return (CrystalUtil.Crystal) crystals.stream().filter(test<invokedynamic>(placeInhibit)).filter(test<invokedynamic>(minDamage)).filter(test<invokedynamic>(antiSuicide, maxSelfDamage)).filter(test<invokedynamic>(antiSuicide)).max(Comparator.comparingDouble(applyAsDouble<invokedynamic>(antiSuicide, antiSuicideFactor))).orElse((Object) null);
    }

    public static List getAvailablePositions(double range, double wallRange, boolean oneThirteen, boolean placeBlocks, int raytraceHits, double shrinkFactor) {
        return (List) BlockUtils.getSphere(range, CrystalUtil.mc.player.getPosition(), true, false).stream().filter(test<invokedynamic>(oneThirteen, placeBlocks)).filter(test<invokedynamic>()).filter(test<invokedynamic>(shrinkFactor, wallRange, raytraceHits)).collect(Collectors.toList());
    }

    public static EnumHand getCrystalHand() {
        return CrystalUtil.mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    public static void placeCrystal(BlockPos pos, boolean packet, EnumHand swingArm) {
        if (packet) {
            CrystalUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, getCrystalHand(), 0.0F, 0.0F, 0.0F));
        } else {
            CrystalUtil.mc.playerController.processRightClickBlock(CrystalUtil.mc.player, CrystalUtil.mc.world, pos, EnumFacing.UP, new Vec3d(0.0D, 0.0D, 0.0D), getCrystalHand());
        }

        if (swingArm != null) {
            CrystalUtil.mc.player.swingArm(swingArm);
        }

        CrystalUtil.placedPositions.add(pos);
    }

    public static void breakCrystal(EntityEnderCrystal entityEnderCrystal) {
        CrystalUtil.mc.getConnection().sendPacket(new CPacketUseEntity(entityEnderCrystal));
        CrystalUtil.hitCrystals.add(Integer.valueOf(entityEnderCrystal.getEntityId()));
        CrystalUtil.placedPositions.clear();
    }

    public static void breakCrystal(int id) {
        CPacketUseEntity packet = new CPacketUseEntity();

        ((ICPacketUseEntity) packet).setEntityId(id);
        ((ICPacketUseEntity) packet).setAction(Action.ATTACK);
        CrystalUtil.mc.player.connection.sendPacket(packet);
        CrystalUtil.hitCrystals.add(Integer.valueOf(id));
        CrystalUtil.placedPositions.clear();
    }

    public static void breakCrystalNoAdd(int id) {
        CPacketUseEntity packet = new CPacketUseEntity();

        ((ICPacketUseEntity) packet).setEntityId(id);
        ((ICPacketUseEntity) packet).setAction(Action.ATTACK);
        CrystalUtil.mc.player.connection.sendPacket(packet);
        CrystalUtil.placedPositions.clear();
    }

    public static boolean canPlaceCrystal1(BlockPos pos, boolean one13, boolean placeBlocks) {
        return (CrystalUtil.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || CrystalUtil.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || placeBlocks && BlockUtils.canPlaceBlock(pos)) && CrystalUtil.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() == Blocks.AIR && (CrystalUtil.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock() == Blocks.AIR || one13);
    }

    public static boolean canPlaceCrystal2(BlockPos pos) {
        Iterator iterator = CrystalUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(0, 1, 0))).iterator();

        Entity entity;

        do {
            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                entity = (Entity) iterator.next();
            } while (entity.isDead);
        } while (entity instanceof EntityEnderCrystal && CrystalUtil.hitCrystals.contains(Integer.valueOf(entity.getEntityId())));

        return false;
    }

    public static List getLoadedCrystalsInRange(double range) {
        return (List) CrystalUtil.mc.world.loadedEntityList.stream().filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).collect(Collectors.toList());
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity, double moveFactor) {
        try {
            Vec3d applied = new Vec3d(entity.posX + entity.motionX * moveFactor, entity.posY + entity.motionY * moveFactor, entity.posZ + entity.motionZ * moveFactor);
            double factor = (1.0D - applied.distanceTo(new Vec3d(posX, posY, posZ)) / 12.0D) * (double) entity.world.getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
            float calculatedDamage = (float) ((int) ((factor * factor + factor) / 2.0D * 7.0D * 12.0D + 1.0D));
            double damage = 1.0D;

            if (entity instanceof EntityLivingBase) {
                damage = (double) getBlastReduction((EntityLivingBase) entity, calculatedDamage * (Minecraft.getMinecraft().world.getDifficulty().getId() == 0 ? 0.0F : (Minecraft.getMinecraft().world.getDifficulty().getId() == 2 ? 1.0F : (Minecraft.getMinecraft().world.getDifficulty().getId() == 1 ? 0.5F : 1.5F))), new Explosion(Minecraft.getMinecraft().world, (Entity) null, posX, posY, posZ, 6.0F, false, true));
            }

            return (float) damage;
        } catch (Exception exception) {
            return 0.0F;
        }
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            damage *= 1.0F - MathHelper.clamp((float) EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), DamageSource.causeExplosionDamage(explosion)), 0.0F, 20.0F) / 25.0F;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0F;
            }

            return damage;
        } else {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    public static boolean doSmartRaytrace(Vec3d startPos, AxisAlignedBB endBB, Vec3d playerPos, double wallRange, int hitCount) {
        boolean allow = false;
        int hits = 0;
        Vec3d[] centerX = getSmartRaytraceVertex(endBB);
        int i = centerX.length;

        for (int centerY = 0; centerY < i; ++centerY) {
            Vec3d pos = centerX[centerY];
            RayTraceResult centerZ = CrystalUtil.mc.world.rayTraceBlocks(startPos, pos);

            if (centerZ == null || centerZ.typeOfHit != Type.BLOCK) {
                ++hits;
            }
        }

        if (hits >= hitCount) {
            allow = true;
        }

        if (!allow) {
            double d0 = (endBB.maxX - endBB.minX) / 2.0D;
            double d1 = (endBB.maxY - endBB.minY) / 2.0D;
            double d2 = (endBB.maxZ - endBB.minZ) / 2.0D;

            if (playerPos.distanceTo(new Vec3d(endBB.minX + d0, endBB.minY + d1, endBB.minZ + d2)) <= wallRange) {
                allow = true;
            }
        }

        return allow;
    }

    public static Vec3d[] getSmartRaytraceVertex(AxisAlignedBB boundingBox) {
        double centerX = (boundingBox.maxX - boundingBox.minX) / 2.0D;
        double centerY = (boundingBox.maxY - boundingBox.minY) / 2.0D;
        double centerZ = (boundingBox.maxZ - boundingBox.minZ) / 2.0D;

        return new Vec3d[] { new Vec3d(boundingBox.minX + centerX, boundingBox.minY + centerY, boundingBox.minZ + centerZ), new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ), new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ), new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ), new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ), new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ), new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ), new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ), new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ)};
    }

    private static boolean lambda$getLoadedCrystalsInRange$12(double range, Entity it) {
        return (double) it.getDistance(CrystalUtil.mc.player) < range;
    }

    private static boolean lambda$getLoadedCrystalsInRange$11(Entity it) {
        return it instanceof EntityEnderCrystal;
    }

    private static boolean lambda$getAvailablePositions$10(double shrinkFactor, double wallRange, int raytraceHits, BlockPos pos) {
        return doSmartRaytrace(BlockUtils.getEyesPos(), (new AxisAlignedBB((double) pos.getX() + 0.5D - 1.0D, (double) pos.getY(), (double) pos.getZ() + 0.5D - 1.0D, (double) pos.getX() + 0.5D + 1.0D, (double) (pos.getY() + 2), (double) pos.getZ() + 0.5D + 1.0D)).shrink(shrinkFactor), new Vec3d(CrystalUtil.mc.player.posX + (double) (CrystalUtil.mc.player.width / 2.0F), CrystalUtil.mc.player.posY, CrystalUtil.mc.player.posZ + (double) (CrystalUtil.mc.player.width / 2.0F)), wallRange, raytraceHits);
    }

    private static boolean lambda$getAvailablePositions$9(boolean oneThirteen, boolean placeBlocks, BlockPos pos) {
        return canPlaceCrystal1(pos, oneThirteen, placeBlocks);
    }

    private static double lambda$getPlacePos$8(boolean antiSuicide, double antiSuicideFactor, CrystalUtil.Crystal crystal) {
        return antiSuicide ? (double) crystal.enemyDamage - (double) crystal.selfDamage * antiSuicideFactor : (double) crystal.enemyDamage;
    }

    private static boolean lambda$getPlacePos$7(boolean antiSuicide, CrystalUtil.Crystal crystal) {
        return !antiSuicide || crystal.selfDamage <= crystal.enemyDamage && CrystalUtil.mc.player.getHealth() + CrystalUtil.mc.player.getAbsorptionAmount() - crystal.selfDamage > 0.0F;
    }

    private static boolean lambda$getPlacePos$6(boolean antiSuicide, double maxSelfDamage, CrystalUtil.Crystal crystal) {
        return !antiSuicide || (double) crystal.selfDamage <= maxSelfDamage;
    }

    private static boolean lambda$getPlacePos$5(double minDamage, CrystalUtil.Crystal crystal) {
        return (double) crystal.enemyDamage >= minDamage;
    }

    private static boolean lambda$getPlacePos$4(boolean placeInhibit, CrystalUtil.Crystal crystal) {
        return !placeInhibit || !CrystalUtil.placedPositions.contains(crystal.crystalPos);
    }

    private static double lambda$getCrystalToBreak$3(Entity entity) {
        return (double) CrystalUtil.mc.player.getDistance(entity);
    }

    private static boolean lambda$getCrystalToBreak$2(boolean inhibit, Entity entity) {
        return !inhibit || !CrystalUtil.hitCrystals.contains(Integer.valueOf(entity.getEntityId()));
    }

    private static boolean lambda$getCrystalToBreak$1(double range, Entity entity) {
        return (double) CrystalUtil.mc.player.getDistance(entity) <= range;
    }

    private static boolean lambda$getCrystalToBreak$0(Entity entity) {
        return entity instanceof EntityEnderCrystal;
    }

    public static class Crystal {

        float selfDamage;
        float enemyDamage;
        long startTime;
        public boolean blockUnder;
        public BlockPos crystalPos;

        public Crystal(BlockPos crystalPos, Entity target, double moveFactor) {
            this.crystalPos = crystalPos;
            this.blockUnder = this.blockUnder;
            this.calculate(target, moveFactor);
        }

        public float getSelfDamage() {
            return this.selfDamage;
        }

        public float getEnemyDamage() {
            return this.enemyDamage;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public void calculate(Entity target, double moveFactor) {
            this.enemyDamage = CrystalUtil.calculateDamage((double) this.crystalPos.getX() + 0.5D, (double) this.crystalPos.getY() + 1.0D, (double) this.crystalPos.getZ() + 0.5D, target, moveFactor);
            this.selfDamage = CrystalUtil.calculateDamage((double) this.crystalPos.getX() + 0.5D, (double) this.crystalPos.getY() + 1.0D, (double) this.crystalPos.getZ() + 0.5D, Minecraft.getMinecraft().player, 0.0D);
            this.startTime = System.currentTimeMillis();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                CrystalUtil.Crystal crystal = (CrystalUtil.Crystal) o;

                return this.crystalPos.equals(crystal.crystalPos);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[] { this.crystalPos});
        }
    }
}
