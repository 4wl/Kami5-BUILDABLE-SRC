package tech.mmmax.kami.impl.features.modules.combat;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.player.TargetUtils;
import tech.mmmax.kami.api.utils.world.HoleUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class PvPBot extends Module {

    Value targetRange = (new ValueBuilder()).withDescriptor("Target Range").withValue(Integer.valueOf(15)).withRange(Integer.valueOf(5), Integer.valueOf(50)).register(this);
    Value doubles = (new ValueBuilder()).withDescriptor("Doubles").withValue(Boolean.valueOf(true)).register(this);
    Entity target;
    List holes;
    boolean initialized;
    ExecutorService service = Executors.newCachedThreadPool();

    public PvPBot() {
        super("PvP Bot", Feature.Category.Combat);
    }

    public void onEnable() {
        super.onEnable();
        if (!this.initialized) {
            this.service.submit(new PvPBot.HoleCallable());
        }

    }

    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            this.target = TargetUtils.getTarget(((Number) this.targetRange.getValue()).doubleValue());
            if (this.holes != null) {
                HoleUtils.Hole targetHole = (HoleUtils.Hole) this.holes.stream().min(Comparator.comparingDouble(applyAsDouble<invokedynamic>(this))).orElse( null);

                if (targetHole != null) {
                    ;
                }
            }
        }
    }

    double rateHole(HoleUtils.Hole hole) {
        return PvPBot.HoleType.getFromHole(hole).getVal() * this.comparePos(hole.pos1);
    }

    double comparePos(BlockPos pos) {
        return PvPBot.mc.player.getDistance((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()) + this.target.getDistance((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
    }

    public class HoleCallable implements Callable {

        public Object call() throws Exception {
            PvPBot.this.initialized = true;
            this.doThreading();
            PvPBot.this.initialized = false;
            return null;
        }

        public void doThreading() {
            while (true) {
                if (!NullUtils.nullCheck()) {
                    PvPBot.this.holes = HoleUtils.getHoles(((Number) PvPBot.this.targetRange.getValue()).doubleValue(), IMinecraft.mc.player.getPosition(), ((Boolean) PvPBot.this.doubles.getValue()).booleanValue());
                    System.out.println("aaaa");
                }
            }
        }
    }

    public static enum HoleType {

        BedrockSafe(2.0D), ObsidianUnsafe(3.0D), DoubleUnsafe(4.0D);

        double val;

        private HoleType(double val) {
            this.val = val;
        }

        public double getVal() {
            return this.val;
        }

        public static PvPBot.HoleType getFromHole(HoleUtils.Hole hole) {
            return hole.bedrock ? PvPBot.HoleType.BedrockSafe : (hole.doubleHole ? PvPBot.HoleType.DoubleUnsafe : PvPBot.HoleType.ObsidianUnsafe);
        }
    }
}
