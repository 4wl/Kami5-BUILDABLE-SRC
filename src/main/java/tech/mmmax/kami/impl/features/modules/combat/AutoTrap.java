package tech.mmmax.kami.impl.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.utils.player.TargetUtils;
import tech.mmmax.kami.api.utils.world.BlockUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class AutoTrap extends Module {

    Timer timer = new Timer();
    Value delay = (new ValueBuilder()).withDescriptor("Delay").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(0), Integer.valueOf(1000)).withAction((set) -> {
        this.timer.setDelay((long) ((Number) set.getValue()).intValue());
    }).register(this);
    Value trapMode = (new ValueBuilder()).withDescriptor("Mode").withValue("Top").withModes(new String[] { "Full", "City", "Top", "None"}).register(this);
    Value blocksPerTick = (new ValueBuilder()).withDescriptor("BPT").withValue(Integer.valueOf(10)).withRange(Integer.valueOf(0), Integer.valueOf(10)).register(this);
    Value targetRange = (new ValueBuilder()).withDescriptor("Target Range").withValue(Double.valueOf(5.0D)).withRange(Double.valueOf(1.0D), Double.valueOf(10.0D)).register(this);
    Value disable = (new ValueBuilder()).withDescriptor("Disable").withValue(Boolean.valueOf(false)).register(this);
    Entity target;
    public BlockPos[] fullOffsets = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(1, 1, 0), new BlockPos(0, 1, 1), new BlockPos(-1, 1, 0), new BlockPos(0, 1, -1), new BlockPos(1, 2, 0), new BlockPos(0, 2, 0)};
    public BlockPos[] cityOffsets = new BlockPos[] { new BlockPos(1, 1, 0), new BlockPos(1, 1, 1), new BlockPos(0, 1, 1), new BlockPos(-1, 1, 1), new BlockPos(-1, 1, 0), new BlockPos(-1, 1, -1), new BlockPos(0, 1, -1), new BlockPos(-1, 1, -1), new BlockPos(1, 2, 0), new BlockPos(0, 2, 0)};
    BlockPos[] surroundOffsets = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 0)};

    public AutoTrap() {
        super("Auto Trap", Feature.Category.Combat);
    }

    BlockPos getPlayerPos(Entity player) {
        double decimalPoint = player.posY - Math.floor(player.posY);

        return new BlockPos(player.posX, decimalPoint > 0.8D ? Math.floor(player.posY) + 1.0D : Math.floor(player.posY), player.posZ);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (this.timer.isPassed()) {
                this.target = TargetUtils.getTarget(((Number) this.targetRange.getValue()).doubleValue());
                if (this.target == null) {
                    return;
                }

                if (AutoTrap.mc.world.getBlockState(this.getPlayerPos(this.target).add(0, 2, 0)).getMaterial().isSolid() && ((Boolean) this.disable.getValue()).booleanValue()) {
                    this.setEnabled(false);
                    return;
                }

                int blocksInTick = 0;
                boolean switched = false;
                int oldSlot = AutoTrap.mc.player.inventory.currentItem;
                int slot = InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN));

                if (slot == -1) {
                    return;
                }

                if (!((String) this.trapMode.getValue()).equalsIgnoreCase("None")) {
                    BlockPos[] offsets = ((String) this.trapMode.getValue()).equalsIgnoreCase("Full") ? this.offsetBlocks(this.fullOffsets, this.getPlayerPos(this.target)) : (((String) this.trapMode.getValue()).equalsIgnoreCase("City") ? this.offsetBlocks(this.cityOffsets, this.getPlayerPos(this.target)) : this.getObbyToHead(this.getPlayerPos(this.target)));
                    BlockPos[] ablockpos = offsets;
                    int i = offsets.length;

                    for (int j = 0; j < i; ++j) {
                        BlockPos pos = ablockpos[j];

                        if (this.canPlaceBlock(pos)) {
                            if (!switched) {
                                InventoryUtils.switchToSlotGhost(slot);
                                switched = true;
                            }

                            BlockUtils.placeBlock(pos, true);
                            ++blocksInTick;
                            if (blocksInTick > ((Number) this.blocksPerTick.getValue()).intValue()) {
                                break;
                            }
                        }
                    }

                    InventoryUtils.switchToSlotGhost(oldSlot);
                    this.timer.resetDelay();
                }
            }

        }
    }

    boolean canPlaceBlock(BlockPos pos) {
        boolean allow = true;

        if (!AutoTrap.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            allow = false;
        }

        Iterator iterator = AutoTrap.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (!(entity instanceof EntityArmorStand)) {
                allow = false;
                break;
            }
        }

        return allow;
    }

    BlockPos[] offsetBlocks(BlockPos[] toOffset, BlockPos offsetPlace) {
        BlockPos[] offsets = new BlockPos[toOffset.length];
        int index = 0;
        BlockPos[] ablockpos = toOffset;
        int i = toOffset.length;

        for (int j = 0; j < i; ++j) {
            BlockPos blockPos = ablockpos[j];

            offsets[index] = offsetPlace.add(blockPos);
            ++index;
        }

        return offsets;
    }

    public BlockPos[] getObbyToHead(BlockPos feet) {
        ArrayList obbyToHead = new ArrayList();
        BlockPos head = feet.add(new BlockPos(0, 1, 0));

        if (this.getSurroundedBlock(head) != null) {
            obbyToHead.add(this.getSurroundedBlock(head).add(new BlockPos(0, 1, 0)));
            obbyToHead.add(head.add(new BlockPos(0, 1, 0)));
        } else if (this.getSurroundedBlock(feet) != null) {
            obbyToHead.add(this.getSurroundedBlock(feet).add(new BlockPos(0, 1, 0)));
        } else if (this.getSurroundedBlock(feet.add(0, -1, 0)) != null) {
            obbyToHead.add(this.getSurroundedBlock(feet.add(new BlockPos(0, -1, 0))).add(new BlockPos(0, 1, 0)));
        }

        BlockPos[] blocks = new BlockPos[obbyToHead.size()];

        return (BlockPos[]) obbyToHead.toArray(blocks);
    }

    public BlockPos getSurroundedBlock(BlockPos feet) {
        BlockPos[] ablockpos = this.surroundOffsets;
        int i = ablockpos.length;

        for (int j = 0; j < i; ++j) {
            BlockPos offset = ablockpos[j];
            IBlockState blockState = AutoTrap.mc.world.getBlockState(feet.add(offset));

            if (!blockState.getMaterial().isReplaceable()) {
                return feet.add(offset);
            }
        }

        return null;
    }
}
