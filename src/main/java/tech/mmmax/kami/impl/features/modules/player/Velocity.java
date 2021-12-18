package tech.mmmax.kami.impl.features.modules.player;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.event.PushOutOfBlocksEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.mixin.mixins.access.ISPacketEntityVelocity;
import tech.mmmax.kami.mixin.mixins.access.ISPacketExplosion;

public class Velocity extends Module {

    Value cancelV = (new ValueBuilder()).withDescriptor("Cancel V").withValue(Boolean.valueOf(false)).register(this);
    Value cancelE = (new ValueBuilder()).withDescriptor("Cancel E").withValue(Boolean.valueOf(false)).register(this);
    Value horizontal = (new ValueBuilder()).withDescriptor("Horizontal").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(0), Integer.valueOf(100)).register(this);
    Value vertical = (new ValueBuilder()).withDescriptor("Vertical").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(0), Integer.valueOf(100)).register(this);
    Value noSlow = (new ValueBuilder()).withDescriptor("No Slow").withValue(Boolean.valueOf(true)).register(this);

    public Velocity() {
        super("Velocity", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == Velocity.mc.player.getEntityId()) {
                SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();
                ISPacketEntityVelocity inter = (ISPacketEntityVelocity) packet;

                if (((Boolean) this.cancelV.getValue()).booleanValue()) {
                    event.setCanceled(true);
                    return;
                }

                inter.setMotionX(packet.getMotionX() * ((Number) this.horizontal.getValue()).intValue());
                inter.setMotionY(packet.getMotionY() * ((Number) this.vertical.getValue()).intValue());
                inter.setMotionZ(packet.getMotionZ() * ((Number) this.horizontal.getValue()).intValue());
            }

            if (event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion packet1 = (SPacketExplosion) event.getPacket();
                ISPacketExplosion inter1 = (ISPacketExplosion) packet1;

                if (((Boolean) this.cancelE.getValue()).booleanValue()) {
                    event.setCanceled(true);
                    return;
                }

                inter1.setMotionX(packet1.getMotionX() * (float) ((Number) this.horizontal.getValue()).intValue());
                inter1.setMotionY(packet1.getMotionY() * (float) ((Number) this.vertical.getValue()).intValue());
                inter1.setMotionZ(packet1.getMotionZ() * (float) ((Number) this.horizontal.getValue()).intValue());
            }

        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (((Boolean) this.noSlow.getValue()).booleanValue() && Velocity.mc.player.isHandActive() && !Velocity.mc.player.isRiding()) {
            MovementInput movementinput = event.getMovementInput();

            movementinput.moveStrafe *= 5.0F;
            movementinput = event.getMovementInput();
            movementinput.moveForward *= 5.0F;
        }

    }

    @SubscribeEvent
    public void onPushOutOfBlocks(PushOutOfBlocksEvent event) {
        event.setCanceled(true);
    }
}
