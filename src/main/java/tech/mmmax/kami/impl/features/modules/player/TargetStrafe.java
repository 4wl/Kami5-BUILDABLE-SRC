package tech.mmmax.kami.impl.features.modules.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToDoubleFunction;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.MathUtil;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.player.PlayerUtils;
import tech.mmmax.kami.api.utils.player.TargetUtils;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class TargetStrafe extends Module {

    Value radius = (new ValueBuilder()).withDescriptor("Radius").withValue(Integer.valueOf(4)).withRange(Integer.valueOf(1), Integer.valueOf(9)).register(this);
    Value targetRange = (new ValueBuilder()).withDescriptor("Target Range").withValue(Integer.valueOf(7)).withRange(Integer.valueOf(3), Integer.valueOf(14)).register(this);
    Value lineColor = (new ValueBuilder()).withDescriptor("Line Color").withValue(new Color(255, 255, 255, 150)).register(this);
    Value fadedAlpha = (new ValueBuilder()).withDescriptor("Faded Alpha").withValue(Integer.valueOf(100)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value fadedMax = (new ValueBuilder()).withDescriptor("Faded Max").withValue(Integer.valueOf(10)).withRange(Integer.valueOf(5), Integer.valueOf(20)).register(this);
    Value targetColor = (new ValueBuilder()).withDescriptor("Target Color").withValue(new Color(255, 255, 255, 47)).register(this);
    Value lineWidth = (new ValueBuilder()).withDescriptor("Line Width").withValue(Integer.valueOf(2)).withRange(Double.valueOf(0.1D), Integer.valueOf(4)).register(this);
    List circle = new ArrayList();
    Entity target = null;

    public TargetStrafe() {
        super("Target Strafe", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if ((this.target = TargetUtils.getTarget(((Number) this.targetRange.getValue()).doubleValue())) != null) {
                if ((this.circle = this.getCircle()) != null) {
                    Vec3d closestPos = (Vec3d) this.circle.stream().min(Comparator.comparingDouble((pos) -> {
                        return TargetStrafe.mc.player.getDistance(this.target.posX + pos.x * ((Number) this.radius.getValue()).doubleValue(), this.target.posY + pos.y, this.target.posZ + pos.z * ((Number) this.radius.getValue()).doubleValue());
                    })).orElse(new Vec3d(0.0D, 0.0D, 0.0D));
                    double speed = PlayerUtils.getDefaultMoveSpeed();

                    TargetStrafe.mc.player.motionX = closestPos.x * speed;
                    TargetStrafe.mc.player.motionZ = closestPos.z * speed;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!NullUtils.nullCheck()) {
            if (this.target != null) {
                RenderUtil.renderBB(7, this.target.getRenderBoundingBox(), (Color) this.targetColor.getValue(), (Color) this.targetColor.getValue());
                if (this.circle != null) {
                    GL11.glLineWidth(((Number) this.lineWidth.getValue()).floatValue());
                    RenderUtil.prepare();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderUtil.builder = RenderUtil.tessellator.getBuffer();
                    RenderUtil.builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
                    this.buildBBBuffer(RenderUtil.builder, this.circle, (Color) this.lineColor.getValue(), ((Number) this.fadedAlpha.getValue()).intValue());
                    RenderUtil.tessellator.draw();
                    RenderUtil.release();
                    GL11.glLineWidth(1.0F);
                }
            }

        }
    }

    void buildBBBuffer(BufferBuilder bb, List positions, Color color, int fadeAlpha) {
        Iterator iterator = positions.iterator();

        while (iterator.hasNext()) {
            Vec3d pos = (Vec3d) iterator.next();
            Vec3d targetPos = (new Vec3d(pos.x * ((Number) this.radius.getValue()).doubleValue(), pos.y, pos.z * ((Number) this.radius.getValue()).doubleValue())).add(this.target.posX, this.target.posY, this.target.posZ);
            Vec3d camPos = RenderUtil.updateToCamera(targetPos);
            double normal = MathUtil.normalize(TargetStrafe.mc.player.getDistance(targetPos.x, targetPos.y, targetPos.z), 0.0D, ((Number) this.fadedMax.getValue()).doubleValue());

            normal = MathHelper.clamp(normal, 0.0D, 1.0D);
            int alpha = (int) (normal * (double) (fadeAlpha - ((Color) this.lineColor.getValue()).getAlpha()) + (double) ((Color) this.lineColor.getValue()).getAlpha());

            RenderUtil.addBuilderVertex(bb, camPos.x, camPos.y, camPos.z, ColorUtil.newAlpha(color, alpha));
        }

    }

    List getCircle() {
        ArrayList positions = new ArrayList();

        for (int i = 0; i <= 360; ++i) {
            double x = Math.sin(Math.toRadians((double) i));
            double z = -Math.cos(Math.toRadians((double) i));

            positions.add(new Vec3d(x, 0.0D, z));
        }

        return positions;
    }
}
