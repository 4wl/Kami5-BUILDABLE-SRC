package tech.mmmax.kami.impl.features.modules.render;

import com.mojang.authlib.GameProfile;
import java.awt.Color;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class RubberESP extends Module {

    Value fillColorSet = (new ValueBuilder()).withDescriptor("Fill Color").withValue(new Color(47, 0, 255, 150)).register(this);
    Value lineColorSet = (new ValueBuilder()).withDescriptor("Line Color").withValue(new Color(255, 255, 255, 255)).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Box").withModes(new String[] { "Box", "Model"}).register(this);
    Value fadeStart = (new ValueBuilder()).withDescriptor("Fade Start").withValue(Integer.valueOf(100)).withRange(Integer.valueOf(0), Integer.valueOf(4000)).register(this);
    Value fadeTime = (new ValueBuilder()).withDescriptor("Fade Time").withValue(Integer.valueOf(500)).withRange(Integer.valueOf(0), Integer.valueOf(2000)).register(this);
    EntityOtherPlayerMP player = null;
    AxisAlignedBB playerBox = null;
    ModelPlayer playerModel = null;
    long startTime = 0L;

    public RubberESP() {
        super("RubberESP", Feature.Category.Render);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();

                if (((String) this.mode.getValue()).equals("Box")) {
                    AxisAlignedBB profile = RubberESP.mc.player.getRenderBoundingBox();
                    double widthX = (profile.maxX - profile.minX) / 2.0D;
                    double height = profile.maxY - profile.minY;
                    double widthZ = (profile.maxZ - profile.minZ) / 2.0D;

                    this.playerBox = new AxisAlignedBB(packet.getX() - widthX, packet.getY(), packet.getZ() - widthZ, packet.getX() + widthX, packet.getY() + height, packet.getZ() + widthZ);
                    this.player = null;
                }

                if (((String) this.mode.getValue()).equals("Model")) {
                    GameProfile profile1 = new GameProfile(RubberESP.mc.player.getUniqueID(), "");

                    this.player = new EntityOtherPlayerMP(RubberESP.mc.world, profile1);
                    this.player.setPositionAndRotation(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
                    this.playerModel = new ModelPlayer(0.0F, false);
                    this.playerBox = null;
                }

                this.startTime = System.currentTimeMillis();
            }

        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        GL11.glLineWidth(1.0F);
        int lineA = ((Color) this.lineColorSet.getValue()).getAlpha();
        int fillA = ((Color) this.fillColorSet.getValue()).getAlpha();

        if (System.currentTimeMillis() - this.startTime > ((Number) this.fadeStart.getValue()).longValue()) {
            long lineColor = System.currentTimeMillis() - this.startTime - ((Number) this.fadeStart.getValue()).longValue();
            double normal = this.normalize((double) lineColor, 0.0D, ((Number) this.fadeTime.getValue()).doubleValue());

            normal = MathHelper.clamp(normal, 0.0D, 1.0D);
            normal = -normal + 1.0D;
            lineA = (int) (normal * (double) lineA);
            fillA = (int) (normal * (double) fillA);
        }

        Color lineColor1 = ColorUtil.newAlpha((Color) this.lineColorSet.getValue(), lineA);
        Color fillColor = ColorUtil.newAlpha((Color) this.fillColorSet.getValue(), fillA);

        if (this.playerBox != null && ((String) this.mode.getValue()).equals("Box")) {
            RenderUtil.renderBB(7, this.playerBox, fillColor, fillColor);
            RenderUtil.renderBB(3, this.playerBox, lineColor1, lineColor1);
        }

        if (this.player != null && ((String) this.mode.getValue()).equals("Model")) {
            RenderUtil.prepare();
            ColorUtil.glColor(fillColor);
            GL11.glPolygonMode(1032, 6914);
            RenderUtil.renderEntity(this.player, this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, (float) this.player.ticksExisted, this.player.rotationYawHead, this.player.rotationPitch, 1.0F);
            ColorUtil.glColor(lineColor1);
            GL11.glPolygonMode(1032, 6913);
            RenderUtil.renderEntity(this.player, this.playerModel, this.player.limbSwing, this.player.limbSwingAmount, (float) this.player.ticksExisted, this.player.rotationYawHead, this.player.rotationPitch, 1.0F);
            GL11.glPolygonMode(1032, 6914);
            RenderUtil.release();
        }

    }

    double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}
