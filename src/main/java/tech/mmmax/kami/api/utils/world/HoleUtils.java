package tech.mmmax.kami.api.utils.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class HoleUtils implements IMinecraft {

    public static BlockPos[] holeOffsets = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};

    public static boolean isHole(BlockPos pos) {
        boolean isHole = false;
        int amount = 0;
        BlockPos[] ablockpos = HoleUtils.holeOffsets;
        int i = ablockpos.length;

        for (int j = 0; j < i; ++j) {
            BlockPos p = ablockpos[j];

            if (!HoleUtils.mc.world.getBlockState(pos.add(p)).getMaterial().isReplaceable()) {
                ++amount;
            }
        }

        if (amount == 5) {
            isHole = true;
        }

        return isHole;
    }

    public static boolean isObbyHole(BlockPos pos) {
        boolean isHole = true;
        int bedrock = 0;
        BlockPos[] ablockpos = HoleUtils.holeOffsets;
        int i = ablockpos.length;

        for (int j = 0; j < i; ++j) {
            BlockPos off = ablockpos[j];
            Block b = HoleUtils.mc.world.getBlockState(pos.add(off)).getBlock();

            if (!isSafeBlock(pos.add(off))) {
                isHole = false;
            } else if (b == Blocks.OBSIDIAN || b == Blocks.ENDER_CHEST || b == Blocks.ANVIL) {
                ++bedrock;
            }
        }

        if (HoleUtils.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock() != Blocks.AIR || HoleUtils.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
            isHole = false;
        }

        if (bedrock < 1) {
            isHole = false;
        }

        return isHole;
    }

    public static boolean isBedrockHoles(BlockPos pos) {
        boolean isHole = true;
        BlockPos[] ablockpos = HoleUtils.holeOffsets;
        int i = ablockpos.length;

        for (int j = 0; j < i; ++j) {
            BlockPos off = ablockpos[j];
            Block b = HoleUtils.mc.world.getBlockState(pos.add(off)).getBlock();

            if (b != Blocks.BEDROCK) {
                isHole = false;
            }
        }

        if (HoleUtils.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock() != Blocks.AIR || HoleUtils.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.AIR) {
            isHole = false;
        }

        return isHole;
    }

    public static HoleUtils.Hole isDoubleHole(BlockPos pos) {
        return checkOffset(pos, 1, 0) ? new HoleUtils.Hole(false, true, pos, pos.add(1, 0, 0)) : (checkOffset(pos, 0, 1) ? new HoleUtils.Hole(false, true, pos, pos.add(0, 0, 1)) : null);
    }

    public static boolean checkOffset(BlockPos pos, int offX, int offZ) {
        return HoleUtils.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && HoleUtils.mc.world.getBlockState(pos.add(offX, 0, offZ)).getBlock() == Blocks.AIR && isSafeBlock(pos.add(0, -1, 0)) && isSafeBlock(pos.add(offX, -1, offZ)) && isSafeBlock(pos.add(offX * 2, 0, offZ * 2)) && isSafeBlock(pos.add(-offX, 0, -offZ)) && isSafeBlock(pos.add(offZ, 0, offX)) && isSafeBlock(pos.add(-offZ, 0, -offX)) && isSafeBlock(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && isSafeBlock(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
    }

    static boolean isSafeBlock(BlockPos pos) {
        return HoleUtils.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || HoleUtils.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK || HoleUtils.mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST;
    }

    public static List getHoles(double range, BlockPos playerPos, boolean doubles) {
        ArrayList holes = new ArrayList();
        List circle = BlockUtils.getSphere(range, playerPos, true, false);
        Iterator iterator = circle.iterator();

        while (iterator.hasNext()) {
            BlockPos pos = (BlockPos) iterator.next();

            if (HoleUtils.mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
                if (isObbyHole(pos)) {
                    holes.add(new HoleUtils.Hole(false, false, pos));
                } else if (isBedrockHoles(pos)) {
                    holes.add(new HoleUtils.Hole(true, false, pos));
                } else if (doubles) {
                    HoleUtils.Hole dh = isDoubleHole(pos);

                    if (dh != null && (HoleUtils.mc.world.getBlockState(dh.pos1.add(0, 1, 0)).getBlock() == Blocks.AIR || HoleUtils.mc.world.getBlockState(dh.pos2.add(0, 1, 0)).getBlock() == Blocks.AIR)) {
                        holes.add(dh);
                    }
                }
            }
        }

        return holes;
    }

    public static class Hole {

        public boolean bedrock;
        public boolean doubleHole;
        public BlockPos pos1;
        public BlockPos pos2;

        public Hole(boolean bedrock, boolean doubleHole, BlockPos pos1, BlockPos pos2) {
            this.bedrock = bedrock;
            this.doubleHole = doubleHole;
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        public Hole(boolean bedrock, boolean doubleHole, BlockPos pos1) {
            this.bedrock = bedrock;
            this.doubleHole = doubleHole;
            this.pos1 = pos1;
        }
    }
}
