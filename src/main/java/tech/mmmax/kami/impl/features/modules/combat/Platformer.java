package tech.mmmax.kami.impl.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.utils.player.TargetUtils;
import tech.mmmax.kami.api.utils.world.BlockUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Platformer extends Module {

    Value blocksPerTick = (new ValueBuilder()).withDescriptor("Blocks Per Tick").withValue(Integer.valueOf(10)).withRange(Integer.valueOf(1), Integer.valueOf(10)).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Forward").withModes(new String[] { "Forward", "Square"}).register(this);
    Value size = (new ValueBuilder()).withDescriptor("Size").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(1), Integer.valueOf(4)).register(this);
    Value motionFactor = (new ValueBuilder()).withDescriptor("Motion Factor").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(1), Integer.valueOf(5)).register(this);
    Value targetRange = (new ValueBuilder()).withDescriptor("Target Range").withValue(Integer.valueOf(10)).withRange(Integer.valueOf(2), Integer.valueOf(15)).register(this);
    Value range = (new ValueBuilder()).withDescriptor("Range").withValue(Integer.valueOf(4)).withRange(Integer.valueOf(1), Integer.valueOf(9)).register(this);
    Entity target;

    public Platformer() {
        super("Platformer", Feature.Category.Combat);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if ((this.target = TargetUtils.getTarget(((Number) this.targetRange.getValue()).doubleValue())) != null) {
                boolean switched = false;
                int blocksInTick = 0;
                int oldSlot = Platformer.mc.player.inventory.currentItem;
                int blockSlot = InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN));

                if (blockSlot != -1) {
                    List posList = this.getPositions(this.target);
                    Iterator iterator = posList.iterator();

                    while (iterator.hasNext()) {
                        BlockPos pos = (BlockPos) iterator.next();

                        if (BlockUtils.canPlaceBlock(pos)) {
                            if (!switched) {
                                InventoryUtils.switchToSlotGhost(blockSlot);
                                switched = true;
                            }

                            BlockUtils.placeBlock(pos, true);
                            ++blocksInTick;
                            if (blocksInTick > ((Number) this.blocksPerTick.getValue()).intValue()) {
                                break;
                            }
                        }
                    }

                    if (switched) {
                        InventoryUtils.switchToSlotGhost(oldSlot);
                    }

                }
            }
        }
    }

    List getPositions(Entity target) {
        ArrayList positions = new ArrayList();
        int x;

        if (((String) this.mode.getValue()).equals("Forward")) {
            for (x = 1; x <= ((Number) this.size.getValue()).intValue(); ++x) {
                BlockPos z = new BlockPos(target.posX + target.motionX * ((Number) this.motionFactor.getValue()).doubleValue() * (double) x, target.posY - 1.0D, target.posZ + target.motionZ * ((Number) this.motionFactor.getValue()).doubleValue() * (double) x);

                if (Platformer.mc.player.getDistance((double) z.getX(), (double) z.getY(), (double) z.getZ()) <= ((Number) this.range.getValue()).doubleValue()) {
                    positions.add(z);
                }
            }
        }

        if (((String) this.mode.getValue()).equals("Square")) {
            for (x = -((Number) this.size.getValue()).intValue(); x < ((Number) this.size.getValue()).intValue(); ++x) {
                for (int i = -((Number) this.size.getValue()).intValue(); i < ((Number) this.size.getValue()).intValue(); ++i) {
                    BlockPos pos = new BlockPos(target.posX + (double) x, target.posY - 1.0D, target.posZ + (double) i);

                    if (Platformer.mc.player.getDistance((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()) <= ((Number) this.range.getValue()).doubleValue()) {
                        positions.add(pos);
                    }
                }
            }
        }

        return positions;
    }

    public String getHudInfo() {
        return (String) this.mode.getValue();
    }
}
