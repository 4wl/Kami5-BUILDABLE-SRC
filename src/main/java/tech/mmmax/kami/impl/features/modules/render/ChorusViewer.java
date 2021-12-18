package tech.mmmax.kami.impl.features.modules.render;

import java.awt.Color;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.MathUtil;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class ChorusViewer extends Module {

    Value lineWidth = (new ValueBuilder()).withDescriptor("Line Width").withValue(Integer.valueOf(1)).withRange(Double.valueOf(0.1D), Integer.valueOf(5)).register(this);
    Value fillColor = (new ValueBuilder()).withDescriptor("Fill Color").withValue(new Color(255, 255, 255, 20)).register(this);
    Value outlineColor = (new ValueBuilder()).withDescriptor("Outline Color").withValue(new Color(255, 255, 255, 255)).register(this);
    Value tracer = (new ValueBuilder()).withDescriptor("Tracer").withValue(Boolean.valueOf(true)).register(this);
    Value tracerColor = (new ValueBuilder()).withDescriptor("Tracer Color").withValue(new Color(255, 255, 255, 150)).register(this);
    Value tracerFadeTime = (new ValueBuilder()).withDescriptor("Tracer Fade").withValue(Integer.valueOf(500)).withRange(Integer.valueOf(1), Integer.valueOf(2000)).register(this);
    Value fadeStart = (new ValueBuilder()).withDescriptor("Fade Start").withValue(Integer.valueOf(500)).withRange(Integer.valueOf(1), Integer.valueOf(2000)).register(this);
    Value fadeTime = (new ValueBuilder()).withDescriptor("Fade Time").withValue(Integer.valueOf(500)).withRange(Integer.valueOf(1), Integer.valueOf(2000)).register(this);
    AxisAlignedBB renderBB;
    long startTime;

    public ChorusViewer() {
        super("ChorusViewer", Feature.Category.Render);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (this.renderBB != null) {
            GL11.glLineWidth(((Number) this.lineWidth.getValue()).floatValue());
            int lineA = ((Color) this.outlineColor.getValue()).getAlpha();
            int fillA = ((Color) this.fillColor.getValue()).getAlpha();
            int tracerA = ((Color) this.tracerColor.getValue()).getAlpha();
            long time;
            double normal;

            if (System.currentTimeMillis() - this.startTime > ((Number) this.fadeStart.getValue()).longValue()) {
                time = System.currentTimeMillis() - this.startTime - ((Number) this.fadeStart.getValue()).longValue();
                normal = MathUtil.normalize((double) time, 0.0D, ((Number) this.fadeTime.getValue()).doubleValue());
                normal = MathHelper.clamp(normal, 0.0D, 1.0D);
                normal = -normal + 1.0D;
                lineA = (int) (normal * (double) lineA);
                fillA = (int) (normal * (double) fillA);
            }

            time = System.currentTimeMillis() - this.startTime;
            normal = MathUtil.normalize((double) time, 0.0D, ((Number) this.tracerFadeTime.getValue()).doubleValue());
            normal = MathHelper.clamp(normal, 0.0D, 1.0D);
            normal = -normal + 1.0D;
            tracerA = (int) (normal * (double) lineA);
            Color fill = ColorUtil.newAlpha((Color) this.fillColor.getValue(), fillA);
            Color line = ColorUtil.newAlpha((Color) this.outlineColor.getValue(), lineA);
            Color tracerC = ColorUtil.newAlpha((Color) this.tracerColor.getValue(), tracerA);

            RenderUtil.prepare();
            RenderUtil.renderBB(7, this.renderBB, fill, fill);
            RenderUtil.renderBB(3, this.renderBB, line, line);
            if (((Boolean) this.tracer.getValue()).booleanValue()) {
                double centerX = (this.renderBB.maxX - this.renderBB.minX) / 2.0D;
                double centerZ = (this.renderBB.maxZ - this.renderBB.minZ) / 2.0D;
                Vec3d startPos = RenderUtil.updateToCamera(ChorusViewer.mc.player.getPositionVector());
                Vec3d endPos = RenderUtil.updateToCamera(new Vec3d(this.renderBB.minX + centerX, this.renderBB.minY, this.renderBB.minZ + centerZ));

                if ((new Frustum()).isBoundingBoxInFrustum(this.renderBB)) {
                    RenderUtil.builder = RenderUtil.tessellator.getBuffer();
                    RenderUtil.builder.begin(1, DefaultVertexFormats.POSITION_COLOR);
                    RenderUtil.addBuilderVertex(RenderUtil.builder, startPos.x, startPos.y, startPos.z, tracerC);
                    RenderUtil.addBuilderVertex(RenderUtil.builder, endPos.x, endPos.y, endPos.z, tracerC);
                    RenderUtil.tessellator.draw();
                }
            }

            RenderUtil.release();
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();

            if (packet.getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT || packet.getSound() == SoundEvents.ENTITY_ENDERMEN_TELEPORT) {
                AxisAlignedBB playerBB = ChorusViewer.mc.player.getRenderBoundingBox();
                double widthX = (playerBB.maxX - playerBB.minX) / 2.0D;
                double height = playerBB.maxY - playerBB.minY;
                double widthZ = (playerBB.maxZ - playerBB.minZ) / 2.0D;

                this.renderBB = new AxisAlignedBB(packet.getX() - widthX, packet.getY(), packet.getZ() - widthZ, packet.getX() + widthX, packet.getY() + height, packet.getZ() + widthZ);
                this.startTime = System.currentTimeMillis();
            }
        }

    }
}
