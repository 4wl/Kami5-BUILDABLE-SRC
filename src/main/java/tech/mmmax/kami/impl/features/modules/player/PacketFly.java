package tech.mmmax.kami.impl.features.modules.player;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.player.PlayerUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.mixin.mixins.access.ISPacketPlayerPosLook;

public class PacketFly extends Module {

    public static PacketFly INSTANCE;
    Value phaseMode = (new ValueBuilder()).withDescriptor("Phase Mode").withValue("NoClip").withModes(new String[] { "NoClip", "Sand", "Packet", "Skip"}).register(this);
    Value tpAccept = (new ValueBuilder()).withDescriptor("TP Accept", "tpAccept").withValue(Boolean.valueOf(true)).register(this);
    Value speed = (new ValueBuilder()).withDescriptor("Speed").withValue(Double.valueOf(0.03D)).withRange(Double.valueOf(0.01D), Double.valueOf(0.1D)).register(this);
    Value jitterAmount = (new ValueBuilder()).withDescriptor("Jitter Amount").withValue(Double.valueOf(1.0D)).withRange(Double.valueOf(0.0D), Double.valueOf(3.0D)).register(this);
    Value bound = (new ValueBuilder()).withDescriptor("Bound").withValue(Boolean.valueOf(true)).register(this);
    Value boundAmount = (new ValueBuilder()).withDescriptor("Bound Amount").withValue(Integer.valueOf(-1000)).withRange(Integer.valueOf(-3000), Integer.valueOf(3000)).register(this);
    Value cancelPacket = (new ValueBuilder()).withDescriptor("Cancel Packet").withValue(Boolean.valueOf(true)).register(this);
    Value noRubberband = (new ValueBuilder()).withDescriptor("No Rubberband").withValue(Boolean.valueOf(true)).register(this);
    Value edgeEnable = (new ValueBuilder()).withDescriptor("Edge Enable").withValue(Boolean.valueOf(false)).register(this);
    Value slide = (new ValueBuilder()).withDescriptor("Slide").withValue(Double.valueOf(0.5D)).withRange(Integer.valueOf(0), Integer.valueOf(1)).register(this);
    Value debug = (new ValueBuilder()).withDescriptor("Debug").withValue(Boolean.valueOf(false)).register(this);
    boolean cancelling = true;
    int teleportId;
    List packets = new ArrayList();

    public PacketFly() {
        super("PacketFly", Feature.Category.Player);
        PacketFly.INSTANCE = this;
    }

    public void onDisable() {
        super.onDisable();
        if (!NullUtils.nullCheck()) {
            if (((String) this.phaseMode.getValue()).equalsIgnoreCase("NoClip")) {
                PacketFly.mc.player.noClip = false;
            }

        }
    }

