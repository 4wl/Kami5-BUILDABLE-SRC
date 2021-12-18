package tech.mmmax.kami.api.utils.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class BlockUtils implements IMinecraft {

    static List tickCache = new ArrayList();

    public BlockUtils() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        BlockUtils.tickCache = new ArrayList();
    }

    public static boolean placeBlock(BlockPos pos, boolean sneak) {
        Block block = BlockUtils.mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        } else {
            EnumFacing side = getPlaceableSide(pos);

            if (side == null) {
                return false;
            } else {
                BlockPos neighbour = pos.offset(side);
                EnumFacing opposite = side.getOpposite();
                Vec3d hitVec = (new Vec3d(neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));

                if (!BlockUtils.mc.player.isSneaking()) {
                    BlockUtils.mc.getConnection().sendPacket(new CPacketEntityAction(BlockUtils.mc.player, Action.START_SNEAKING));
                }

                EnumActionResult action = BlockUtils.mc.playerController.processRightClickBlock(BlockUtils.mc.player, BlockUtils.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);

                BlockUtils.mc.getConnection().sendPacket(new CPacketEntityAction(BlockUtils.mc.player, Action.STOP_SNEAKING));
                BlockUtils.tickCache.add(pos);
                return action == EnumActionResult.SUCCESS;
            }
        }
    }

    public static boolean placeBlock(BlockPos pos, boolean sneak, EnumHand hand) {
        Block block = BlockUtils.mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        } else {
            EnumFacing side = getPlaceableSide(pos);

            if (side == null) {
                return false;
            } else {
                BlockPos neighbour = pos.offset(side);
                EnumFacing opposite = side.getOpposite();
                Vec3d hitVec = (new Vec3d(neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));

                if (!BlockUtils.mc.player.isSneaking()) {
                    BlockUtils.mc.getConnection().sendPacket(new CPacketEntityAction(BlockUtils.mc.player, Action.START_SNEAKING));
                }

                EnumActionResult action = BlockUtils.mc.playerController.processRightClickBlock(BlockUtils.mc.player, BlockUtils.mc.world, neighbour, opposite, hitVec, hand);

                BlockUtils.mc.getConnection().sendPacket(new CPacketEntityAction(BlockUtils.mc.player, Action.STOP_SNEAKING));
                BlockUtils.tickCache.add(pos);
                return action == EnumActionResult.SUCCESS;
            }
        }
    }

    public static void breakBlock(BlockPos p) {
        Block block = BlockUtils.mc.world.getBlockState(p).getBlock();
        EnumFacing side = getPlaceableSide(p);

        BlockUtils.mc.player.connection.sendPacket(new CPacketPlayerDigging(net.minecraft.network.play.client.CPacketPlayerDigging.Action.START_DESTROY_BLOCK, p, side));
        BlockUtils.mc.player.connection.sendPacket(new CPacketPlayerDigging(net.minecraft.network.play.client.CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, p, side));
        BlockUtils.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing side = aenumfacing[j];
            BlockPos neighbour = pos.offset(side);

            if (BlockUtils.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtils.mc.world.getBlockState(neighbour), false) || BlockUtils.tickCache.contains(neighbour)) {
                IBlockState blockState = BlockUtils.mc.world.getBlockState(neighbour);

                if (!blockState.getMaterial().isReplaceable()) {
                    return side;
                }
            }
        }

        return null;
    }

    public static EnumFacing getPlaceableSideH(BlockPos pos) {
        EnumFacing[] aenumfacing = EnumFacing.HORIZONTALS;
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing side = aenumfacing[j];
            BlockPos neighbour = pos.offset(side);

            if (BlockUtils.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtils.mc.world.getBlockState(neighbour), false)) {
                IBlockState blockState = BlockUtils.mc.world.getBlockState(neighbour);

                if (!blockState.getMaterial().isReplaceable()) {
                    return side;
                }
            }
        }

        return null;
    }

    public static float[] getNeededRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new float[] { BlockUtils.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - BlockUtils.mc.player.rotationYaw), BlockUtils.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - BlockUtils.mc.player.rotationPitch)};
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(BlockUtils.mc.player.posX, BlockUtils.mc.player.posY + (double) BlockUtils.mc.player.getEyeHeight(), BlockUtils.mc.player.posZ);
    }

    public static List getSphere(double range, BlockPos pos, boolean sphere, boolean hollow) {
        ArrayList circleblocks = new ArrayList();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();

        for (int x = cx - (int) range; (double) x <= (double) cx + range; ++x) {
            for (int z = cz - (int) range; (double) z <= (double) cz + range; ++z) {
                for (int y = sphere ? cy - (int) range : cy; (double) y < (sphere ? (double) cy + range : (double) cy + range); ++y) {
                    double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));

                    if (dist < range * range && (!hollow || dist >= (range - 1.0D) * (range - 1.0D))) {
                        BlockPos l = new BlockPos(x, y, z);

                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

    public static boolean canPlaceBlock(BlockPos pos) {
        boolean allow = true;

        if (!BlockUtils.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            allow = false;
        }

        Iterator iterator = BlockUtils.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).iterator();

        if (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            allow = false;
        }

        return allow;
    }

    static {
        new BlockUtils();
    }
}
