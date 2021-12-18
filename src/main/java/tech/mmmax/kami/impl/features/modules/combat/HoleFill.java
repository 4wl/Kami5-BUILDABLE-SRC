package tech.mmmax.kami.impl.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.utils.player.RotationUtil;
import tech.mmmax.kami.api.utils.player.TargetUtils;
import tech.mmmax.kami.api.utils.world.BlockUtils;
import tech.mmmax.kami.api.utils.world.HoleUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class HoleFill extends Module {

    Value switchMode = (new ValueBuilder()).withDescriptor("Switch Mode", "switchMode").withValue("Ghost").withModes(new String[] { "Normal", "Ghost", "Require"}).register(this);
    Value range = (new ValueBuilder()).withDescriptor("Range").withValue(Double.valueOf(5.0D)).withRange(Double.valueOf(1.0D), Double.valueOf(10.0D)).register(this);
    Value wallRange = (new ValueBuilder()).withDescriptor("Wall Range").withValue(Double.valueOf(3.0D)).withRange(Double.valueOf(1.0D), Double.valueOf(10.0D)).register(this);
    Value delay = (new ValueBuilder()).withDescriptor("Delay", "delay").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(1000)).register(this);
    Value blocksPerTick = (new ValueBuilder()).withDescriptor("Blocks Per Tick", "blocksPerTick").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(1), Integer.valueOf(10)).register(this);
    Value disableAfter = (new ValueBuilder()).withDescriptor("Disable", "disable").withValue(Boolean.valueOf(true)).register(this);
    Value rotate = (new ValueBuilder()).withDescriptor("Rotate", "rotate").withValue(Boolean.valueOf(true)).register(this);
    Value doubles = (new ValueBuilder()).withDescriptor("Doubles").withValue(Boolean.valueOf(true)).register(this);
    Value smart = (new ValueBuilder()).withDescriptor("Smart", "smart").withValue(Boolean.valueOf(false)).register(this);
    Value smartTargetRange = (new ValueBuilder()).withDescriptor("Target Range", "targetRange").withValue(Double.valueOf(5.0D)).withRange(Double.valueOf(1.0D), Double.valueOf(10.0D)).register(this);
    Value smartBlockRange = (new ValueBuilder()).withDescriptor("Smart Block Range", "smartBlockRange").withValue(Double.valueOf(1.0D)).withRange(Double.valueOf(0.3D), Double.valueOf(4.0D)).register(this);
    Value noSelfFill = (new ValueBuilder()).withDescriptor("No Self Fill").withValue(Boolean.valueOf(false)).register(this);
    Value selfDist = (new ValueBuilder()).withDescriptor("Self Dist").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(3)).register(this);
    Timer timeSystem = new Timer();
    List holes = new ArrayList();
    BlockPos render = null;
    Entity target;

    public HoleFill() {
        super("Hole Fill", Feature.Category.Combat);
    }

    public void onEnable() {
        super.onEnable();
        this.timeSystem.resetDelay();
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            this.target = TargetUtils.getTarget(((Number) this.smartTargetRange.getValue()).doubleValue());
            this.timeSystem.setDelay(((Number) this.delay.getValue()).longValue());
            int blocksPlaced = 0;

            if (this.timeSystem.isPassed()) {
                this.getHoles();
                if (this.holes == null || this.holes.size() == 0) {
                    if (((Boolean) this.disableAfter.getValue()).booleanValue()) {
                        this.setEnabled(false);
                    }

                    return;
                }

                if (((String) this.switchMode.getValue()).equalsIgnoreCase("Require") && HoleFill.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() != Item.getItemFromBlock(Blocks.OBSIDIAN)) {
                    return;
                }

                int oldSlot = HoleFill.mc.player.inventory.currentItem;
                int blockSlot = InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN));

                if (blockSlot == -1) {
                    return;
                }

                boolean switched = false;
                Iterator iterator = this.holes.iterator();

                while (iterator.hasNext()) {
                    HoleUtils.Hole hole = (HoleUtils.Hole) iterator.next();

                    if (!switched) {
                        this.doSwitch(blockSlot);
                        switched = true;
                    }

                    this.doRotate(hole.pos1);
                    if (hole.doubleHole) {
                        BlockUtils.placeBlock(hole.pos1, true);
                        BlockUtils.placeBlock(hole.pos2, true);
                    } else {
                        BlockUtils.placeBlock(hole.pos1, true);
                    }

                    this.render = hole.pos1;
                    ++blocksPlaced;
                    if (blocksPlaced >= ((Number) this.blocksPerTick.getValue()).intValue()) {
                        break;
                    }
                }

                if (((String) this.switchMode.getValue()).equalsIgnoreCase("Ghost") && switched) {
                    this.doSwitch(oldSlot);
                }

                this.timeSystem.resetDelay();
            } else {
                if (RotationUtil.INSTANCE.rotating) {
                    RotationUtil.INSTANCE.resetRotations();
                }

                RotationUtil.INSTANCE.rotating = false;
            }

        }
    }

    public void getHoles() {
        this.loadHoles();
    }

    public void loadHoles() {


        holes = (List) HoleUtils.getHoles(((Number) this.range.getValue()).doubleValue(), HoleFill.mc.player.getPosition(), ((Boolean) this.doubles.getValue()).booleanValue()).stream().filter((hole) -> {
            boolean isAllowedHole = true;

            if (hole.doubleHole) {
                // new(net.minecraft.util.math.AxisAlignedBB.class)
                new AxisAlignedBB((double)hole.pos1.getX(), (double)hole.pos1.getY(), (double)hole.pos1.getZ(), (double)(hole.pos2.getX() + 1), (double)(hole.pos2.getY() + 1), (double)(hole.pos2.getZ() + 1));
            }

            for (Iterator iterator = HoleFill.mc.world.getEntitiesWithinAABB(Entity.class, bb).iterator(); iterator.hasNext(); isAllowedHole = false) {
                Entity e = (Entity) iterator.next();
            }

            return isAllowedHole;
        }).filter((hole) -> {
            boolean isAllowedSmart = false;

            if (((Boolean) this.smart.getValue()).booleanValue()) {
                if (this.target != null && this.target.getDistance((double) hole.pos1.getX() + 0.5D, (double) (hole.pos1.getY() + 1), (double) hole.pos1.getZ() + 0.5D) < ((Number) this.smartBlockRange.getValue()).doubleValue()) {
                    isAllowedSmart = true;
                }
            } else {
                isAllowedSmart = true;
            }

            return isAllowedSmart;
        }).filter((hole) -> {
            BlockPos pos = hole.pos1.add(0, 1, 0);
            boolean raytrace = HoleFill.mc.world.rayTraceBlocks(BlockUtils.getEyesPos(), new Vec3d(pos)) != null;

            return !raytrace || HoleFill.mc.player.getDistance((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()) <= ((Number) this.wallRange.getValue()).doubleValue();
        }).collect(Collectors.toList());
    }

    public void doSwitch(Item i) {
        if (((String) this.switchMode.getValue()).equalsIgnoreCase("Normal")) {
            InventoryUtils.switchToSlot(i);
        }

        if (((String) this.switchMode.getValue()).equalsIgnoreCase("Ghost")) {
            InventoryUtils.switchToSlotGhost(i);
        }

    }

    public void doSwitch(int i) {
        if (((String) this.switchMode.getValue()).equalsIgnoreCase("Normal")) {
            InventoryUtils.switchToSlot(i);
        }

        if (((String) this.switchMode.getValue()).equalsIgnoreCase("Ghost")) {
            InventoryUtils.switchToSlotGhost(i);
        }

    }

    public void doRotate(BlockPos pos) {
        if (((Boolean) this.rotate.getValue()).booleanValue()) {
            if (!RotationUtil.INSTANCE.rotating) {
                RotationUtil.INSTANCE.rotating = true;
            }

            RotationUtil.INSTANCE.rotate(new Vec3d((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()));
        }

    }

    public void onDisable() {
        super.onDisable();
    }
}