    public void onEnable() {
        super.onEnable();
        if (!NullUtils.nullCheck()) {
            if (((String) this.phaseMode.getValue()).equalsIgnoreCase("NoClip")) {
                PacketFly.mc.player.noClip = true;
            }

            this.teleportId = 0;
        }
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (this.shouldFly()) {
                this.cancelling = false;
                double[] forward = PlayerUtils.forward(this.getSpeed() * this.getJitter());
                double up = PacketFly.mc.gameSettings.keyBindJump.isKeyDown() ? 0.0233D : (PacketFly.mc.gameSettings.keyBindSneak.isKeyDown() ? -0.0233D : 1.0E-6D);
                double[] playerPos = this.toPlayerPos(forward[0], up, forward[1]);

                PacketFly.mc.player.setVelocity(forward[0], up, forward[1]);
                Position packetPlayer = new Position(playerPos[0], playerPos[1], playerPos[2], PacketFly.mc.player.onGround);

                PacketFly.mc.player.connection.sendPacket(packetPlayer);
                this.packets.add(packetPlayer);
                PacketFly.mc.player.setPosition(playerPos[0], playerPos[1], playerPos[2]);
                if (((Boolean) this.bound.getValue()).booleanValue()) {
                    Position bounds = new Position(playerPos[0], ((Number) this.boundAmount.getValue()).doubleValue(), playerPos[2], PacketFly.mc.player.onGround);

                    PacketFly.mc.player.connection.sendPacket(bounds);
                    this.packets.add(bounds);
                }

                ++this.teleportId;
                if (((Boolean) this.tpAccept.getValue()).booleanValue()) {
                    PacketFly.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId - 1));
                    PacketFly.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
                    PacketFly.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId + 1));
                }

                this.cancelling = true;
            }

        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getTime() == PacketEvent.Time.Send) {
                if (event.getPacket() instanceof Position && ((Boolean) this.cancelPacket.getValue()).booleanValue()) {
                    Position packet = (Position) event.getPacket();

                    if (this.cancelling) {
                        event.setCanceled(true);
                    }
                }

                if (event.getPacket() instanceof CPacketConfirmTeleport) {
                    CPacketConfirmTeleport packet1 = (CPacketConfirmTeleport) event.getPacket();
                }
            }

            if (event.getTime() == PacketEvent.Time.Receive && event.getPacket() instanceof SPacketPlayerPosLook && ((Boolean) this.noRubberband.getValue()).booleanValue()) {
                SPacketPlayerPosLook packet2 = (SPacketPlayerPosLook) event.getPacket();
                ISPacketPlayerPosLook inter = (ISPacketPlayerPosLook) packet2;

                if (PacketFly.mc.player.isEntityAlive() && PacketFly.mc.world.isBlockLoaded(new BlockPos(PacketFly.mc.player.posX, PacketFly.mc.player.posY, PacketFly.mc.player.posZ)) && !(PacketFly.mc.currentScreen instanceof GuiDownloadTerrain)) {
                    if (this.teleportId <= 0) {
                        this.teleportId = packet2.getTeleportId();
                    } else {
                        inter.setX(MathHelper.clampedLerp(Math.min(PacketFly.mc.player.posX, packet2.getX()), Math.max(PacketFly.mc.player.posX, packet2.getX()), ((Number) this.slide.getValue()).doubleValue()));
                        inter.setY(MathHelper.clampedLerp(Math.min(PacketFly.mc.player.getEntityBoundingBox().minY, packet2.getY()), Math.max(PacketFly.mc.player.getEntityBoundingBox().minY, packet2.getY()), ((Number) this.slide.getValue()).doubleValue()));
                        inter.setZ(MathHelper.clampedLerp(Math.min(PacketFly.mc.player.posZ, packet2.getZ()), Math.max(PacketFly.mc.player.posZ, packet2.getZ()), ((Number) this.slide.getValue()).doubleValue()));
                    }
                }
            }

        }
    }

    public void move() {}

    double getSpeed() {
        return !PacketFly.mc.gameSettings.keyBindJump.isKeyDown() && !PacketFly.mc.gameSettings.keyBindSneak.isKeyDown() ? ((Number) this.speed.getValue()).doubleValue() : 0.0D;
    }

    double[] toPlayerPos(double x, double y, double z) {
        return new double[] { PacketFly.mc.player.posX + x, PacketFly.mc.player.posY + y, PacketFly.mc.player.posZ + z};
    }

    double getJitter() {
        return Math.floor(Math.random() * (((Number) this.jitterAmount.getValue()).doubleValue() - ((Number) this.jitterAmount.getMin()).doubleValue() + 1.0D) + ((Number) this.jitterAmount.getMin()).doubleValue());
    }

    boolean isPhasing() {
        return PacketFly.mc.world.getBlockState(PacketFly.mc.player.getPosition()).getMaterial().isReplaceable() || PacketFly.mc.world.getBlockState(PacketFly.mc.player.getPosition().add(0, 1, 0)).getMaterial().isReplaceable();
    }

    boolean isOnEdge() {
        boolean verticalFlying = PacketFly.mc.gameSettings.keyBindJump.isKeyDown() || PacketFly.mc.gameSettings.keyBindSneak.isKeyDown();

        return PacketFly.mc.player.collidedHorizontally || verticalFlying;
    }

    boolean shouldFly() {
        return !((Boolean) this.edgeEnable.getValue()).booleanValue() || this.isOnEdge();
    }

    public void doFly(double x, double y, double z, boolean onGround) {
        Position pos = new Position(PacketFly.mc.player.posX + x, PacketFly.mc.player.posY + y, PacketFly.mc.player.posZ + z, onGround);

        this.packets.add(pos);
        PacketFly.mc.player.connection.sendPacket(pos);
        ++this.teleportId;
        if (((Boolean) this.tpAccept.getValue()).booleanValue()) {
            PacketFly.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId - 1));
            PacketFly.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
            PacketFly.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId + 1));
        }

    }
}
